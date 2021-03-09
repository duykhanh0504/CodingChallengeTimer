package com.kan.codingchallengesfossil3.feature.main.timer

import android.os.Bundle
import android.widget.NumberPicker
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.base.BaseFragment
import com.kan.codingchallengesfossil3.data.model.TimerModel
import com.kan.codingchallengesfossil3.extension.*
import com.kan.codingchallengesfossil3.feature.dialog.DialogTimerPicker
import com.kan.codingchallengesfossil3.feature.main.MainActivity
import com.kan.codingchallengesfossil3.feature.main.MainComponent
import com.kan.codingchallengesfossil3.feature.main.MainViewModel
import com.kan.codingchallengesfossil3.feature.navigation.Navigator
import com.kan.codingchallengesfossil3.model.StateEvent
import com.kan.codingchallengesfossil3.utils.ResourceUtil
import kotlinx.android.synthetic.main.fragment_timer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.math.roundToLong


/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class TimerFragment : BaseFragment() {

    companion object {
        const val MIN_PICKTIME = 0
        const val MAX_PICKTIME = 99
        const val FORMAT_DIGIT = "%02d"
        const val TIME_DURATION = 1000L
        const val MAX_SETUP = 100000
        fun newInstance(): TimerFragment =
            TimerFragment()
    }

    override fun layoutId() = R.layout.fragment_timer

    override fun inject() = getComponent(MainComponent::class.java)?.inject(this) ?: Unit

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigator: Navigator

    private lateinit var mainViewModel: MainViewModel

    private lateinit var timerAdapter: TimerAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
        initRecyclerView()
        setListener()
        mainViewModel.getAllTimer()
    }

    private fun initViewModel() {
        mainViewModel = this.viewModel(factory = viewModelFactory) {
            observe(currentTime, ::handleUpdateCurrentTimer)
            observe(listTimerSetup, ::handleTimesetup)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerEventBus()
    }

    override fun onDestroy() {
        unregisterEventBus()
        super.onDestroy()
    }

    fun initRecyclerView() {
        timerAdapter = TimerAdapter().apply {
            onItemClick = ::handleItemClick
        }

        recycleView.apply {
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = timerAdapter
        }
    }

    private fun handleItemClick(data: TimerModel) {
        data.timerSecond?.also {
            updateData(it)
        }
    }

    private fun updateData(timer: Long) {
        numberPickerHour.value = (timer / 3600).toInt()
        numberPickerMinute.value = (timer / 60 % 60).toInt()
        numberPickerSecond.value = (timer % 60).toInt()
        mainViewModel.totalSecondTime = timer
        requireActivity().config.timerSeconds = timer
    }

    private fun handleTimesetup(data: List<TimerModel>?) {
        data?.also {
            timerAdapter.submitList(it)
        }
    }

    private fun handleUpdateCurrentTimer(data: String?) {
        timerTime.text = data
    }

    private fun setListener() {

        btnList.setOnSafeClickListener {
            DialogTimerPicker(activity as MainActivity, mainViewModel.totalSecondTime) { seconds ->
                if (mainViewModel.listTimerSetup.value?.firstOrNull {
                        it.timerSecond == seconds
                    } == null) {
                    val id = mainViewModel.listTimerSetup.value?.maxBy { it.id }?.id ?: 0
                    mainViewModel.insertTimer(TimerModel((id + 1), seconds, ""))
                }
                updateData(seconds)
            }
        }

        btnStop.setOnSafeClickListener {
            stopTimer()
            btnStart.isChecked = false
        }

        btnStart.setOnSafeClickListener {
            btnStart.isChecked = btnStart.isChecked
            mainViewModel.currentState = requireActivity().config.timerState
            val hours = numberPickerHour.value
            val minutes = numberPickerMinute.value
            val seconds = numberPickerSecond.value
            val totalSecond = (hours * 3600 + minutes * 60 + seconds).toLong()
            requireActivity().config.timerSeconds = totalSecond
            totalTime.text = ResourceUtil.getString(R.string.total, totalSecond)
            mainViewModel.totalSecondTime = totalSecond

            when (mainViewModel.currentState) {
                is StateEvent.Idle -> {
                    mainViewModel.totalSecondTime
                    eventBus
                        ?.post(StateEvent.Start(mainViewModel.totalSecondTime.secondsToMillis))
                }
                is StateEvent.Paused -> eventBus?.post(StateEvent.Start((mainViewModel.currentState as StateEvent.Paused).tick))
                is StateEvent.Running -> eventBus?.post(StateEvent.Pause((mainViewModel.currentState as StateEvent.Running).tick))
                is StateEvent.Finished -> eventBus?.post(StateEvent.Start(mainViewModel.totalSecondTime.secondsToMillis))
                else -> {
                    Unit
                }
            }
        }
    }


    private fun updateViewStates(state: StateEvent) {
        when (state) {
            is StateEvent.Running -> {
                rlContentCalendar.invisible()
                btnStop.visible()
                llTimerContent.visible()
                recycleView.gone()
            }
            is StateEvent.Idle -> {
                rlContentCalendar.visible()
                btnStop.invisible()
                llTimerContent.invisible()
                btnStart.isChecked = false
                recycleView.visible()
            }

            is StateEvent.Paused -> {
                rlContentCalendar.invisible()
                btnStop.visible()
                llTimerContent.visible()
                recycleView.gone()
                btnStart.isChecked = true
                mainViewModel.updateTimer(state.tick.div(1000F).roundToLong())
                totalTime.text =
                    ResourceUtil.getString(R.string.total, mainViewModel.totalSecondTime)
                circularProgressBar.setProgressWithAnimation(
                    state.tick.div(requireActivity().config.timerSeconds.secondsToMillis.toFloat()) * 100,
                    1000
                )
            }

            is StateEvent.Finish -> {
                rlContentCalendar.invisible()
                btnStop.invisible()
                recycleView.gone()
                llTimerContent.visible()
            }

            is StateEvent.Finished -> {
                rlContentCalendar.visible()
                llTimerContent.visible()
                recycleView.visible()
                btnStop.invisible()
            }
            else -> Unit
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: StateEvent.Idle) {
        mainViewModel.updateTimer(0L)
        circularProgressBar.setProgressWithAnimation(100f, 1000)
        updateViewStates(state)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: StateEvent.Running) {
        mainViewModel.updateTimer(state.tick.div(1000F).roundToLong())
        circularProgressBar.setProgressWithAnimation(
            state.tick.div(requireActivity().config.timerSeconds.secondsToMillis.toFloat()) * 100,
            1000
        )
        updateViewStates(state)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: StateEvent.Paused) {
        updateViewStates(state)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: StateEvent.Finished) {
        timerTime.text = 0.getFormattedDuration()
        circularProgressBar.progress = 0f
        GlobalScope.launch(Dispatchers.Main) {
            delay(TIME_DURATION)
            EventBus.getDefault().post(StateEvent.Idle)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: StateEvent.Finish) {
        timerTime.text = 0.getFormattedDuration()
        circularProgressBar.setProgressWithAnimation(0f, 500L)
        updateViewStates(state)
    }

    private fun stopTimer() {
        eventBus?.post(StateEvent.Idle)
        activity?.hideTimerNotification()
    }

    private fun initView() {
        numberPickerHour.minValue =
            MIN_PICKTIME
        numberPickerHour.maxValue =
            MAX_PICKTIME
        numberPickerMinute.minValue =
            MIN_PICKTIME
        numberPickerMinute.maxValue =
            MAX_PICKTIME
        numberPickerSecond.minValue =
            MIN_PICKTIME
        numberPickerSecond.maxValue =
            MAX_PICKTIME

        numberPickerHour.setFormatter(NumberPicker.Formatter { i ->
            String.format(
                FORMAT_DIGIT,
                i
            )
        })
        numberPickerMinute.setFormatter(NumberPicker.Formatter { i ->
            String.format(
                FORMAT_DIGIT,
                i
            )
        })
        numberPickerSecond.setFormatter(NumberPicker.Formatter { i ->
            String.format(
                FORMAT_DIGIT,
                i
            )
        })

        numberPickerHour.value = (requireActivity().config.timerSeconds / 3600).toInt()
        numberPickerMinute.value = ((requireActivity().config.timerSeconds) / 60 % 60).toInt()
        numberPickerSecond.value = (requireActivity().config.timerSeconds % 60).toInt()
        mainViewModel.totalSecondTime = requireActivity().config.timerSeconds
        mainViewModel.currentState = requireActivity().config.timerState

        updateViewStates(mainViewModel.currentState)
    }


}