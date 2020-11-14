package com.a531tracker.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.a531tracker.R
import com.a531tracker.databinding.BottomDialogLayoutBinding
import com.a531tracker.homepage.HomePageActivity
import com.a531tracker.lifts.SetLiftsActivity
import com.a531tracker.settings.SettingsNew
import com.a531tracker.tools.AppConstants
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_dialog_layout.*

class BottomDialog(private val mContext: Context): BottomSheetDialogFragment(), ViewBinding {

    lateinit var binding: BottomDialogLayoutBinding

    private var showMenu = true
    private var currentWeek: Int = 0

    private lateinit var listener: BottomDialogClicks

    interface BottomDialogClicks {
        fun submitAmrap(text: String, currentWeek: Int)
    }

    fun newInstance(argumentMap: HashMap<Int, Int>): BottomDialog {
        val bundle = Bundle()
        val argumentKeys = argumentMap.keys
        bundle.apply {
            for (value in argumentKeys) {
                when (value) {
                    AppConstants.NAVIGATION_MENU -> {
                        if (argumentMap[value] == 0) {
                            putBoolean(AppConstants.DIALOG_MENU, true)
                        } else {
                            putBoolean(AppConstants.DIALOG_MENU, false)
                        }
                    }
                    AppConstants.NAVIGATION_WEEK -> {
                        putInt(AppConstants.CURRENT_WEEK_DISPLAYED, argumentMap[value] ?: 1)
                    }
                    AppConstants.NAVIGATION_AMRAP -> {
                        val amrapVal = argumentMap[value] ?: -1
                        putString(AppConstants.AMRAP_LAST_WEEK, if (amrapVal >= 0) {
                            argumentMap[value].toString()
                        } else {
                            "N/A"
                        })
                    }
                }
            }
        }

        return BottomDialog(mContext).apply { arguments = bundle }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BottomDialogClicks) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomDialogLayoutBinding.inflate(inflater)
        if (arguments != null) {
            showMenu = arguments?.getBoolean("view_menu") ?: true
            currentWeek = arguments?.getInt(AppConstants.CURRENT_WEEK_DISPLAYED) ?: 0
            val headerText = "${getString(R.string.amrap_reps_completed)} for Week #$currentWeek"

            if (!showMenu) {
                binding.inputHeaderText.text = headerText
                binding.inputView.visibility = View.VISIBLE
                binding.navigationView.visibility = View.GONE

                binding.inputLastWeekAmrap.text = arguments?.getString(AppConstants.AMRAP_LAST_WEEK) ?: "N/A"

                setInputButtons()
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navigation_view.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.app_bar_settings -> {
                    val intent = Intent(mContext, SettingsNew::class.java)
                    startActivity(intent)
                    dismiss()
                }
                R.id.app_bar_home -> {
                    if (mContext !is HomePageActivity) {
                        startActivity(Intent(mContext, HomePageActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        })
                    } else {
                        dismiss()
                    }
                }
                R.id.app_bar_update -> {
                    startActivity(Intent(mContext, SetLiftsActivity::class.java).apply{
                        putExtra(AppConstants.FRESH_LAUNCH, false)
                    })
                    dismiss()
                }
                R.id.app_bar_help -> {
                    val msgIntent = Intent(Intent.ACTION_SENDTO).apply {
                        type = "text/plain"
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, "ballenstein20@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "5/3/1 Fitness: Question about the app")
                    }

                    if (msgIntent.resolveActivity(mContext.packageManager) != null) {
                        startActivity(msgIntent)
                    }
                    dismiss()
                }
                R.id.app_bar_info -> {
                    val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder().apply {
                        setToolbarColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
                        addDefaultShareMenuItem()
                        setShowTitle(true)
                    }
                    builder.build().launchUrl(mContext, Uri.parse(AppConstants.JW_URL))
                    dismiss()
                }
            }
            true
        }
    }

    private fun setInputButtons() {
        binding.inputCancelButton.setOnClickListener {
            dialog?.dismiss()
        }

        binding.inputButton.setOnClickListener {
            getAmrapInfo(binding.inputAmrapValue.text.toString())
        }
    }

    private fun getAmrapInfo(text: String) {
        if (text.isNotBlank()) {
            listener.submitAmrap(text, this.currentWeek)
            dismiss()
        } else {
            binding.inputErrorMsg.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.navigation_menu, menu)
        if (showMenu) {
            binding.inputView.visibility = View.GONE
            binding.navigationView.visibility = View.VISIBLE
        }
    }

    override fun getRoot(): View {
        return binding.root
    }
}