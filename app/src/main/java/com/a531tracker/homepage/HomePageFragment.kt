package com.a531tracker.homepage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.databinding.FragmentHomepageInformationBinding
import com.a531tracker.databinding.HomepageCompletedWeekDataBinding
import com.a531tracker.databinding.HomepageDataBinding
import com.a531tracker.tools.AppConstants
import kotlinx.android.synthetic.main.homepage_completed_week_data.view.*
import kotlinx.android.synthetic.main.homepage_data.view.*

class HomePageFragment : Fragment(), ViewBinding {

    private lateinit var binding: FragmentHomepageInformationBinding

    private lateinit var homeRecyclerView: HomePageRecycler

    private lateinit var databaseRepository: DatabaseRepository

    private var thisWeek: Int = 0

    fun newInstance(): HomePageFragment {
        return HomePageFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomepageInformationBinding.inflate(layoutInflater)
        databaseRepository = DatabaseRepository(binding.root.context)

        homeRecyclerView = HomePageRecycler(root.context, binding, this)
        homeRecyclerView.setHomeData()

        return root
    }

    override fun getRoot(): View {
        return binding.root
    }

    fun getTrainingMax(): HashMap<String, Int> {
        return databaseRepository.getTrainingMaxes()
    }

    fun getLifts(backup: Boolean): MutableList<String> {
        return if (!backup) lifts else liftsBackup
    }

    fun getWeekStats(weight: Int): HashMap<String, HashMap<Int, Int>> {
        databaseRepository.setCompletedLifts(weight)
        return databaseRepository.getCompletedLifts()
    }

    fun startWeekActivity(liftName: String) {
        val swapLift = when (liftName) {
            AppConstants.BENCH -> AppConstants.SQUAT
            AppConstants.SQUAT -> AppConstants.BENCH
            AppConstants.DEADLIFT -> AppConstants.OVERHAND
            AppConstants.OVERHAND -> AppConstants.DEADLIFT
            else -> "na"
        }
        (root.context as HomePageActivity).startWeekActivity(hashMapOf(AppConstants.MAIN_LIFT to liftName, AppConstants.SWAP_LIFT to swapLift))
    }

    fun setThisWeek(currentWeek: Int) {
        thisWeek = currentWeek
    }

    fun getThisWeek(): Int {
        return thisWeek
    }

    class HomePageRecycler(private val mContext: Context, private val binding: FragmentHomepageInformationBinding, private val fragment: HomePageFragment) {

        private lateinit var homePageAdapter: HomePageAdapter

        private lateinit var recyclerView: RecyclerView

        private lateinit var liftNamesList: MutableList<String>

        private lateinit var liftDataMap: HashMap<String, Int>

        fun setHomeData() {
            recyclerView = binding.homepageRecycler
            liftDataMap = fragment.getTrainingMax()
            liftNamesList = if (liftDataMap.containsKey(AppConstants.WEEK_BENCH)) {
                fragment.getLifts(false)
            } else {
                fragment.getLifts(true)
            }

            homePageAdapter = HomePageAdapter(
                    liftDataset = liftNamesList,
                    trainingMaxMap = liftDataMap,
                    fragment = fragment,
                    context = mContext
            )
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mContext)
                recycledViewPool.setMaxRecycledViews(0, 0)
                adapter = homePageAdapter
            }

