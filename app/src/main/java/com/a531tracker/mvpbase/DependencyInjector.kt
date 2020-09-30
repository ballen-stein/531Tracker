package com.a531tracker.mvpbase

import com.a531tracker.database.DatabaseRepository

interface DependencyInjector {
    fun dataRepo() : DatabaseRepository
}