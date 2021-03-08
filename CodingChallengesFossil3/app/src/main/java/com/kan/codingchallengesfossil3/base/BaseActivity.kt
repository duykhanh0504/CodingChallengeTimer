package com.kan.codingchallengesfossil3.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kan.codingchallengesfossil3.application.BaseApplication
import com.kan.codingchallengesfossil3.di.ApplicationComponent
import com.kan.codingchallengesfossil3.extension.empty
import org.greenrobot.eventbus.EventBus

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

abstract class BaseActivity : AppCompatActivity() {

    protected val eventBus: EventBus? by lazy {
        applicationComponent().getEventBus()
    }

    /**
     * returns id of layout
     */
    protected abstract fun layoutId(): Int

    /**
     * inject dagger2
     */
    protected abstract fun inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        setContentView(layoutId())
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    /**
     * @return [ApplicationComponent]
     */
    fun applicationComponent(): ApplicationComponent {
        return (application as BaseApplication).applicationComponent
    }

    /**
     * add fragment
     */
    fun addFragment(containerViewId: Int, fragment: Fragment, backToStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(containerViewId, fragment)
        if (backToStack) {
            transaction.addToBackStack(String.empty())
        }
        transaction.commit()
    }

    fun clearAllFragment() {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
    }

    /**
     * replace fragment
     */
    fun replaceFragment(
        containerViewId: Int,
        fragment: Fragment,
        animationId: Int = -1,
        backToStack: Boolean = false
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (animationId != -1) {
            transaction.setCustomAnimations(animationId, 0, 0, 0)
        }
        if (backToStack) {
            transaction.addToBackStack(String.empty())
        }
        transaction.replace(containerViewId, fragment)
        transaction.commit()
    }

    protected fun popFragment() {
        supportFragmentManager.popBackStack()
    }

    protected fun registerEventBus() {
        eventBus?.also {
            if (!it.isRegistered(this@BaseActivity)) {
                it.register(this@BaseActivity)
            }
        }
    }

    protected fun unregisterEventBus() {
        eventBus?.also {
            if (it.isRegistered(this@BaseActivity)) {
                it.unregister(this@BaseActivity)
            }
        }
    }

}