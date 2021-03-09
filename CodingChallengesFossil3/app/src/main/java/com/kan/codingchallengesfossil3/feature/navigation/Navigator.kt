package com.kan.codingchallengesfossil3.feature.navigation

import android.content.Context
import com.kan.codingchallengesfossil3.base.BaseActivity
import com.kan.codingchallengesfossil3.feature.main.setting.SettingActivity
import com.kan.codingchallengesfossil3.feature.main.timer.TimerFragment
import javax.inject.Inject

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class Navigator @Inject constructor() {

    fun showPickerFragment(activity: BaseActivity, containerViewId: Int) {
        activity.addFragment(
            containerViewId,
            TimerFragment.newInstance(), false
        )
    }

    fun showMenuActivity(context: Context) {
        val intent = SettingActivity.createIntent(context)
        context.startActivity(intent)
    }

}