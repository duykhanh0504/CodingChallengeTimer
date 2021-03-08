package com.kan.codingchallengesfossil3.extension

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

inline fun <reified T : ViewModel> Fragment.viewModel(
    factory: ViewModelProvider.Factory, body: T.() -> Unit
): T {
    val vm = ViewModelProviders.of(this, factory).get(T::class.java)
    vm.body()
    return vm
}

@Throws(InstantiationException::class, IllegalAccessException::class)
inline fun <reified F: Fragment> newInstance(clazz: Class<F>, bundle: Bundle? = null): F {
    return clazz.newInstance().apply {
        this.arguments = bundle
    }
}