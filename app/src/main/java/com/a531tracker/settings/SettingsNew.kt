package com.a531tracker.settings

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.a531tracker.BaseActivity
import com.a531tracker.databinding.ActivityUserSettingsBinding
import com.a531tracker.mvpbase.DependencyInjectorClass

class SettingsNew : BaseActivity(), ViewBinding {

    private lateinit var binding: ActivityUserSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSettingsBinding.inflate(layoutInflater)
        setContentView(root)


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        supportFragmentManager.beginTransaction().replace(
                binding.settingsFragment.id,
                SettingsFragment(this, DependencyInjectorClass())
        ).commit()
    }

    override fun getRoot(): View {
        return binding.root
    }
}