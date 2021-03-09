package com.kan.codingchallengesfossil3.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kan.codingchallengesfossil3.data.repository.TimerDataRepository
import com.kan.codingchallengesfossil3.data.repository.TimerRepository
import com.kan.codingchallengesfossil3.di.PerActivity
import com.kan.codingchallengesfossil3.di.ViewModelFactory
import com.kan.codingchallengesfossil3.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

@Module
 class MainModule {

    @Provides
    @PerActivity
    fun provideViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory {
        return factory
    }

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel(viewModel: MainViewModel): ViewModel {
        return viewModel
    }

    @Provides
    @PerActivity
    fun provideTimerRepository(repository: TimerDataRepository): TimerRepository {
        return repository
    }

}