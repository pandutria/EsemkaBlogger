package com.example.esemkablogger.ui.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkablogger.R
import com.example.esemkablogger.data.model.User
import com.example.esemkablogger.databinding.ItemUserBinding
import com.example.esemkablogger.ui.LoginScreen
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserAdapter(
    private val list: MutableList<User> = mutableListOf()
): RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]
        holder.apply {
            binding.tvName.text = "${user.firstName} ${user.lastName}"

            val date = LocalDateTime.parse(user.joinDate)
            val format = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
            binding.tvDate.text = "Join Date: ${format}"

            if (user.photo == null || user.photo == "null") {
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