package com.kan.codingchallengesfossil3.feature.main.setting

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.base.BaseActivity
import com.kan.codingchallengesfossil3.extension.*
import com.kan.codingchallengesfossil3.feature.dialog.SelectAlarmSoundDialog
import com.kan.codingchallengesfossil3.feature.main.timer.TimerFragment
import com.kan.codingchallengesfossil3.feature.navigation.Navigator
import com.kan.codingchallengesfossil3.utils.ALARM_SOUND_TYPE_ALARM
import com.kan.codingchallengesfossil3.utils.PICK_AUDIO_FILE_INTENT_ID
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class SettingActivity : BaseActivity() {

    companion object {
        fun createIntent(context: Context) =
            Intent(context, SettingActivity::class.java)
    }

    override fun layoutId() = R.layout.activity_setting

    override fun inject() = applicationComponent().inject(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
    }

    private fun initView() {
        switchVibrate.isChecked = config.timerVibrate
    }

    private fun setListener() {
        switchVibrate.setOnCheckedChangeListener { _, isChecked ->
            config.timerVibrate = isChecked
            config.timerChannelId = String.empty()
        }

        rlSelectSound.setOnSafeClickListener {
            SelectAlarmSoundDialog(this, config.timerSoundUri, AudioManager.STREAM_ALARM,
                ALARM_SOUND_TYPE_ALARM, true,
                onAlarmPicked = { sound ->
                    if (sound != null) {
                        config.timerChannelId = String.empty()
                        config.timerSoundUri = sound.uri
                    }
                })
        }
    }
}