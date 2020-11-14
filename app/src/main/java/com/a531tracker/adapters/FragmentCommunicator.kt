package com.a531tracker.adapters

interface FragmentCommunicator {
    fun startWeekActivity(hashMap: HashMap<String, String>)

    fun submitAmrapValues(amrapValue: String, currentWeek: Int)
}