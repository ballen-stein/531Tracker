package com.a531tracker.onboarding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.a531tracker.databinding.FragmentNewUserOneBinding
import com.a531tracker.databinding.FragmentNewUserTwoBinding
import com.a531tracker.databinding.FragmentNewUserZeroBinding
import com.a531tracker.tools.AppConstants

class NewUserFragment : Fragment() {

    private lateinit var bindingOne: FragmentNewUserZeroBinding
    private lateinit var bindingTwo: FragmentNewUserOneBinding
    private lateinit var bindingThree: FragmentNewUserTwoBinding

    fun newInstance(userStep: Int): NewUserFragment {
        val bundle = Bundle().apply {
            putInt(AppConstants.NEW_USER_ONBOARD, userStep)
        }

        return NewUserFragment().apply { arguments = bundle }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val userStep = arguments?.get(AppConstants.NEW_USER_ONBOARD) ?: 3
        Log.d("NewUserFragment", "At step $userStep")
        val binding = when (userStep) {
            0 -> {
                FragmentNewUserZeroBinding.inflate(layoutInflater)
            }
            1 -> {
                FragmentNewUserOneBinding.inflate(layoutInflater)
            }
            2 -> {
                FragmentNewUserTwoBinding.inflate(layoutInflater)
            }
            else -> {
                FragmentNewUserTwoBinding.inflate(layoutInflater)
            }
        }

        return binding.root
    }
}