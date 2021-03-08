package com.kan.codingchallengesfossil3.feature.main

import com.kan.codingchallengesfossil3.di.ApplicationComponent
import com.kan.codingchallengesfossil3.di.PerActivity
import com.kan.codingchallengesfossil3.feature.main.timer.TimerFragment
import dagger.Component

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

@PerActivity
@Component(modules = [MainModule::class], dependencies = [ApplicationComponent::class])
interface MainComponent {

    fun inject(activity: MainActivity)

    fun inject(fragment: TimerFragment)

}