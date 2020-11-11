package com.a531tracker.mvpbase

import android.content.Context
import com.a531tracker.database.DatabaseRepository

interface DependencyInjector {
    fun dataRepo(mContext: Context) : DatabaseRepository
}