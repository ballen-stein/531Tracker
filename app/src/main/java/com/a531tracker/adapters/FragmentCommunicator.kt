package com.a531tracker.adapters

interface FragmentCommunicator {
    fun startWeekActivity(hashMap: HashMap<String, String>)

    fun launchTool(tool: Int)
}