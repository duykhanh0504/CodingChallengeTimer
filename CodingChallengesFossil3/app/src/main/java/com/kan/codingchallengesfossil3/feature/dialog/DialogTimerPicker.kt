package com.kan.codingchallengesfossil3.feature.dialog

import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.base.BaseActivity
import com.kan.codingchallengesfossil3.extension.config
import com.kan.codingchallengesfossil3.feature.main.timer.TimerFragment
import kotlinx.android.synthetic.main.dialog_my_time_picker.view.*
import kotlinx.android.synthetic.main.fragment_timer.*

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class DialogTimerPicker(
    val activity: BaseActivity,
    val initialSeconds: Long,
    val callback: (result: Long) -> Unit
) {
    private var view = activity.layoutInflater.inflate(R.layout.dialog_my_time_picker, null)

    init {
        view.numberPickerHour.minValue =
            TimerFragment.MIN_PICKTIME
        view.numberPickerHour.maxValue =
            TimerFragment.MAX_PICKTIME
        view.numberPickerMinute.minValue =
            TimerFragment.MIN_PICKTIME
        view.numberPickerMinute.maxValue =
            TimerFragment.MAX_PICKTIME
        view.numberPickerSecond.minValue =
            TimerFragment.MIN_PICKTIME
        view.numberPickerSecond.maxValue =
            TimerFragment.MAX_PICKTIME

        view.numberPickerHour.setFormatter(NumberPicker.Formatter { i ->
            String.format(
                TimerFragment.FORMAT_DIGIT,
                i
            )
        })
        view.numberPickerMinute.setFormatter(NumberPicker.Formatter { i ->
            String.format(
                TimerFragment.FORMAT_DIGIT,
                i
            )
        })
        view.numberPickerSecond.setFormatter(NumberPicker.Formatter { i ->
            String.format(
                TimerFragment.FORMAT_DIGIT,
                i
            )
        })

        view.numberPickerHour.value = (initialSeconds / 3600).toInt()
        view.numberPickerMinute.value = (initialSeconds / 60 % 60).toInt()
        view.numberPickerSecond.value = (initialSeconds % 60).toInt()
        AlertDialog.Builder(activity)
            .setPositiveButton(R.string.ok, { dialog, which -> dialogConfirmed() })
            .setNegativeButton(R.string.cancel, null)
            .create().apply {
                activity.setupDialogStuff(view, this)
            }
    }

    private fun dialogConfirmed() {
        view.apply {
            val hours = numberPickerHour.value
            val minutes = numberPickerMinute.value
            val seconds = numberPickerSecond.value
            callback((hours * 3600 + minutes * 60 + seconds).toLong())
        }
    }
}