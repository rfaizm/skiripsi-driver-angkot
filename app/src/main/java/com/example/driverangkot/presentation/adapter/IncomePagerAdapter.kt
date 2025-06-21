package com.example.driverangkot.presentation.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.driverangkot.presentation.income.daily.DailyFragment
import com.example.driverangkot.presentation.income.weekly.WeeklyFragment

class IncomePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = DailyFragment()
            1 -> fragment = WeeklyFragment()
        }
        return fragment as Fragment
    }


}