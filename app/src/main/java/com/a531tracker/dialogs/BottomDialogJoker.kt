package com.a531tracker.dialogs

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.viewbinding.ViewBinding
import com.a531tracker.R
import com.a531tracker.database.DatabaseRepository
import com.a531tracker.databinding.BottomDialogJokerBinding
import com.a531tracker.tools.PreferenceUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialogJoker(private val mContext: Context, private val dr: DatabaseRepository): BottomSheetDialogFragment(), ViewBinding {

    lateinit var binding: BottomDialogJokerBinding

    private lateinit var listener: BottomDialogJokerClick

    private lateinit var prefUtils: PreferenceUtils

    interface BottomDialogJokerClick {
        fun confirmNewSet(addedReps: Int)
    }

    fun newInstance(): BottomDialogJoker {
        return BottomDialogJoker(mContext, dr)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BottomDialogJokerClick) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomDialogJokerBinding.inflate(inflater)
        prefUtils = PreferenceUtils.getInstance(binding.root.context)

        val altFormat: Boolean? = prefUtils.getPreference(binding.root.context.getString(R.string.preference_split_variant_extra_key)) as Boolean

        if (altFormat != null && altFormat) {
            binding.jokerOne.text = binding.root.context.getString(R.string.bottom_dialog_joker_alt_three)
            binding.jokerThree.text = binding.root.context.getString(R.string.bottom_dialog_joker_alt_six)
            binding.jokerFive.text = binding.root.context.getString(R.string.bottom_dialog_joker_alt_eight)
        }

        binding.inputCancelButton.setOnClickListener {
            dismiss()
        }

        binding.jokerOne.setOnClickListener {
            sendJokerReps(1)
        }
        binding.jokerThree.setOnClickListener {
            sendJokerReps(3)
        }
        binding.jokerFive.setOnClickListener {
            sendJokerReps(5)
        }

        return binding.root
    }

    private fun sendJokerReps(repsToSend: Int) {
        listener.confirmNewSet(repsToSend)
        dismiss()
    }

    override fun getRoot(): View {
        return binding.root
    }
}