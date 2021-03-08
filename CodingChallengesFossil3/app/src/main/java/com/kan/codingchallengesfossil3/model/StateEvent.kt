package com.kan.codingchallengesfossil3.model

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

sealed class StateEvent {
    object Idle : StateEvent()
    data class Start(val duration: Long) : StateEvent()
    data class Running(val duration: Long, val tick: Long) : StateEvent()
    data class Pause(val duration: Long) : StateEvent()
    data class Paused(val duration: Long, val tick: Long) : StateEvent()
    data class Finish(val duration: Long) : StateEvent()
    object Finished : StateEvent()
}
