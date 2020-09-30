package com.a531tracker.DetailFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

import androidx.fragment.app.Fragment

import com.a531tracker.R

class InformationFragment : Fragment() {

    private var descriptionHeader: TextView? = null
    private var descriptionInfo: TextView? = null
    private var cancelFrame: FrameLayout? = null

    private var clickListener: InformationFragmentListener? = null

    interface InformationFragmentListener {
        fun closeInformation()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.information_fragment, container, false)
        setViews(view)
        if (arguments != null) {
            setCancelButton()
            val headerText = arguments!!.getString("Header")
            descriptionHeader!!.text = headerText
            val descText = arguments!!.getString("Description")
            descriptionInfo!!.text = descText
        }
        return view
    }


    private fun setViews(v: View) {
        descriptionHeader = v.findViewById(R.id.fragment_settings_description_header)
        descriptionInfo = v.findViewById(R.id.fragment_settings_description_text)
        cancelFrame = v.findViewById(R.id.fragment_cancel_frame)
    }


    private fun setCancelButton() {
        cancelFrame!!.setOnClickListener { clickListener!!.closeInformation() }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        clickListener = context as InformationFragmentListener
    }


    override fun onDetach() {
        super.onDetach()
        clickListener = null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        setToNull()
    }


    private fun setToNull() {
        descriptionHeader = null
        descriptionInfo = null
        cancelFrame = null
    }

    companion object {

        fun newInstance(header: String, desc: String): InformationFragment {
            val fragment = InformationFragment()
            val bundle = Bundle()
            bundle.putString("Header", header)
            bundle.putString("Description", desc)
            fragment.arguments = bundle
            return fragment
        }
    }
}