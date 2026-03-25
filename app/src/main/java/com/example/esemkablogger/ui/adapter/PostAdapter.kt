package com.example.esemkablogger.ui.adapter

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkablogger.data.model.Post
import com.example.esemkablogger.databinding.ItemPostBinding
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PostAdapter(
    private val list: MutableList<Post> =  mutableListOf()
): RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position]
        holder.apply {
            binding.tvTitle.text = post.title
            binding.tvContent.text = post.content
            binding.tvCategory.text = post.category.name
            binding.tvLikes.text = "${post.likeCount} Likes"

            CoroutineScope(Dispatchers.Main).launch {
                val bitmap = Helper.loadImage(post.thumbnail)
                binding.imgThumbnail.setImageBitmap(bitmap)
            }

//            var bitmap: Bitmap? = null
//            CoroutineScope(Dispatchers.IO).launch {
//                bitmap = Helper.loadImage(post.thumbnail)
//            }
//            CoroutineScope(Dispatchers.Main).launch {
//                binding.imgThumbnail.setImageBitmap(bitmap)
//            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}