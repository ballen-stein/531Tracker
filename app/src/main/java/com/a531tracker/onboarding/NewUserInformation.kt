package com.a531tracker.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.a531tracker.R
import com.a531tracker.databinding.ActivityNewuserExplanationBinding
import com.a531tracker.lifts.SetLiftsActivity
import com.a531tracker.tools.AppConstants

class NewUserInformation : AppCompatActivity() {

    private lateinit var binding: ActivityNewuserExplanationBinding

    private lateinit var fragmentHolder: FrameLayout

    private var userSteps = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewuserExplanationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fragmentHolder = binding.fragmentNewUserFrame

        binding.progressPartTwo.isEnabled = false
        binding.progressPartThree.isEnabled = false

        supportFragmentManager.beginTransaction()
                .replace(fragmentHolder.id, NewUserFragment().newInstance(userSteps))
                .commit()

        userSteps++
        binding.newUserContinue.setOnClickListener {
            if (userSteps < 3) {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fragment_in_right, R.anim.fragment_out_left, 0, 0)
                        .replace(fragmentHolder.id, NewUserFragment().newInstance(userSteps))
                        .commit()
                incrementProgression()
                userSteps++
            } else {
                val intent = Intent(this, SetLiftsActivity::class.java)
                        .putExtra(AppConstants.FRESH_LAUNCH, true)
                startActivity(intent)
                finish()
                overridePendingTransition(R.anim.enter_right,R.anim.exit_left)
            }
        }
    }

    private fun incrementProgression() {
        when (userSteps) {
            1 -> {
                binding.progressPartTwo.isEnabled = true
                binding.progressLine.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSecondary))
            }
            2 -> {
                binding.progressPartThree.isEnabled = true
                binding.progressLineRight.setBackgroundColor(ContextCompat.getColor(this, R.color.colorSecondary))
                binding.newUserContinue.text = getString(R.string.new_user_submit_lifts)
            }
        }
    }
}