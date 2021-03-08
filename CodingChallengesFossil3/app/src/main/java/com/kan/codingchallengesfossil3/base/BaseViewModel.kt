package com.kan.codingchallengesfossil3.base

import androidx.lifecycle.ViewModel
import com.kan.codingchallengesfossil3.common.Failure
import com.kan.codingchallengesfossil3.extension.livedata.SingleLiveEvent

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

abstract class BaseViewModel : ViewModel() {

    var failure: SingleLiveEvent<Failure> = SingleLiveEvent()
    var isLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()

    protected open fun handleFailure(failure: Failure) {
        this.isLoading.value = false
        this.failure.value = failure
    }

    protected fun isCanLoadData() = when (isLoading.value) {
        true -> false
        else -> true
    }
}