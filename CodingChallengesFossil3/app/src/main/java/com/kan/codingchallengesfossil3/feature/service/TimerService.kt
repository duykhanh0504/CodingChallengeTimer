package com.kan.codingchallengesfossil3.feature.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.extension.config
import com.kan.codingchallengesfossil3.extension.getFormattedDuration
import com.kan.codingchallengesfossil3.extension.getOpenTimerTabIntent
import com.kan.codingchallengesfossil3.model.StateEvent
import com.kan.codingchallengesfossil3.utils.CHANNEL_ID
import com.kan.codingchallengesfossil3.utils.TIMER_NOTIF_ID
import com.kan.codingchallengesfossil3.utils.TIMER_RUNNING_NOTIF_ID
import com.kan.codingchallengesfossil3.utils.isOreoPlus
import kotlinx.android.synthetic.main.fragment_picker.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class TimerService : Service() {

    private val eventBus = EventBus.getDefault()

    override fun onCreate() {
        super.onCreate()
        eventBus.register(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val formattedDuration = config.timerSeconds.getFormattedDuration()
        startForeground(TIMER_RUNNING_NOTIF_ID, notification(formattedDuration))

        return START_NOT_STICKY
    }

    private fun stopService() {
        if (isOreoPlus()) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventBus.unregister(this)
    }

    private fun updateNotification(text: String) {
        val notification: Notification = notification(text)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(TIMER_RUNNING_NOTIF_ID, notification)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageStopService(event: TimerStopService) {
        stopService()
    }

    @Subscribe
    fun onMessageEvent(state: StateEvent.Running) {
        updateNotification(state.tick.div(1000F).roundToInt().getFormattedDuration())
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun notification(formattedDuration: String): Notification {
        val channelId = CHANNEL_ID
        val label = getString(R.string.timer)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (isOreoPlus()) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelId, label, importance).apply {
                setSound(null, null)
                notificationManager.createNotificationChannel(this)
            }
        }

        val builder = NotificationCompat.Builder(this)
            .setContentTitle(label)
            .setContentText(formattedDuration)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentIntent(this.getOpenTimerTabIntent())
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setSound(null)
            .setOngoing(true)
            .setAutoCancel(true)
            .setChannelId(channelId)

        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        return builder.build()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun startTimerService(context: Context) {
    if (isOreoPlus()) {
        context.startForegroundService(Intent(context, TimerService::class.java))
    } else {
        context.startService(Intent(context, TimerService::class.java))
    }
}

object TimerStopService