            homePageAdapter.notifyDataSetChanged()
        }
    }

    class HomePageAdapter(private val liftDataset: MutableList<String>, private val trainingMaxMap: HashMap<String, Int>, private val fragment: HomePageFragment, private val context: Context) : RecyclerView.Adapter<HomePageAdapter.ViewHolder>(), ViewBinding {

        lateinit var binding: HomepageDataBinding

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            binding = HomepageDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val liftData = liftDataset[position]
            holder.bind(liftData)

            val liftName = if (trainingMaxMap.containsKey(liftData)) {
                AppConstants.LIFT_NAMES_REVERSE_MAP[liftData]!!
            } else {
                AppConstants.LIFT_NAMES_MAP[liftData]!!
            }

            holder.itemView.homepage_lift_name.text = liftName

            val trainingMax = trainingMaxMap[liftData]!!

            holder.itemView.homepage_lift_current_weight.text = trainingMax.toString()

            val completedWeeksRecycler = CompletedWeeksRecycler(fragment, context, holder.itemView.homepage_completed_week)
            completedWeeksRecycler.setCompletedData(trainingMax, liftData)

            //val viewWeekText = "${context.resources.getString(R.string.homepage_go_to_week)} (${fragment.getThisWeek()})"
            val viewWeekText = context.resources.getString(R.string.homepage_go_to_week).replace("this", liftName)
            holder.itemView.homepage_go_to_week.text = viewWeekText

            holder.itemView.homepage_go_to_week.setOnClickListener {
                fragment.startWeekActivity(liftName)
            }
        }

        class ViewHolder(binding: HomepageDataBinding) : RecyclerView.ViewHolder(binding.root) {
            private lateinit var liftData: String

            fun bind(lift: String) {
                liftData = lift
            }

        }

        override fun getItemCount(): Int {
            return liftDataset.size
        }

        override fun getRoot(): View {
            return binding.root
        }
    }

    //   Data for the Recycler View inside the base Recycler View

    class CompletedWeeksRecycler(private val fragment: HomePageFragment, private val context: Context, private val binding: RecyclerView) {

        private lateinit var completedWeeksAdapter: CompletedWeeksAdapter

        private lateinit var recyclerView: RecyclerView

        private lateinit var liftNamesList: MutableList<String>

        private lateinit var liftDataMap: HashMap<String, HashMap<Int, Int>>

        fun setCompletedData(liftWeight: Int, liftName: String) {
            recyclerView = binding
            liftDataMap = fragment.getWeekStats(liftWeight)
            liftNamesList = if (liftDataMap.containsKey(AppConstants.WEEK_BENCH)) {
                fragment.getLifts(false)
            } else {
                fragment.getLifts(true)
            }

            val dataToShow = liftDataMap[liftName]

            val liftWeek = ArrayList<Int>()
            val liftSuccess = ArrayList<Int>()
            for (value in 0 until 3) {
                liftWeek.add(value + 1)
                liftSuccess.add(dataToShow?.get(value+1) ?: -1)
            }

            val layoutManager2 = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            completedWeeksAdapter = CompletedWeeksAdapter(
                    liftWeek = liftWeek,
                    liftSuccesses = liftSuccess,
                    fragment = fragment
            )

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = layoutManager2
                recycledViewPool.setMaxRecycledViews(0, 5)
                adapter = completedWeeksAdapter
            }

            completedWeeksAdapter.notifyDataSetChanged()
        }
    }

    class CompletedWeeksAdapter(private val liftWeek: ArrayList<Int>, private val liftSuccesses: ArrayList<Int>, private val fragment: HomePageFragment) : RecyclerView.Adapter<CompletedWeeksAdapter.ViewHolder>(), ViewBinding {

        lateinit var binding: HomepageCompletedWeekDataBinding

        var setPosition = true

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            binding = HomepageCompletedWeekDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val liftData = liftWeek[position]
            holder.bind(liftData)

            val weekText = "Week $liftData"
            holder.itemView.homepage_required_week.text = weekText

            val repsText = if (liftSuccesses[position] >=0 ) {
                "${liftSuccesses[position]} reps"
            } else {
                "NONE"
            }
            holder.itemView.homepage_required_percent.text = repsText

            val showCheckMark = if (repsText == "NONE") {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
            holder.itemView.homepage_required_completed.visibility = showCheckMark

            /*
            if (setPosition) {
                if (liftSuccesses[position] <=0) {
                    Log.d("TestingData", "Hitting position for $position")
                    fragment.setThisWeek(position+1)
                    setPosition = false
                }
            }
            */
        }

        class ViewHolder(binding: HomepageCompletedWeekDataBinding) : RecyclerView.ViewHolder(binding.root) {
            private var liftData: Int = 0

            fun bind(lift: Int) {
                liftData = lift
            }
        }

        override fun getItemCount(): Int {
            return liftWeek.size
        }

        override fun getRoot(): View {
            return binding.root
        }
    }

    companion object {
        val lifts: MutableList<String> = mutableListOf(AppConstants.WEEK_BENCH, AppConstants.WEEK_DEADLIFT, AppConstants.WEEK_OVERHAND, AppConstants.WEEK_SQUAT)
        val liftsBackup: MutableList<String> = mutableListOf("Bench", "Squat", "Overhand Press", "Deadlift")
    }
}