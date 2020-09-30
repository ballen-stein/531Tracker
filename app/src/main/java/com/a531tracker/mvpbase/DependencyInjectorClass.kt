package com.a531tracker.mvpbase

import com.a531tracker.database.DatabaseRepository

class DependencyInjectorClass : DependencyInjector {
    override fun dataRepo(): DatabaseRepository {
        return DatabaseRepository()
    }
}