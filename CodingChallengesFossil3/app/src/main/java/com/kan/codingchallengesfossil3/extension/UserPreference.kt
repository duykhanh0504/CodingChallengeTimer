package com.kan.codingchallengesfossil3.extension

import android.content.Context
import com.kan.codingchallengesfossil3.application.BaseApplication
import com.kan.codingchallengesfossil3.base.BasePreferences

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

enum class UserPreferences(override val key: String) : BasePreferences {
    TIMER_SECONDS("timer_second"),
    TIMER_STATE("timer_state"),
    TIMER_VIBRATE("timer_vibrate"),
    TIMER_SOUND_URI("timer_sound_uri"),
    TIMER_CHANNEL_ID("timer_channel_id");

    companion object {
        /*** Save when a file name  */
        private const val FILE_NAME = "user_pref"

        fun removeAll() {
            val sp = BaseApplication.INSTANCE.applicationContext.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            sp.edit().clear().apply()
        }
    }

    override fun getSharedPreferences(): android.content.SharedPreferences {
        return BaseApplication.INSTANCE.applicationContext.getSharedPreferences(
            FILE_NAME,
            Context.MODE_PRIVATE
        )
    }

}