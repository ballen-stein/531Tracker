package com.a531tracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.dialogs.BottomDialog
import com.a531tracker.dialogs.BottomDialogJoker
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
        menuInflater.inflate(R.menu.week_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.help -> {
                val msgIntent = Intent(Intent.ACTION_SENDTO).apply {
                    type = "text/plain"
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, "ballenstein20@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "5/3/1 Fitness: Question about the app")
                }

                if (msgIntent.resolveActivity(this.packageManager) != null) {
                    startActivity(msgIntent)
                }
            }
            R.id.info -> {
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder().apply {
                    setToolbarColor(ContextCompat.getColor(this@BaseActivity, R.color.colorPrimaryDark))
                    addDefaultShareMenuItem()
                    setShowTitle(true)
                }
                builder.build().launchUrl(this, Uri.parse(AppConstants.JW_URL))
            }
            R.id.joker -> {
                BottomDialogJoker(
                        this,
                        DatabaseRepository(this)
                ).newInstance().show(supportFragmentManager, "joker")
            }
        }

        return true
    }

    private fun navIconListener() {
        navigationBar.setNavigationOnClickListener {
            BottomDialog(this).newInstance(hashMapOf(AppConstants.NAVIGATION_MENU to 0)).show(supportFragmentManager, "navigation")
        }
    }
}