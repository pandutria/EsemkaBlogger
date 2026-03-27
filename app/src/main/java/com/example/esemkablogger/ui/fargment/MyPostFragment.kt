package com.example.esemkablogger.ui.fargment

import android.media.session.MediaSession
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.local.TokenManager
import com.example.esemkablogger.data.model.Category
import com.example.esemkablogger.data.model.Post
import com.example.esemkablogger.data.model.User
import com.example.esemkablogger.databinding.FragmentMyPostBinding
import com.example.esemkablogger.databinding.FragmentProfileBinding
import com.example.esemkablogger.ui.adapter.PostProfileAdapter
import com.example.esemkablogger.ui.adapter.UserAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class MyPostFragment : Fragment() {
    private var _binding: FragmentMyPostBinding? =  null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyPostBinding.inflate(layoutInflater, container, false)
        showData()
        return binding.root
    }

    fun showData() {
        val list: MutableList<Post> = mutableListOf()
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "me/post",
                    token = TokenManager(requireContext()).get()
                )
            }

            if (result.code in 200..300) {
                val array = JSONArray(result.body)

                for (i in 0 until array.length()) {
                    val data = array.getJSONObject(i)
                    val category = data.getJSONObject("category")
                    val user = data.getJSONObject("user")

                    list.add(
                        Post(
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
                    )
                }

                binding.rv.adapter = PostProfileAdapter(list)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showData()
    }
}