package com.example.esemkablogger.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkablogger.R
import com.example.esemkablogger.databinding.ActivityMainScreenBinding
import com.example.esemkablogger.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainScreen : AppCompatActivity() {
    private var _binding: ActivityMainScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(binding.tab, binding.viewPager){tab, position ->
            when(position) {
                0 -> {
                    tab.text = "POSTS"
                }
                1 -> {
                    tab.text = "USERS"
                }
                2 -> {
                    tab.text = "PROFILE"
                }
            }
        }.attach()
    }
}