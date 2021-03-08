package com.kan.codingchallengesfossil3.feature.main.timer

import android.os.Bundle
import android.widget.NumberPicker
import androidx.lifecycle.ViewModelProvider
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.base.BaseFragment
import com.kan.codingchallengesfossil3.extension.*
import com.kan.codingchallengesfossil3.feature.main.MainComponent
import com.kan.codingchallengesfossil3.feature.main.MainViewModel
import com.kan.codingchallengesfossil3.feature.navigation.Navigator
import com.kan.codingchallengesfossil3.model.StateEvent
import kotlinx.android.synthetic.main.fragment_picker.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject
import kotlin.math.roundToInt
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
        fun newInstance(): TimerFragment =
            TimerFragment()
    }

    override fun layoutId() = R.layout.fragment_picker

    override fun inject() = getComponent(MainComponent::class.java)?.inject(this) ?: Unit

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var navigator: Navigator

    private lateinit var mainViewModel: MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()
        initView()
        setListener()
    }

    private fun initViewModel() {
        mainViewModel = this.viewModel(factory = viewModelFactory) {
            observe(currentTime, ::handleUpdateCurrentTimer)
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

    private fun handleUpdateCurrentTimer(data: String?) {
        timerTime.text = data
    }

    private fun setListener() {

        btnStop.setOnSafeClickListener {
            stopTimer()
            btnStart.isSelected = false
        }

        btnStart.setOnSafeClickListener {
            btnStart.isSelected = !it.isSelected
            mainViewModel.currentState = requireActivity().config.timerState
            val hours = numberPickerHour.value
            val minutes = numberPickerMinute.value
            val seconds = numberPickerSecond.value
            val totalSecond = (hours * 3600 + minutes * 60 + seconds).toLong()
            requireActivity().config.timerSeconds = totalSecond
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
            }
            is StateEvent.Idle -> {
                rlContentCalendar.visible()
                btnStop.invisible()
            }

            is StateEvent.Paused -> {
                rlContentCalendar.invisible()
                btnStop.visible()
            }

            is StateEvent.Finish -> {
                rlContentCalendar.visible()
                btnStop.invisible()
            }

            is StateEvent.Finished -> {
                rlContentCalendar.invisible()
                btnStop.invisible()
            }
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
        updateViewStates(state)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(state: StateEvent.Finish) {
        timerTime.text = 0.getFormattedDuration()
        circularProgressBar.progress = 0f
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