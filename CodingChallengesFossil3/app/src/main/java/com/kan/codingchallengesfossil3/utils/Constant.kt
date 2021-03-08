package com.kan.codingchallengesfossil3.utils

import android.os.Build

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

const val TIMER_RUNNING_NOTIF_ID = 11111
const val TIMER_NOTIF_ID = 22222
const val ALARM_SOUND_TYPE_ALARM = 1
const val ALARM_SOUND_TYPE_NOTIFICATION = 2

const val TAB_TIMER = 1


const val CHANNEL_ID = "simple_timer_countdown"
const val SILENT = "silent"


fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isNougatMR1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isOreoMr1Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
fun isPiePlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R