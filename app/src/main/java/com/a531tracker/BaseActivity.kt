package com.a531tracker

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.a531tracker.dialogs.BottomDialog
import com.a531tracker.tools.AppConstants
import com.a531tracker.week.WeekActivity
import com.google.android.material.bottomappbar.BottomAppBar


open class BaseActivity : AppCompatActivity() {

    private lateinit var navigationBar: BottomAppBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (applicationContext is WeekActivity) {
            setContentView(R.layout.fragment_week_toolbar)
        } else {
            setContentView(R.layout.fragment_homepage_toolbar)
        }
        navigationBar = findViewById(R.id.bottom_toolbar)
        navIconListener()
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        setSupportActionBar(navigationBar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.navigation_menu, menu)
        return true
    }

    private fun navIconListener() {
        navigationBar.setNavigationOnClickListener {
            BottomDialog(this).newInstance(hashMapOf(AppConstants.NAVIGATION_MENU to 0)).show(supportFragmentManager, "navigation")
        }
    }
}