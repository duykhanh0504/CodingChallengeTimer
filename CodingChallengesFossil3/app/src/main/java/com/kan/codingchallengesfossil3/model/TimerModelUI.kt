package com.kan.codingchallengesfossil3.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Kan on 3/9/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

class TimerModelUI(
    var id: Int = 0,
    var timerSecond: Long? = null,
    var updateAt: String? = null
)