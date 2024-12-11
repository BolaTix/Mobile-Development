package com.example.bolatix.ui.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.bolatix.ui.fragments.BookedMatchFragment
import com.example.bolatix.ui.fragments.UpcomingTicketFragment


class TicketViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = UpcomingTicketFragment()
            1 -> fragment = BookedMatchFragment()
        }
        return fragment as Fragment
    }
}