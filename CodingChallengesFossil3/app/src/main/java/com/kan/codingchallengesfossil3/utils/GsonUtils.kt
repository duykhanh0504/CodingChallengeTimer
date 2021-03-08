package com.kan.codingchallengesfossil3.utils

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.kan.codingchallengesfossil3.extension.empty
import com.kan.codingchallengesfossil3.model.StateEvent

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

object GsonUtils {

    inline fun <reified T : Any> toJson(data: T): String = try {
        Gson().toJson(data, T::class.java)
    } catch (ex: JsonIOException) {
        String.empty()
    }

    inline fun <reified T : Any> fromJson(data: String): T? = try {
        Gson().fromJson(data, T::class.java)
    } catch (ex: JsonIOException) {
        null
    }

}
