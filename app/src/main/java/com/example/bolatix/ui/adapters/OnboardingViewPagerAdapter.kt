package com.example.bolatix.ui.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bolatix.R
import com.example.bolatix.ui.fragments.OnboardingFragment


class OnboardingViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val context: Context
) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment.newInstance(
                context.getString(R.string.titleOnboarding1),
                context.getString(R.string.descOnboarding1),
                R.drawable.bg_onboarding_1
            )
            1 -> OnboardingFragment.newInstance(
                context.getString(R.string.titleOnboarding2),
                context.getString(R.string.descOnboarding2),
                R.drawable.bg_onboarding_2
            )
            else -> OnboardingFragment.newInstance(
                context.getString(R.string.titleOnboarding3),
                context.getString(R.string.descOnboarding3),
                R.drawable.bg_onboarding_3
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}