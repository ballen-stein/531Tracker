package com.a531tracker.homepage

import android.content.Context
import androidx.core.content.ContextCompat
import com.a531tracker.ObjectBuilders.GraphDataHolder
import com.a531tracker.R
import com.a531tracker.tools.AppUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class AmrapGrapingTool {

    fun resetPointer() {
        xPointer = 1F
    }

    fun createEntriesList(data: GraphDataHolder, usingKgs: Boolean): ArrayList<Entry> {
        val entriesList = ArrayList<Entry>()

        entriesList.apply {
            add(Entry(xPointer,
                    if (usingKgs) {
                        appUtils.getKilo(data.tmWeekOne.toInt()).toFloat()
                    } else {
                        data.tmWeekOne
                    })
            )
            add(Entry(xPointer+1, if (usingKgs) {
                appUtils.getKilo(data.tmWeekTwo.toInt()).toFloat()
            } else {
                data.tmWeekTwo
            })
            )
            add(Entry(xPointer+2, if (usingKgs) {
                appUtils.getKilo(data.tmWeekThree.toInt()).toFloat()
            } else {
                data.tmWeekThree
            })
            )
        }
        xPointer += 3

        return entriesList
    }

    fun setLineDataSetTheme(lineDataSet: LineDataSet, mContext: Context, cycle: Int) {
        lineDataSet.apply {
            setCircleColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
            fillColor = ContextCompat.getColor(mContext, R.color.highWhite)
            valueTextColor = ContextCompat.getColor(mContext, R.color.highWhite)
            valueTextSize = 12f
            color = ContextCompat.getColor(mContext, if (cycle%2 ==0) R.color.colorSecondaryDark else R.color.colorTertiary)
        }
    }

    fun setChartTheme(chart: LineChart, mContext: Context, min: Int, max: Int, chartText: String) {
        chart.apply {
            description.apply {
                text = chartText
                textColor = ContextCompat.getColor(mContext, R.color.highWhite)
            }
            axisRight.apply {
                textColor = ContextCompat.getColor(mContext, R.color.highWhite)
                gridColor = ContextCompat.getColor(mContext, R.color.highWhite)
                axisLineColor = ContextCompat.getColor(mContext, R.color.highWhite)
                axisMinimum//reps min
                axisMaximum//reps max
                labelCount//max reps - min reps
            }
            axisLeft.apply {
                textColor = ContextCompat.getColor(mContext, R.color.highWhite)
                gridColor = ContextCompat.getColor(mContext, R.color.highWhite)
                axisLineColor = ContextCompat.getColor(mContext, R.color.highWhite)
                axisMinimum//reps min
                axisMaximum//reps max
                labelCount//max reps - min reps
            }

            background.apply {
                background = ContextCompat.getDrawable(mContext, R.color.colorPrimaryDark)
            }

            legend.apply {
                textColor = ContextCompat.getColor(mContext, R.color.highWhite)
            }

            description.apply {
                textColor = ContextCompat.getColor(mContext, R.color.highWhite)
                textSize = 12f
                text//Description text
            }

            xAxis.apply {
                gridColor = ContextCompat.getColor(mContext, R.color.highWhite)
                textColor = ContextCompat.getColor(mContext, R.color.highWhite)
                axisMinimum = min.toFloat()
                axisMaximum = max.toFloat()
                labelCount = 1 + ((max - min) / 5)
            }
        }
    }

    companion object {
        private var xPointer = 1F
        private val appUtils = AppUtils().getInstance()
    }
}