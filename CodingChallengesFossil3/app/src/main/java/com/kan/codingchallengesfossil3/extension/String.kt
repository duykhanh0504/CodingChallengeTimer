package com.kan.codingchallengesfossil3.extension

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

fun String.Companion.empty() = ""

fun String.Companion.parseToDouble(value: String): Double {
    return value.replace(",", "").toDouble()
}

fun String.isInteger() = toIntOrNull()?.let { true } ?: false

fun String.isNumber() = toBigDecimalOrNull()?.let { true } ?: false