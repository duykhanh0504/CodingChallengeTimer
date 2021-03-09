package com.kan.codingchallengesfossil3.data.model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

public open class TimerModel(
    @PrimaryKey public open var id: Int = 0,
    open var timerSecond: Long? = null,
    open var updateAt: String? = null
) : RealmObject() {

}
