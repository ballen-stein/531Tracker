package com.a531tracker.week

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.a531tracker.tools.AppConstants
import com.a531tracker.BaseActivity
import com.a531tracker.HomeScreen
import com.a531tracker.R
import com.a531tracker.databinding.ActivityWeekBinding
import com.a531tracker.mvpbase.DependencyInjectorClass
import com.a531tracker.tools.AppUtils
import com.a531tracker.views.WorkoutPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_week_toolbar.view.*

class WeekActivity : BaseActivity(), ViewBinding, WeekContract.View {

    private lateinit var presenter: WeekContract.Presenter

    private lateinit var binding: ActivityWeekBinding

    private lateinit var pager: ViewPager2

    private lateinit var tabLayout: TabLayout

    private lateinit var compound: String
    private lateinit var swapLift: String
    private var cycleNum: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeekBinding.inflate(layoutInflater)
        setContentView(root)

        pager = binding.root.pager_view
        tabLayout = binding.root.tab_view_days

        compound = intent.getStringExtra(AppConstants.COMPOUND_STRING) ?: "na"
        cycleNum = intent.getIntExtra(AppConstants.CYCLE_NUM, -1)
        swapLift = intent.getStringExtra(AppConstants.SWAP_LIFT) ?: "na"

        if (compound == "na" || swapLift == "na" || cycleNum == -1) {
            startActivity(Intent(this, HomeScreen::class.java))
        }

        setPresenter(WeekPresenter(this, DependencyInjectorClass(), AppUtils(), this))
        presenter.onViewCreated(this, compound)

        setSupportActionBar(binding.root.week_bottom_toolbar)
    }

    private fun createViewAdapter() : WorkoutPagerAdapter {
        return WorkoutPagerAdapter(this)
    }

    private fun setFragments() {
        pager.adapter = createViewAdapter()
        tabLayout.elevation = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                8.0f,
                resources.displayMetrics
        )
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            tab.text = when (position) {
                0 -> {
                    getString(R.string.format_531_week_1)
                }
                1 -> {
                    getString(R.string.format_531_week_2)
                }
                2 -> {
                    getString(R.string.format_531_week_3)
                }
                else -> {
                    getString(R.string.deload_text)
                }
            }
        }.attach()

    }

    override fun getRoot(): View {
        return binding.root
    }

    override fun updateWeekFragment() {
        setFragments()
    }

    override fun error(throwable: Throwable) {
        TODO("Not yet implemented")
    }

    override fun setPresenter(presenter: WeekContract.Presenter) {
        this.presenter = presenter
    }

    companion object {
        private val basicPercents = arrayOf(0.85, 0.90, 0.95)
        private val threePercents = arrayOf(0.80, 0.85, 0.90)
    }
}

