package com.kan.codingchallengesfossil3.extension

import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

fun Int.getFormattedDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

val Int.secondsToMillis get() = TimeUnit.SECONDS.toMillis(this.toLong())

val Int.millisToSeconds get() = TimeUnit.MILLISECONDS.toSeconds(this.toLong())