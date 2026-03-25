package com.example.esemkablogger.ui.fargment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.model.Category
import com.example.esemkablogger.data.model.Http
import com.example.esemkablogger.data.model.Post
import com.example.esemkablogger.data.model.User
import com.example.esemkablogger.databinding.FragmentHomeBinding
import com.example.esemkablogger.ui.adapter.PostAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        showDataCategory()
        showData()

        binding.btn.setOnClickListener {
            showData()
        }
        return binding.root
    }


    fun showData() {
        val list: MutableList<Post> = mutableListOf()
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "posts"
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
                binding.rv.adapter = PostAdapter(list)
            }
        }
    }

    fun showDataCategory() {
        val list: MutableList<Category> = mutableListOf()
        list.add(
            Category(
                "0",
                "Select Category"
            )
        )
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "categories"
                )
            }

            if (result.code in 200..300) {
                val array = JSONArray(result.body)

                for (i in 0 until array.length()) {
                    val data = array.getJSONObject(i)

                    list.add(
                        Category(
                            id = data.getString("id"),
                            name = data.getString("name")
                        )
                    )
                }
                val adapter = ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    list
                )

                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = adapter
            }
        }
    }
}