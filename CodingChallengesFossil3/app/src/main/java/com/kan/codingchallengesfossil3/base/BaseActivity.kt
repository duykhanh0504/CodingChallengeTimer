package com.kan.codingchallengesfossil3.base

import android.app.Activity
import android.media.RingtoneManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.application.BaseApplication
import com.kan.codingchallengesfossil3.di.ApplicationComponent
import com.kan.codingchallengesfossil3.extension.*
import com.kan.codingchallengesfossil3.model.AlarmSound
import com.kan.codingchallengesfossil3.utils.ALARM_SOUND_TYPE_NOTIFICATION
import com.kan.codingchallengesfossil3.utils.PERMISSION_READ_STORAGE
import com.kan.codingchallengesfossil3.utils.SILENT
import kotlinx.android.synthetic.main.dialog_title.view.*
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

abstract class BaseActivity : AppCompatActivity() {

    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    var isAskingPermissions = false
    private val GENERIC_PERM_HANDLER = 100

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

    fun getAlarmSounds(type: Int, callback: (ArrayList<AlarmSound>) -> Unit) {
        val alarms = ArrayList<AlarmSound>()
        val manager = RingtoneManager(this)
        manager.setType(if (type == ALARM_SOUND_TYPE_NOTIFICATION) RingtoneManager.TYPE_NOTIFICATION else RingtoneManager.TYPE_ALARM)

        try {
            val cursor = manager.cursor
            var curId = 1
            val silentAlarm = AlarmSound(curId++, getString(R.string.silent), SILENT)
            alarms.add(silentAlarm)

            val defaultAlarm = getDefaultAlarmSound(type)
            alarms.add(defaultAlarm)

            while (cursor.moveToNext()) {
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                var uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
                val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
                if (!uri.endsWith(id)) {
                    uri += "/$id"
                }

                val alarmSound = AlarmSound(curId++, title, uri)
                alarms.add(alarmSound)
            }
            callback(alarms)
        } catch (e: Exception) {
            if (e is SecurityException) {
                handlePermission(PERMISSION_READ_STORAGE) {
                    if (it) {
                        getAlarmSounds(type, callback)
                    } else {
                        callback(ArrayList())
                    }
                }
            } else {
                callback(ArrayList())
            }
        }
    }

    fun Activity.setupDialogStuff(view: View, dialog: AlertDialog, titleId: Int = 0, titleText: String = "", callback: (() -> Unit)? = null) {
        if (isDestroyed || isFinishing) {
            return
        }

        var title: TextView? = null
        if (titleId != 0 || titleText.isNotEmpty()) {
            title = layoutInflater.inflate(R.layout.dialog_title, null) as TextView
            title.dialog_title_textview.apply {
                if (titleText.isNotEmpty()) {
                    text = titleText
                } else {
                    setText(titleId)
                }
            }
        }

        dialog.apply {
            setView(view)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCustomTitle(title)
            setCanceledOnTouchOutside(true)
            show()
        }
        callback?.invoke()
    }

    fun setupDialogStuff(view: View, dialog: AlertDialog, titleId: Int = 0, titleText: String = "", callback: (() -> Unit)? = null) {
        if (isDestroyed || isFinishing) {
            return
        }

        var title: TextView? = null
        if (titleId != 0 || titleText.isNotEmpty()) {
            title = layoutInflater.inflate(R.layout.dialog_title, null) as TextView
            title.dialog_title_textview.apply {
                if (titleText.isNotEmpty()) {
                    text = titleText
                } else {
                    setText(titleId)
                }
            }
        }

        dialog.apply {
            setView(view)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCustomTitle(title)
            setCanceledOnTouchOutside(true)
            show()
        }
        callback?.invoke()
    }

    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(this, arrayOf(getPermissionString(permissionId)), GENERIC_PERM_HANDLER)
        }
    }

}