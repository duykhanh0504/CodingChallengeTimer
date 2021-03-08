package com.kan.codingchallengesfossil3.feature.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kan.codingchallengesfossil3.extension.hideTimerNotification
import com.kan.codingchallengesfossil3.model.StateEvent
import org.greenrobot.eventbus.EventBus

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class TimerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        context.hideTimerNotification()
        EventBus.getDefault().post(StateEvent.Idle)
    }
}
