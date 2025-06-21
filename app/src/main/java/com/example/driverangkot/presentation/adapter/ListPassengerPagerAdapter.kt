package com.example.driverangkot.presentation.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.driverangkot.presentation.listpassenger.pickup.PickUpFragment
import com.example.driverangkot.presentation.listpassenger.waiting.WaitingFragment

class ListPassengerPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = PickUpFragment()
            1 -> fragment = WaitingFragment()
        }
        return fragment as Fragment
    }

}