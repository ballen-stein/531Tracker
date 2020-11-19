package com.a531tracker.homepage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.a531tracker.ObjectBuilders.GraphDataHolder
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.databinding.CompoundGraphsDataBinding
import com.a531tracker.databinding.FragmentToolspageBinding
import com.a531tracker.tools.AppConstants
import com.a531tracker.tools.AppUtils
import com.a531tracker.tools.PreferenceUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class ToolsPageFragment : Fragment(), ViewBinding {

    private lateinit var binding: FragmentToolspageBinding

    private lateinit var graphRecyclerView: GraphRecyclerView

    private lateinit var dr: DatabaseRepository

    private lateinit var prefUtils: PreferenceUtils

    private var cycleVal = 0

    private var percentVal = 0.0f

    private var extrasDisabled = false

    private var usingKgs = false

    fun newInstance(): ToolsPageFragment {
        return ToolsPageFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentToolspageBinding.inflate(layoutInflater)
        dr = DatabaseRepository(binding.root.context)
        prefUtils = PreferenceUtils.getInstance(binding.root.context)
        extrasDisabled = prefUtils.getPreference(getString(R.string.preference_remove_extras_key)) ?: false
        usingKgs = prefUtils.getPreference(binding.root.context.getString(R.string.preference_kilogram_key)) ?: false
        setViews()

        graphRecyclerView = GraphRecyclerView(binding.root.context, binding, this)
        graphRecyclerView.setData()

        return root
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val seekProgress = (dr.getUserPercentList("Bench")[0] * 100).toInt()
        binding.liftToolsPercentSeekbar.apply {
            progress = seekProgress
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                min = 30
                max = 90
            }
        }
    }

    private fun setViews() {
        cycleVal = dr.getCycle()

        binding.liftToolsCycleValue.text = cycleVal.toString()
        binding.liftToolsUpdateCycleBtn.setOnClickListener {
            (root.context as HomePageActivity).launchTool(AppConstants.NAVIGATION_TOOL_CYCLE)
        }

        if (extrasDisabled) {
            binding.liftToolsPercentSeekbar.isEnabled = false
            binding.liftToolsUpdatePercentBtn.isEnabled = false
            binding.liftToolsPercentBarText.text = root.context.getString(R.string.extras_disabled)
        } else {
            binding.liftToolsPercentSeekbar.isEnabled = true
            binding.liftToolsUpdatePercentBtn.isEnabled = true
        }
        binding.liftToolsPercentSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                percentVal = AppUtils().getInstance().normalizePercent(progress.toFloat())

                binding.liftToolsPercentBarText.apply {
                    text = percentVal.toString()
                    if (percentVal > 90f || percentVal < 30f) {
                        setTextColor(root.context.getColor(R.color.colorRed))
                    } else {
                        setTextColor(root.context.getColor(R.color.highWhite))
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        binding.liftToolsUpdatePercentBtn.setOnClickListener {
            (root.context as HomePageActivity).launchTool(AppConstants.NAVIGATION_TOOL_PERCENT)
        }

        binding.liftToolsUpdateLiftsBtn.setOnClickListener {
            (root.context as HomePageActivity).launchTool(AppConstants.NAVIGATION_TOOL_TM)
        }
    }

    fun kilos(): Boolean {
        return usingKgs
    }

    fun getGraphData(liftName: String): ArrayList<GraphDataHolder>? {
        return dr.getAmrapGraph(liftName)
    }

    /*
    fun setGraphs() {
        val benchGraph = dr.getAmrapGraph("Bench")
        val chart = binding.root.chart1
        val dataPoints = ArrayList<ILineDataSet>()
        val entryList = ArrayList<Entry>()

        if (benchGraph != null) {
            /*for ((cycle,data) in benchGraph.withIndex()) {
                val min = 1
                val max = benchGraph.size * 3

                val chartText = "Your ${data.compoundName} data"
                AmrapGrapingTool().setChartTheme(chart, binding.root.context, min, max, chartText)
                dataPoints.add(AmrapGrapingTool().setGraphData(data, chart, binding.root.context, cycle))
            }*/
            for (data in benchGraph) {
                val min = 0
                val max = (benchGraph.size * 3) + 1
                val chartText = "Your ${data.compoundName} data from Cycle 1 " + if(benchGraph.size > 1) {
                    "to ${benchGraph.size}"
                } else {
                    ""
                }

                AmrapGrapingTool().setChartTheme(chart, binding.root.context, min, max, chartText)
                entryList.addAll(AmrapGrapingTool().createEntriesList(data, usingKgs))
            }
        }

        val cycleHeader = "Bench Format Stats"
        val lineDataSet = LineDataSet(entryList, cycleHeader)
        AmrapGrapingTool().setLineDataSetTheme(lineDataSet, binding.root.context, 1)
        dataPoints.add(lineDataSet)

        val lineData = LineData(dataPoints)
        chart.data = lineData
        chart.invalidate()
    }
