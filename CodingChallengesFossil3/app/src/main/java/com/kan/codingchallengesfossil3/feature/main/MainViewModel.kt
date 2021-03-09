package com.kan.codingchallengesfossil3.feature.main

import androidx.lifecycle.MutableLiveData
import com.kan.codingchallengesfossil3.base.BaseViewModel
import com.kan.codingchallengesfossil3.data.model.TimerModel
import com.kan.codingchallengesfossil3.data.repository.TimerRepository
import com.kan.codingchallengesfossil3.extension.getFormattedDuration
import com.kan.codingchallengesfossil3.model.StateEvent
import javax.inject.Inject

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class MainViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : BaseViewModel() {

    var totalSecondTime: Long = 0

    var currentState: StateEvent = StateEvent.Idle

    var currentTime: MutableLiveData<String> = MutableLiveData()

    var listTimerSetup: MutableLiveData<List<TimerModel>> = MutableLiveData()

    fun updateTimer(timerTick: Long?) {
        currentTime.value = timerTick?.getFormattedDuration() ?: 0L.getFormattedDuration()
    }

    fun insertTimer(data: TimerModel) {
        timerRepository.save(data)
    }

    fun getAllTimer() {
        val data = timerRepository.findAllData()
        data?.also {
            listTimerSetup.value = data
        }
    }

}