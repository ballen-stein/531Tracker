package com.a531tracker.week

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.a531tracker.database.DatabaseRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class WeekActivity : AppCompatActivity(), ViewBinding, WeekContract.View {

    @Inject lateinit var databaseRepository: DatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getRoot(): View {
        TODO("Not yet implemented")
    }

    override fun updateWeekFragment() {
        TODO("Not yet implemented")
    }

    override fun error(throwable: Throwable) {
        TODO("Not yet implemented")
    }

    override fun setPresenter(presenter: WeekContract.Presenter) {
        TODO("Not yet implemented")
    }
}