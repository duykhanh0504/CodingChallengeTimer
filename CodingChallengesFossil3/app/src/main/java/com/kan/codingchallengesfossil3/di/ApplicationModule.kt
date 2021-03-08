package com.kan.codingchallengesfossil3.di

import android.content.Context
import com.kan.codingchallengesfossil3.application.BaseApplication
import com.kan.codingchallengesfossil3.feature.navigation.Navigator
import dagger.Component
import dagger.Module
import dagger.Provides
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */


@Module
open class ApplicationModule(private val application: BaseApplication) {

    @Provides
    @Singleton
    fun provideMoneyForwardApplication(): BaseApplication {
        return application
    }

    @Provides
    @Singleton
    fun provideContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideNavigator(): Navigator {
        return Navigator()
    }

    @Provides
    @Singleton
    fun provideEventBus(): EventBus {
        return EventBus.getDefault()
    }

}