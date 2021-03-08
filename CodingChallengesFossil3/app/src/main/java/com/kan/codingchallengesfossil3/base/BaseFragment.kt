package com.kan.codingchallengesfossil3.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kan.codingchallengesfossil3.di.HasComponent
import org.greenrobot.eventbus.EventBus

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

abstract class BaseFragment : Fragment() {

    protected val eventBus: EventBus? by lazy {
        baseActivity()?.applicationComponent()?.getEventBus()
    }

    protected abstract fun layoutId(): Int

    /**
     * inject dagger2
     */
    protected abstract fun inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inject()
    }

    /**
     * Returns BaseActivity. Otherwise return null
     */
    protected fun baseActivity() = activity as? BaseActivity

    /**
     * get component [C] to inject objects
     */
    protected fun <C> getComponent(componentType: Class<C>): C? {
        return componentType.cast((activity as HasComponent<*>).getComponent())
    }

    protected fun registerEventBus() {
        eventBus?.also {
            if (!it.isRegistered(this@BaseFragment)) {
                it.register(this@BaseFragment)
            }
        }
    }

    protected fun unregisterEventBus() {
        eventBus?.also {
            if (it.isRegistered(this@BaseFragment)) {
                it.unregister(this@BaseFragment)
            }
        }
    }

}