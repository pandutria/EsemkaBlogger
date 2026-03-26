package com.example.esemkablogger.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.local.TokenManager
import com.example.esemkablogger.data.local.UserSession
import com.example.esemkablogger.data.model.Category
import com.example.esemkablogger.data.model.Post
import com.example.esemkablogger.data.model.User
import com.example.esemkablogger.databinding.ActivityPostDetailScreenBinding
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PostDetailScreen : AppCompatActivity() {
    private var _binding: ActivityPostDetailScreenBinding? = null
    private val binding get() = _binding!!
    var post: Post? = null
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityPostDetailScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showData()
        likeCount()
        isLike()

        binding.btn.setOnClickListener {
            if (binding.btn.text != "Delete Post") {
                likePost()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showData() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "posts/${intent.getStringExtra("id").toString()}"
                )
            }


            if (result.code in 200..300) {
                Log.d("data", result.body)
                val data = JSONObject(result.body)
                val category = data.getJSONObject("category")
                val user = data.getJSONObject("user")

                post = Post(
                    id = data.getString("id"),
                    title = data.getString("title"),
                    content = data.getString("content"),
                    thumbnail = data.getString("thumbnail"),
                    imageContent = data.getString("imageContent"),
                    date = data.getString("date"),
                    likeCount = data.getInt("likeCount"),
                    category = Category(
                        id = category.getString("id"),
                        name = category.getString("name")
                    ),
                    user = User(
                        id = user.getString("id"),
                        firstName = user.getString("firstName"),
                        lastName = user.getString("lastName"),
                        username = user.getString("username"),
                        dateOfBirth = user.getString("dateOfBirth"),
                        joinDate = user.getString("joinDate"),
                        photo = user.getString("photo"),
                    )
                )

                binding.tvTitle.text = post?.title
                binding.tvContent.text = post?.content
                binding.tvCategory.text = post?.category?.name
                val date = LocalDateTime.parse(post?.date)
                binding.tvDate.text = date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))


                CoroutineScope(Dispatchers.Main).launch {
                    val bitmap = Helper.loadImage(post?.imageContent!!)
                    binding.imgImage.setImageBitmap(bitmap)
                }

                if (UserSession.user?.id == post?.user?.id) {
                    binding.btn.text = "Delete Post"
                } else {
                    isLike()
                }
            }
        }
    }

    fun likeCount() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "posts/${intent.getStringExtra("id")}/total-count"
                )
            }

            if (result.code in 200..300) {
                binding.tvLikes.text = "${result.body} Likes"
            }
        }
    }

    fun isLike() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "me/is-liked-post/${intent.getStringExtra("id")}",
                    token = TokenManager(this@PostDetailScreen).get()
                )
            }

            if (result.code in 200..300) {
                if (result.body == "false") {
                    binding.btn.text = "Like"
                } else {
                    binding.btn.text = "Unlike"
                }
            }
        }
    }

    fun likePost() {
        lifecycleScope.launch {
            val rBody = JSONObject().apply {
                put("postId", post?.id)
            }
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "posts/like",
                    "POST",
                    token = TokenManager(this@PostDetailScreen).get(),
                    rBody = rBody.toString()
                )
            }

            if (result.code in 200..300) {
                isLike()
                likeCount()
                Helper.toast(this@PostDetailScreen, "berhasil")
            } else {
                Helper.toast(this@PostDetailScreen, result.body.toString())
            }
        }
    }
}