package com.example.esemkablogger.ui.fargment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.databinding.FragmentProfileBinding
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        showData()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showData() {
        lifecycleScope.launch {
            val user = Helper.me(requireContext())
            binding.tvName.text = "${user?.firstName} ${user?.lastName}"

            val dateBirth = LocalDateTime.parse(user?.dateOfBirth)
            binding.tvBirthDate.text = dateBirth.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

            val dateJoin = LocalDateTime.parse(user?.joinDate)
            binding.tvJoinDate.text = "Join Date: ${dateJoin.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}"

            if (user?.photo == null || user.photo == "null") {
                binding.imgImage.setImageResource(R.drawable.outline_account_circle_24)
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap = Helper.loadImage(user.photo)
                    binding.imgImage.setImageBitmap(bitmap)
                }
            }
        }

    }
}