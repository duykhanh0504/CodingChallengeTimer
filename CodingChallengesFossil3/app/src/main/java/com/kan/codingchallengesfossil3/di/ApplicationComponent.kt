package com.kan.codingchallengesfossil3.di

import android.content.Context
import com.kan.codingchallengesfossil3.application.BaseApplication
import dagger.Component
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    /**
     *
     * @return [FFMApplication] data type
     */
    fun getApplication(): BaseApplication

    /**
     * get Context
     *
     * @return [Context] data type
     */
    fun getContext(): Context

    /**
     * get EventBus
     *
     * @return [EventBus] data type
     */
    fun getEventBus(): EventBus

}