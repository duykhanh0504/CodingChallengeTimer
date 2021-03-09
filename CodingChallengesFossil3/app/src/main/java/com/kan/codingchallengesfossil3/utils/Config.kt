package com.kan.codingchallengesfossil3.utils

import android.content.Context
import com.kan.codingchallengesfossil3.extension.UserPreferences

import com.kan.codingchallengesfossil3.extension.gson.gson
import com.kan.codingchallengesfossil3.model.StateEvent
import com.kan.codingchallengesfossil3.model.StateWrapper

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class Config() {
    companion object {
        fun newInstance() = Config()
    }

    var timerSeconds: Long
        get() = UserPreferences.TIMER_SECONDS.getLong(0)
        set(lastTimerSeconds) = UserPreferences.TIMER_SECONDS.set(lastTimerSeconds)

    var timerState: StateEvent
        get() = UserPreferences.TIMER_STATE.getString().let { state ->
            gson.fromJson(state, StateWrapper::class.java)
        }?.state ?: StateEvent.Idle
        set(state) = UserPreferences.TIMER_STATE.set(gson.toJson(StateWrapper(state)))

    var timerVibrate: Boolean
        get() = UserPreferences.TIMER_VIBRATE.getBoolean(true)
        set(timerVibrate) = UserPreferences.TIMER_VIBRATE.set(timerVibrate)

    var timerChannelId: String
        get() = UserPreferences.TIMER_CHANNEL_ID.getString()
        set(id) = UserPreferences.TIMER_CHANNEL_ID.set(id)

    var timerSoundUri: String
        get() = if (UserPreferences.TIMER_SOUND_URI.getString()
                .isNullOrEmpty()
        ) ResourceUtil.getDefaultAlarmUri(ALARM_SOUND_TYPE_ALARM)
            .toString() else UserPreferences.TIMER_SOUND_URI.getString()
        set(timerSoundUri) = UserPreferences.TIMER_SOUND_URI.set(timerSoundUri)
}