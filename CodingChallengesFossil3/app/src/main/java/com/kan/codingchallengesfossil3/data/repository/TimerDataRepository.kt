package com.kan.codingchallengesfossil3.data.repository

import androidx.lifecycle.LiveData
import com.kan.codingchallengesfossil3.data.model.TimerModel
import com.kan.codingchallengesfossil3.extension.livedata.RealmLiveData
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults
import io.realm.Sort
import java.util.*
import javax.inject.Inject
import javax.inject.Provider


/**
 * Created by Kan on 3/8/21
 * Copyright © 2018 Money Forward, Inc. All rights
 */


class TimerDataRepository @Inject
constructor(private val realmProvider: Realm) : TimerRepository {


    override fun findAllData(): List<TimerModel> {
        val realmResults = realmProvider.where(TimerModel::class.java)
        return realmResults.findAll()
    }


    override fun findAllSorted(
        sortField: String?,
        sort: Sort,
        detached: Boolean
    ): List<TimerModel> {
        val realmResults =
            realmProvider.where(TimerModel::class.java).findAllSorted(sortField, sort)

        if (detached) {
            return realmProvider.copyFromRealm(realmResults)
        } else {
            return realmResults
        }
    }

    fun <T : RealmModel> RealmResults<T>.asLiveData() = RealmLiveData<T>(this)

    override fun findAllSortedWithChanges(
        sortField: String?,
        sort: Sort
    ): LiveData<RealmResults<TimerModel>> {
        return realmProvider.where(TimerModel::class.java).findAllSortedAsync(sortField, sort)
            .asLiveData()
    }

    override fun getByField(field: String?, value: String?, detached: Boolean): TimerModel? {

        var realmCountry: TimerModel? =
            realmProvider.where(TimerModel::class.java).equalTo(field, value).findFirst()
        if (detached && realmCountry != null) {
            realmCountry = realmProvider.copyFromRealm<TimerModel>(realmCountry)
        }
        return realmCountry

    }

    override fun save(data: TimerModel) {
        realmProvider.executeTransaction() { r -> r.copyToRealmOrUpdate(data) }
    }

    override fun delete(data: TimerModel) {
        val obj = realmProvider.where(TimerModel::class.java).equalTo("id", data.id).findFirst()
        obj?.also {
            realmProvider.executeTransaction() { r ->
                it.deleteFromRealm()
            }
        }

    }

}