*/

    class GraphRecyclerView(private val mContext: Context, private val binding: FragmentToolspageBinding, private val fragment: ToolsPageFragment) {

        private lateinit var graphAdapter: GraphAdapter

        private lateinit var recyclerView: RecyclerView

        private lateinit var compoundList: ArrayList<String>

        fun setData() {
            recyclerView = binding.graphRecycler
            compoundList = AppConstants.LIFT_ACCESS_LIST as ArrayList<String>
            graphAdapter = GraphAdapter(liftDataset = compoundList,
                    usingKg = fragment.kilos(),
                    fragment = fragment
            )
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
                recycledViewPool.setMaxRecycledViews(0, 0)
                adapter = graphAdapter
            }

            graphAdapter.notifyDataSetChanged()
        }
    }

    class GraphAdapter(private val liftDataset: ArrayList<String>, private val usingKg: Boolean, private val fragment: ToolsPageFragment) : RecyclerView.Adapter<GraphAdapter.ViewHolder>(), ViewBinding {

        lateinit var binding: CompoundGraphsDataBinding

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            binding = CompoundGraphsDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val liftData = liftDataset[position]

            holder.bind(liftData)
            setGraphs(binding.chart1, liftData)
        }

        private fun setGraphs(chart: LineChart, liftData: String) {
            val graphData = fragment.getGraphData(liftData)
            val dataPoints = ArrayList<ILineDataSet>()
            val entryList = ArrayList<Entry>()

            if (graphData != null && graphData.isNotEmpty()) {
                for (data in graphData) {
                    val min = 0
                    val max = (graphData.size * 3) + 1
                    val chartText = "Your ${data.compoundName} data from Cycle 1 " + if(graphData.size > 1) {
                        "to ${graphData.size}"
                    } else {
                        ""
                    }

                    AmrapGrapingTool().setChartTheme(chart, binding.root.context, min, max, chartText)
                    entryList.addAll(AmrapGrapingTool().createEntriesList(data, usingKg))
                }

                val cycleHeader = "Your $liftData Stats"
                val lineDataSet = LineDataSet(entryList, cycleHeader)
                AmrapGrapingTool().setLineDataSetTheme(lineDataSet, binding.root.context, 1)
                dataPoints.add(lineDataSet)

                val lineData = LineData(dataPoints)
                chart.data = lineData
                chart.invalidate()
                AmrapGrapingTool().resetPointer()
            } else {
                binding.emptyState.visibility = View.VISIBLE
            }
        }

        class ViewHolder(binding: CompoundGraphsDataBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun getRoot(): View {
        return binding.root
    }

    companion object {
        val lifts: MutableList<String> = mutableListOf(AppConstants.WEEK_BENCH, AppConstants.WEEK_DEADLIFT, AppConstants.WEEK_OVERHAND, AppConstants.WEEK_SQUAT)
        val liftsBackup: MutableList<String> = mutableListOf("Bench", "Squat", "Overhand Press", "Deadlift")
    }
}