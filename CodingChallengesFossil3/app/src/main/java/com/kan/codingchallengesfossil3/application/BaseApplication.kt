package com.kan.codingchallengesfossil3.application

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.kan.codingchallengesfossil3.di.ApplicationComponent
import com.kan.codingchallengesfossil3.di.ApplicationModule
import com.kan.codingchallengesfossil3.di.DaggerApplicationComponent
import com.kan.codingchallengesfossil3.extension.config
import com.kan.codingchallengesfossil3.extension.getOpenTimerTabIntent
import com.kan.codingchallengesfossil3.extension.getTimerNotification
import com.kan.codingchallengesfossil3.feature.service.TimerStopService
import com.kan.codingchallengesfossil3.feature.service.startTimerService
import com.kan.codingchallengesfossil3.model.StateEvent
import com.kan.codingchallengesfossil3.utils.TIMER_NOTIF_ID
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */
class BaseApplication : Application(), LifecycleObserver {

    companion object {
        lateinit var INSTANCE: BaseApplication private set
    }


    private var timer: CountDownTimer? = null
    private var lastTick = 0L

    val applicationComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        applicationComponent.getEventBus().register(this)
    }

    override fun onTerminate() {
        applicationComponent.getEventBus().unregister(this)
        super.onTerminate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onAppBackgrounded() {
        if (config.timerState is StateEvent.Running) {
            startTimerService(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onAppForegrounded() {
        EventBus.getDefault().post(TimerStopService)
    }

    fun handleIdle(state: StateEvent.Idle) {
        config.timerState = state
        timer?.cancel()
    }

    fun handleStart(state: StateEvent.Start) {
        timer = object : CountDownTimer(state.duration, 1000) {
            override fun onTick(tick: Long) {
                lastTick = tick

                val newState = StateEvent.Running(state.duration, tick)
                EventBus.getDefault().post(newState)
                config.timerState = newState
            }

            override fun onFinish() {
                EventBus.getDefault().post(StateEvent.Finish(state.duration))
                EventBus.getDefault().post(TimerStopService)
            }
        }.start()
    }

    fun handleFinish(event: StateEvent.Finish) {
        val pendingIntent = getOpenTimerTabIntent()
        val notification = getTimerNotification(pendingIntent, false)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(TIMER_NOTIF_ID, notification)

        EventBus.getDefault().post(StateEvent.Finished)
    }

    fun handleFinished(state: StateEvent.Finished) {
        config.timerState = state
    }

    fun handlePause(event: StateEvent.Pause) {
        EventBus.getDefault().post(StateEvent.Paused(event.duration, lastTick))
    }

    fun handlePaused(state: StateEvent.Paused) {
        config.timerState = state
        timer?.cancel()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleMessageEvent(event: StateEvent) = when (event) {
        is StateEvent.Idle -> {
            handleIdle(state = event)
        }
        is StateEvent.Start -> {
            handleStart(event)
        }
        is StateEvent.Finish -> {
            handleFinish(event)
        }
        is StateEvent.Finished -> {
            handleFinished(event)
        }
        is StateEvent.Pause -> {
            handlePause(event)
        }
        is StateEvent.Paused -> {
            handlePaused(event)
        }
        else -> {
            Unit
        }
    }
}