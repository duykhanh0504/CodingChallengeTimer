package com.kan.codingchallengesfossil3.feature.dialog

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.base.BaseActivity
import com.kan.codingchallengesfossil3.extension.config
import com.kan.codingchallengesfossil3.extension.empty
import com.kan.codingchallengesfossil3.model.AlarmSound
import com.kan.codingchallengesfossil3.utils.SILENT
import kotlinx.android.synthetic.main.dialog_select_alarm_sound.view.*
import kotlinx.android.synthetic.main.dialog_title.view.*
import java.util.ArrayList

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class SelectAlarmSoundDialog(val activity: BaseActivity, val currentUri: String, val audioStream: Int,
                             val type: Int, val loopAudio: Boolean, val onAlarmPicked: (alarmSound: AlarmSound?) -> Unit) {

    private val view = activity.layoutInflater.inflate(R.layout.dialog_select_alarm_sound, null)
    private var systemAlarmSounds = ArrayList<AlarmSound>()
    private var mediaPlayer: MediaPlayer? = null
    private val dialog: AlertDialog
    private var soundId: Int = -1

    init {
        activity.getAlarmSounds(type) {
            systemAlarmSounds = it
            gotSystemAlarms()
        }


        dialog = AlertDialog.Builder(activity)
            .setOnDismissListener { mediaPlayer?.stop() }
            .setPositiveButton(R.string.ok) { _, _ -> dialogConfirmed() }
            .setNegativeButton(R.string.cancel, null)
            .create().apply {
                activity.setupDialogStuff(view, this)
                window?.volumeControlStream = audioStream
            }
    }

    fun Activity.setupDialogStuff(view: View, dialog: AlertDialog, titleId: Int = 0, titleText: String = String.empty(), callback: (() -> Unit)? = null) {
        if (isDestroyed || isFinishing) {
            return
        }


        var title: TextView? = null
        if (titleId != 0 || titleText.isNotEmpty()) {
            title = layoutInflater.inflate(R.layout.dialog_title, null) as TextView
            title.dialog_title_textview.apply {
                if (titleText.isNotEmpty()) {
                    text = titleText
                } else {
                    setText(titleId)
                }

            }
        }

        dialog.apply {
            setView(view)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCustomTitle(title)
            setCanceledOnTouchOutside(true)
            show()
        }
        callback?.invoke()
    }


    private fun gotSystemAlarms() {
        systemAlarmSounds.forEach {
            addAlarmSound(it, view.radioSelectAlarm)
        }
    }

    private fun addAlarmSound(alarmSound: AlarmSound, holder: ViewGroup) {
        val radioButton = (activity.layoutInflater.inflate(R.layout.item_select_alarm_sound, null) as RadioButton).apply {
            text = alarmSound.title
            isChecked = alarmSound.uri == currentUri
            id = alarmSound.id
            setOnClickListener {
                alarmClicked(alarmSound)
                soundId = alarmSound.id
                if (holder == view.radioSelectAlarm) {
                    view.radioSelectAlarm.clearCheck()
                } else {
                    view.radioSelectAlarm.clearCheck()
                }
            }

        }

        holder.addView(radioButton, RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    private fun alarmClicked(alarmSound: AlarmSound) {
        when {
            alarmSound.uri == SILENT -> mediaPlayer?.stop()
            alarmSound.id != -1 -> {
                try {
                    mediaPlayer?.reset()
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer().apply {
                            setAudioStreamType(audioStream)
                            isLooping = loopAudio
                        }
                    }

                    mediaPlayer?.apply {
                        setDataSource(activity, Uri.parse(alarmSound.uri))
                        prepare()
                        start()
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


    private fun dialogConfirmed() {
        onAlarmPicked(systemAlarmSounds.firstOrNull { it.id == soundId })
    }
}
