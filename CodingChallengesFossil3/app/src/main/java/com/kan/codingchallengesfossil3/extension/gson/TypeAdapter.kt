package com.kan.codingchallengesfossil3.extension.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapterFactory
import com.kan.codingchallengesfossil3.model.StateEvent

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

val timerStates = valueOf<StateEvent>()
    .registerSubtype(StateEvent.Idle::class.java)
    .registerSubtype(StateEvent.Start::class.java)
    .registerSubtype(StateEvent.Running::class.java)
    .registerSubtype(StateEvent.Pause::class.java)
    .registerSubtype(StateEvent.Paused::class.java)
    .registerSubtype(StateEvent.Finish::class.java)
    .registerSubtype(StateEvent.Finished::class.java)

inline fun <reified T : Any> valueOf(): RuntimeTypeAdapterFactory<T> = RuntimeTypeAdapterFactory.of(T::class.java)

fun GsonBuilder.registerTypes(vararg types: TypeAdapterFactory) = apply {
    types.forEach { registerTypeAdapterFactory(it) }
}

val gson: Gson = GsonBuilder().registerTypes(timerStates).create()
