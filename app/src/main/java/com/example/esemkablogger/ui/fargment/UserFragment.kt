package com.example.esemkablogger.ui.fargment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.model.Category
import com.example.esemkablogger.data.model.User
import com.example.esemkablogger.databinding.FragmentUserBinding
import com.example.esemkablogger.ui.adapter.UserAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        showData()
        return binding.root
    }

    fun showData() {
        val list: MutableList<User> = mutableListOf()
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "users"
                )
            }

            if (result.code in 200..300) {
                val array = JSONArray(result.body)

                for (i in 0 until array.length()) {
                    val user = array.getJSONObject(i)

                    list.add(
                        User(
                            id = user.getString("id"),
                            firstName = user.getString("firstName"),
                            lastName = user.getString("lastName"),
                            username = user.getString("username"),
                            dateOfBirth = user.getString("dateOfBirth"),
                            joinDate = user.getString("joinDate"),
                            photo = user.getString("photo"),
                        )
                    )
                }

                binding.rv.adapter = UserAdapter(list)
            }
        }
    }
}