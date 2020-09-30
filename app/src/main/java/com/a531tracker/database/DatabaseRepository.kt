package com.a531tracker.database

import android.content.Context
import com.a531tracker.ObjectBuilders.CompoundLifts
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

//@Module
//@InstallIn(ApplicationComponent::class)
class DatabaseRepository (private val mContext: Context) {

    private lateinit var db: DatabaseHelper

    init {
        db = DatabaseHelper(mContext)
    }

    fun getDataRepo(mContext: Context) : DatabaseRepository {
        return DatabaseRepository(mContext)
    }

    fun getLift(liftName: String): CompoundLifts? {
        return db.getLifts(liftName)
    }
}