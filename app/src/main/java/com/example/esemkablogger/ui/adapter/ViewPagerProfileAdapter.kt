package com.example.esemkablogger.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.esemkablogger.ui.fargment.HomeFragment
import com.example.esemkablogger.ui.fargment.LikedPostFragment
import com.example.esemkablogger.ui.fargment.MyPostFragment
import org.apache.http.conn.scheme.HostNameResolver

class ViewPagerProfileAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                MyPostFragment()
            }
            1 -> {
                LikedPostFragment()
            }
            else -> {
                MyPostFragment()
            }
        }
    }
}