package com.kan.codingchallengesfossil3.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.kan.codingchallengesfossil3.R
import com.kan.codingchallengesfossil3.application.BaseApplication

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

object ResourceUtil {
    /**
     * Returns Context.
     */
    private val context: Context get() = BaseApplication.INSTANCE.applicationContext

    fun getString(@StringRes id: Int): String = context.getString(id)

    fun getStringArray(@ArrayRes id: Int): Array<String> = context.resources.getStringArray(id)

    fun getDimensionPixelSize(@DimenRes id: Int) = context.resources.getDimensionPixelSize(id)

    fun getHeightActionBar() =
        context.resources.getDimensionPixelSize(R.dimen.abc_action_bar_default_height_material)

    fun getColor(@ColorRes id: Int) = ContextCompat.getColor(context, id)

    fun getDrawable(@DrawableRes id: Int): Drawable? = ContextCompat.getDrawable(context, id)

    fun getDrawableCompat(@DrawableRes id: Int): Drawable? =
        VectorDrawableCompat.create(context.resources, id, context.theme)

    fun getDimension(@DimenRes id: Int) = context.resources.getDimension(id)

    fun getString(@StringRes id: Int, vararg formatArgs: Any): String =
        context.resources.getString(id, *formatArgs)

    fun getDefaultAlarmUri(type: Int): Uri =
        RingtoneManager.getDefaultUri(if (type == ALARM_SOUND_TYPE_NOTIFICATION) RingtoneManager.TYPE_NOTIFICATION else RingtoneManager.TYPE_ALARM)


}