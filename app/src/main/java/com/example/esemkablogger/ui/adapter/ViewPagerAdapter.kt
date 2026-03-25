package com.example.esemkablogger.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.esemkablogger.ui.fargment.HomeFragment
import com.example.esemkablogger.ui.fargment.ProfileFragment
import com.example.esemkablogger.ui.fargment.UserFragment

class ViewPagerAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                HomeFragment()
            }
            1 -> {
                UserFragment()
            }
            2 -> {
                ProfileFragment()
            }
            else -> {
                HomeFragment()
            }
        }
    }
}