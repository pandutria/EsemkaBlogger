package com.example.esemkablogger.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.local.TokenManager
import com.example.esemkablogger.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object Helper {
    val url = "http://10.0.2.2:5000/api/"
    val imageUrl = url.replace("api/", "images/")

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    suspend fun loadImage(image: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val conn = URL(imageUrl + image).openConnection()
                conn.connect()
                val input = conn.getInputStream()
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun me(context: Context): User? {
        return withContext(Dispatchers.IO) {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "me",
                    token = TokenManager(context).get()
                )
            }

            if (result.code in 200..300) {
                val user = JSONObject(result.body)

                User(
                    id = user.getString("id"),
                    firstName = user.getString("firstName"),
                    lastName = user.getString("lastName"),
                    username = user.getString("username"),
                    dateOfBirth = user.getString("dateOfBirth"),
                    joinDate = user.getString("joinDate"),
                    photo = user.getString("photo"),
                )
            } else {
                null
            }
        }
    }
}