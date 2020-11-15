package com.a531tracker.week

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.a531tracker.tools.AppConstants
import com.a531tracker.BaseActivity
import com.a531tracker.dialogs.BottomDialog
import com.a531tracker.R
import com.a531tracker.databinding.ActivityWeekBinding
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppUtils
import com.a531tracker.adapters.WorkoutPagerAdapter
import com.a531tracker.homepage.HomePageActivity
import com.a531tracker.tools.PreferenceUtils
import com.a531tracker.tools.Snack
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_week_toolbar.view.*

class WeekActivity : BaseActivity(), ViewBinding, WeekContract.View, BottomDialog.BottomDialogClicks {

    private lateinit var presenter: WeekContract.Presenter

    private lateinit var binding: ActivityWeekBinding

    private lateinit var pager: ViewPager2

    private lateinit var tabLayout: TabLayout

    private lateinit var depInjector: DependencyInjectorClass

    private lateinit var prefUtils: PreferenceUtils

    private lateinit var userPreferences: MutableMap<String, *>

    private lateinit var compound: String
    private lateinit var swapLift: String

    private var altFormat: Boolean = true
    private var sevenWeek: Boolean = true
    private var cycleNum: Int = 0
    private var currentFrag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeekBinding.inflate(layoutInflater)
        setContentView(root)

        pager = binding.root.pager_view
        tabLayout = binding.root.tab_view_days

        compound = intent.getStringExtra(AppConstants.MAIN_LIFT) ?: "na"
        cycleNum = intent.getIntExtra(AppConstants.CYCLE_NUM, -1)
        swapLift = intent.getStringExtra(AppConstants.SWAP_LIFT) ?: "na"

        if (compound == "na" || swapLift == "na" || cycleNum == -1) {
            startActivity(Intent(this, HomePageActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
        }

        depInjector = DependencyInjectorClass()
        prefUtils = PreferenceUtils.getInstance(mContext = this)
        userPreferences = prefUtils.userPreferences()!!.all
        altFormat = userPreferences[getString(R.string.preference_split_variant_extra_key)] as Boolean
        sevenWeek = prefUtils.userPreferences()?.all?.get(getString(R.string.preference_week_options_key)) as Boolean

        setPresenter(WeekPresenter(this, depInjector, AppUtils(), prefUtils, this))
        presenter.onViewCreated(this, compound)

        setSupportActionBar(binding.root.week_bottom_toolbar)

        root.week_bottom_toolbar.setNavigationOnClickListener {
            BottomDialog(this).newInstance(
                    hashMapOf(AppConstants.NAVIGATION_MENU to 0)
            ).show(supportFragmentManager, "navigation")
        }

        root.week_fab.setOnClickListener {
            for (fragment in supportFragmentManager.fragments) {
                if (fragment.isVisible) {
                    currentFrag = when(fragment.tag!!) {
                        "f0" -> 1
                        "f1" -> 2
                        "f2" -> 3
                        else -> 4
                    }
                }
            }

            BottomDialog(this).newInstance(
                    hashMapOf(AppConstants.NAVIGATION_MENU to 1, AppConstants.NAVIGATION_WEEK to currentFrag, AppConstants.NAVIGATION_AMRAP to getLastWeekAmrap())
            ).show(supportFragmentManager, "input")
        }

        for (fragment in supportFragmentManager.fragments) {
            if (fragment.isVisible && fragment.tag == "f3") {
                root.week_fab.visibility = View.GONE
            } else {
                root.week_fab.visibility = View.VISIBLE
            }
        }

        //Snackbar.make(binding.snackHolder,  getString(R.string.amrap_success), Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        if (userPreferences != prefUtils.userPreferences()?.all) {
            userPreferences = prefUtils.userPreferences()?.all!!
            altFormat = prefUtils.userPreferences()?.all?.get(getString(R.string.preference_split_variant_extra_key)) as Boolean
            sevenWeek = prefUtils.userPreferences()?.all?.get(getString(R.string.preference_week_options_key)) as Boolean
            presenter.onPrefUpdate(this, compound, userPreferences)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.week_menu, menu)
        return true
    }

    private fun createViewAdapter(): WorkoutPagerAdapter {
        return WorkoutPagerAdapter(this, sevenWeek, cycleNum)
    }

    private fun getLastWeekAmrap(): Int {
        return depInjector.dataRepo(this).getAmrapValue(compound, currentFrag)
    }

    override fun getRoot(): View {
        return binding.root
    }

    override fun updateWeekFragment() {
        pager.adapter = createViewAdapter()
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = when (position) {
                0 -> {
                    if (!altFormat) getString(R.string.format_531_week_1) else getString(R.string.format_863_week_1)
                }
                1 -> {
                    if (!altFormat) getString(R.string.format_531_week_2) else getString(R.string.format_863_week_2)
                }
                2 -> {
                    if (!altFormat) getString(R.string.format_531_week_3) else getString(R.string.format_863_week_3)
                }
                else -> {
                    getString(R.string.deload_text)
                }
            }
        }.attach()

    }

    override fun refresh(refresh: Boolean) {

    }

    override fun setLastWeek(int: Int) {
    }

    override fun amrapSnackbar(success: Boolean) {
        Handler().postDelayed(
                {
                    runOnUiThread{
                        Snack(this).apply {
                            if(success) {
                                info(binding.root.week_bottom_toolbar, getString(R.string.amrap_success))
                            } else {
                                error(binding.root.week_bottom_toolbar, getString(R.string.amrap_error))
                            }
                        }
                    }
                }, 500
        )
    }

    override fun error(throwable: Throwable) {

    }

    override fun setPresenter(presenter: WeekContract.Presenter) {
        this.presenter = presenter
    }

    // Bottom Dialog Click
    override fun submitAmrap(text: String, currentWeek: Int) {
        if (currentWeek != 4) {
            presenter.onAmrapReceived(
                    liftName = compound,
                    percent = basicPercents[currentWeek-1],
                    repsDone = text.toInt()
            )
        }
    }

    companion object {
        private val basicPercents = arrayOf("0.85", "0.9", "0.95")
        private val threePercents = arrayOf("0.80", "0.85", "0.90")
    }
}

