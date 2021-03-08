package com.kan.codingchallengesfossil3.di

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

interface HasComponent<out C> {
    fun getComponent(): C
}