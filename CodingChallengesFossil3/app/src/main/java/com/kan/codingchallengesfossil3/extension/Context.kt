package com.kan.codingchallengesfossil3.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.feature.main.MainActivity
import com.kan.codingchallengesfossil3.feature.receiver.TimerReceiver
import com.kan.codingchallengesfossil3.model.AlarmSound
import com.kan.codingchallengesfossil3.utils.*
import com.kan.codingchallengesfossil3.utils.ResourceUtil.getDefaultAlarmUri

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

val Context.config: Config get() = Config.newInstance()

fun Context.getOpenTimerTabIntent(): PendingIntent {
    val intent = Intent(this, MainActivity::class.java)
    return PendingIntent.getActivity(
        this,
        TIMER_NOTIF_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

fun Context.hideNotification(id: Int) {
    val manager =
        applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.cancel(id)
}

fun Context.grantReadUriPermission(uriString: String) {
    try {
        grantUriPermission(
            "com.android.systemui",
            Uri.parse(uriString),
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (ignored: Exception) {
    }
}

fun Context.getHideTimerPendingIntent(): PendingIntent {
    val intent = Intent(this, TimerReceiver::class.java)
    return PendingIntent.getBroadcast(this, TIMER_NOTIF_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
}

fun Context.getDefaultAlarmSound(type: Int) = AlarmSound(0, getDefaultAlarmTitle(type), getDefaultAlarmUri(type).toString())

fun Context.getDefaultAlarmTitle(type: Int): String {
    val alarmString = getString(R.string.alarm)
    return try {
        RingtoneManager.getRingtone(this, getDefaultAlarmUri(type))?.getTitle(this) ?: alarmString
    } catch (e: Exception) {
        alarmString
    }
}


fun Context.getPermissionString(id: Int) = when (id) {
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    else -> ""
}

fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(this, getPermissionString(permId)) == PackageManager.PERMISSION_GRANTED

@SuppressLint("NewApi")
fun Context.getTimerNotification(
    pendingIntent: PendingIntent
): Notification {
    var soundUri = config.timerSoundUri
    if (soundUri == SILENT) {
        soundUri = ""
    } else {
        grantReadUriPermission(soundUri)
    }

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId =
        if (config.timerChannelId.isNullOrEmpty()) "simple_timer_channel_${soundUri}_${System.currentTimeMillis()}" else config.timerChannelId
    config.timerChannelId = channelId

    if (isOreoPlus()) {
        try {
            notificationManager.deleteNotificationChannel(channelId)
        } catch (e: Exception) {
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setLegacyStreamType(AudioManager.STREAM_ALARM)
            .build()

        val name = getString(R.string.timer)
        val importance = NotificationManager.IMPORTANCE_HIGH
        NotificationChannel(channelId, name, importance).apply {
            setBypassDnd(true)
            enableLights(true)
            lightColor = ResourceUtil.getColor(R.color.purple_200)
            setSound(Uri.parse(soundUri), audioAttributes)

            if (!config.timerVibrate) {
                vibrationPattern = longArrayOf(0L)
            }

            enableVibration(true)
            notificationManager.createNotificationChannel(this)
        }
    }

    val builder = NotificationCompat.Builder(this)
        .setContentTitle(getString(R.string.timer))
        .setContentText(getString(R.string.time_expired))
        .setSmallIcon(R.drawable.ic_timer)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(Notification.DEFAULT_LIGHTS)
        .setCategory(Notification.CATEGORY_EVENT)
        .setAutoCancel(true)
        .setSound(Uri.parse(soundUri), AudioManager.STREAM_ALARM)
        .setChannelId(channelId)
        .addAction(
            R.drawable.ic_timer,
            getString(R.string.dismiss), getHideTimerPendingIntent()
        )

    builder.setSound((Uri.parse(soundUri)))
    builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

    if (config.timerVibrate) {
        val vibrateArray = LongArray(2) { 300 }
        builder.setVibrate(vibrateArray)
    }

    val notification = builder.build()
    notification.flags = notification.flags or Notification.FLAG_INSISTENT
    return notification
}

fun Context.hideTimerNotification() = hideNotification(TIMER_NOTIF_ID)