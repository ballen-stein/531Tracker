package com.a531tracker.week

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.a531tracker.databinding.FragmentWeekInformationBinding

class WeekFragment : Fragment(), ViewBinding {

    private lateinit var binding: FragmentWeekInformationBinding

    fun newInstance() : WeekFragment {
        return WeekFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWeekInformationBinding.inflate(inflater)

        return root
    }

    override fun getRoot(): View {
        return binding.root
    }

}