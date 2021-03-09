package com.kan.codingchallengesfossil3.data.repository

import androidx.lifecycle.LiveData
import com.kan.codingchallengesfossil3.data.model.TimerModel
import io.realm.RealmResults
import io.realm.Sort

/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */

interface TimerRepository {

    fun findAllData(): List<TimerModel>
    fun findAllSorted(sortField: String?, sort: Sort, detached: Boolean): List<TimerModel>
    fun findAllSortedWithChanges(sortField: String?, sort: Sort): LiveData<RealmResults<TimerModel>>

    fun getByField(field: String?, value: String?, detached: Boolean): TimerModel?

    fun save(data: TimerModel)
    fun delete(data: TimerModel)

}