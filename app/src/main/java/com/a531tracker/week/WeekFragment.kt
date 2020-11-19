package com.a531tracker.week

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.databinding.FragmentWeekInformationBinding
import com.a531tracker.databinding.WeekDataBinding
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils
import kotlinx.android.synthetic.main.week_data.view.*

class WeekFragment(private val weekToShow: Int,  private val liftName: String, private val swapLiftName: String) : Fragment(), ViewBinding {

    private lateinit var binding: FragmentWeekInformationBinding

    private lateinit var weekRecyclerView: WeekRecyclerView

    private lateinit var databaseRepository: DatabaseRepository

    private lateinit var prefUtils: PreferenceUtils

    private val appUtils = AppUtils().getInstance()

    private var extraDeload: Boolean = false

    private var hideExtras: Boolean = false

    private var coreSwap: Boolean = false

    private var usingKgs: Boolean = false

    private var altFormat: Boolean = false

    fun newInstance(position: Int) : WeekFragment {
        return WeekFragment(weekToShow = position, liftName = liftName, swapLiftName = swapLiftName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWeekInformationBinding.inflate(inflater)
        databaseRepository = DatabaseRepository(binding.root.context)
        prefUtils = PreferenceUtils.getInstance(binding.root.context)
        extraDeload = prefUtils.getPreference(binding.root.context.getString(R.string.preference_deload_key)) ?: false
        hideExtras = prefUtils.getPreference(binding.root.context.getString(R.string.preference_remove_extras_key)) ?: false
        coreSwap = prefUtils.getPreference(binding.root.context.getString(R.string.preference_swap_extras_key)) ?: false
        usingKgs = prefUtils.getPreference(binding.root.context.getString(R.string.preference_kilogram_key)) ?: false
        altFormat = prefUtils.getPreference(binding.root.context.getString(R.string.preference_split_variant_extra_key)) ?: false

        weekRecyclerView = WeekRecyclerView(binding.root.context, binding, this)
        weekRecyclerView.setData(weekToShow)

        return root
    }

    override fun getRoot(): View {
        return binding.root
    }

    fun getWeeklyData(weekToShow: Int): ArrayList<String> {
        return ArrayList<String>().apply {
            addAll(databaseRepository.getWeekData(weekToShow)!![0]!!)
            if (weekToShow == 3 && extraDeload) {
                addAll(databaseRepository.getWeekData(weekToShow)!![1]!!)
            }
            if (weekToShow != 3) {
                addAll(databaseRepository.getWeekData(weekToShow)!![1]!!)
                if (!hideExtras) {
                    addAll(databaseRepository.getWeekData(weekToShow)!![2]!!)
                }
            }
        }
    }

    private var jokerWeight = 0.0

    private var jokerSetNum = 1

    private lateinit var jokerReferenceLocation: View

    fun putArguments(bundle: Bundle) {
        val addedReps = bundle[AppConstants.JOKER_REPS]
        jokerReferenceLocation.joker_layout.visibility = View.VISIBLE
        val jokerText = "${getString(R.string.joker_set)} $jokerSetNum"
        jokerReferenceLocation.joker_set_num.text = jokerText
        jokerSetNum++
        jokerWeight *= 1.05
        val text = if (usingKgs) {
            val jokerFromKg = appUtils.getPound(jokerWeight)
            String.format("%.2f", appUtils.getJokerWeight(true, jokerFromKg).toFloat())
        } else {
            appUtils.getJokerWeight(false, jokerWeight)
        }
        jokerReferenceLocation.joker_amount.text = text

        val reps = getReps(addedReps as Int)
        jokerReferenceLocation.joker_reps.text = reps
        jokerReferenceLocation.joker_check.isChecked = false
    }

    private fun getReps(reps: Int): String {
        return when (reps) {
            1 -> {
                if (altFormat) "1x3" else "1x1"
            }
            3 -> {
                if (altFormat) "1x6" else "1x3"
            }
            5 -> {
                if (altFormat) "1x8" else "1x5"
            }
            else -> {
                "1x1+"
            }
        }
    }

    fun hideExtras(): Boolean {
        return hideExtras
    }

    fun swapExtras(): Boolean {
        return coreSwap
    }

    fun showKilos(): Boolean {
        return usingKgs
    }

    fun getFslSwapWeight() {
        val swapTm = databaseRepository.getLift(getSwapName())?.trainingMax

    }

    fun setJokerReferenceLocation(itemView: View, weight: String) {
        jokerReferenceLocation = itemView
        jokerWeight = weight.toDouble()
    }

    fun getSwapName(): String {
        return swapLiftName
    }

    fun getWeeklyReps(): ArrayList<String> {
        return databaseRepository.getRepData()
    }

    fun getWeightBreakdown(weekToShow: Int): ArrayList<ArrayList<Double>> {
        return databaseRepository.getWeightBreakdown(weekToShow)
    }

    class WeekRecyclerView(private val mContext: Context, private val binding: FragmentWeekInformationBinding, private val fragment: WeekFragment) {

        private lateinit var weekAdapter: WeekAdapter

        private lateinit var recyclerView: RecyclerView

        private lateinit var weekData: ArrayList<String>

        private lateinit var repData: ArrayList<String>

        private lateinit var breakdownData: ArrayList<ArrayList<Double>>

        fun setData(weekToShow: Int) {
            recyclerView = binding.weekRecycler
            weekData = fragment.getWeeklyData(weekToShow)
            repData = fragment.getWeeklyReps()
            breakdownData = fragment.getWeightBreakdown(weekToShow)
            weekAdapter = WeekAdapter(liftDataset = weekData,
                    repData = repData,
                    breakdown = breakdownData,
                    deload = weekToShow == 3,
                    liftName = fragment.liftName,
                    hideExtras = fragment.hideExtras(),
                    swapExtras = fragment.swapExtras(),
                    swapLiftName = fragment.getSwapName(),
                    usingKgs = fragment.showKilos(),
                    apputils = fragment.appUtils,
                    weekToShow = weekToShow,
                    fragment = fragment
            )
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mContext)
                recycledViewPool.setMaxRecycledViews(0, 0)
                adapter = weekAdapter
            }

            weekAdapter.notifyDataSetChanged()
        }
    }

    class WeekAdapter(
            private val liftDataset: ArrayList<String>,
            private val repData: ArrayList<String>,
            private val breakdown: ArrayList<ArrayList<Double>>,
            private val deload: Boolean,
            private val liftName: String,
            private val hideExtras: Boolean,
            private val swapExtras: Boolean,
            private val swapLiftName: String,
            private val usingKgs: Boolean,
            private val apputils: AppUtils,
            private val weekToShow: Int,
            private val fragment: WeekFragment
    ) : RecyclerView.Adapter<WeekAdapter.ViewHolder>(), ViewBinding {

        lateinit var binding: WeekDataBinding

        private var hideExtrasNow: Boolean = false

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            binding = WeekDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val liftData = liftDataset[position]

            /*
            val params: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT)
            when(position) {
                0,4,8 -> params.setMargins(24,48,24,0)
                3,7 -> params.setMargins(24,0,24,32)
                13 -> params.setMargins(24, 0, 24, 48)
                else -> params.setMargins(24,0,24,0)
            }
            holder.itemView.coordinator.layoutParams = params*/
            holder.bind(liftData)
            if (liftData == AppConstants.SET_WARMUP
                    || liftData == AppConstants.SET_CORE
                    || liftData == AppConstants.SET_BBB
                    || liftData == AppConstants.SET_DELOAD
                    || liftData == AppConstants.SET_FSL) {
                holder.itemView.weight_layout.visibility = View.GONE
                holder.itemView.weight_breakdown_layout.visibility = View.GONE

                holder.itemView.header_text.apply {
                    text = if (deload) {
                        "Deload Sets"
                    } else if (swapExtras && liftData == AppConstants.SET_BBB || liftData == AppConstants.SET_FSL) {
                        val text = "$swapLiftName $liftData"
                        text
                    } else {
                        val text = "$liftName $liftData"
                        text
                    }
                    visibility = if(deload && position > 3) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                }
                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0,0,0,0)
                holder.itemView.coordinator.apply {
                    layoutParams = params
                    cardElevation = 0f
                    setBackgroundColor(resources.getColor(R.color.colorSurface, null))
                }
                if (liftData == AppConstants.SET_DELOAD || liftData == AppConstants.SET_FSL && hideExtras) {
                    hideExtrasNow = true
                }
            } else {
                holder.itemView.header_layout.visibility = View.GONE
                holder.itemView.weight_reps.text = when (weekToShow) {
                    0 -> {
                        repData[5]
                    }
                    1 -> {
                        "${repData[5]}+"
                    }
                    2 -> {
                        "${repData[5]}+"
                    }
                    else -> {
                        repData[position]
                    }
                }

                holder.itemView.weight_reps.text = when (position) {
                    5 -> {
                        if (weekToShow != 2) {
                            repData[5+weekToShow]
                        } else {
                            repData[5]
                        }
                    }
                    6 -> {
                        if (weekToShow != 2) {
                            repData[5+weekToShow]
                        } else {
                            repData[6]
                        }
                    }
                    7 -> {
                        "${repData[5+weekToShow]}+"
                    }
                    else -> {
                        repData[position]
                    }
                }
                holder.itemView.weight_amount.text = liftData
                val weightBreakdown = breakdown[position]
                holder.itemView.bar_weight.text = if (usingKgs) {
                    "${apputils.getJokerWeight(usingKgs, weightBreakdown[0])} kgs"
                } else {
                    "${weightBreakdown[0]} lbs"
                }
                holder.itemView.weight_breakdown.text = formatBreakdownText(weightBreakdown, usingKgs)
            }

            if (position == 7) {
                fragment.setJokerReferenceLocation(holder.itemView, liftData)
            }

            holder.itemView.weight_breakdown_layout.setOnClickListener{
                when (holder.itemView.divider_bar.visibility) {
                    View.VISIBLE -> {
                        holder.itemView.apply {
                            divider_bar.visibility = View.GONE
                            weight_breakdown_info.visibility = View.GONE
                            display_further_info_arrow.isSelected = false
                        }
                    }
                    else -> {
                        holder.itemView.apply {
                            divider_bar.visibility = View.VISIBLE
                            weight_breakdown_info.visibility = View.VISIBLE
                            display_further_info_arrow.isSelected = true
                        }
                    }
                }
            }
        }

        private fun formatBreakdownText(weightBreakdown: ArrayList<Double>, usingKgs: Boolean): String {
            var bdString = ""

            for (value in 1 until weightBreakdown.size) {
                bdString += if (usingKgs) {
                    "${apputils.getJokerWeight(usingKgs, weightBreakdown[value])} "
                } else {
                    "${weightBreakdown[value]} "
                }
                bdString += " | "
            }
            bdString += "&"
            return bdString.replace("| &", if (usingKgs) "kgs" else "lbs")
        }

        class ViewHolder(binding: WeekDataBinding) : RecyclerView.ViewHolder(binding.root) {
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
}