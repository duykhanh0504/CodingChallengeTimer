package com.kan.codingchallengesfossil3.extension

import android.os.SystemClock
import android.view.View

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class OnSafeClickListener(
    private var defaultInterval: Int = 200,
    private val onSafeClick: (View) -> Unit
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        val time = SystemClock.elapsedRealtime() - lastTimeClicked

        if (time >= defaultInterval) {
            lastTimeClicked = SystemClock.elapsedRealtime()
            onSafeClick(v)
        }
    }
}

fun View.setOnSafeClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = OnSafeClickListener {
        onSafeClick(it)
    }

    setOnClickListener(safeClickListener)
}