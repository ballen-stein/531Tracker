package com.a531tracker.mvpbase

import android.content.Context
import com.a531tracker.database.DatabaseRepository

class DependencyInjectorClass : DependencyInjector {
    override fun dataRepo(mContext: Context): DatabaseRepository {
        return DatabaseRepository(mContext = mContext)
    }
}