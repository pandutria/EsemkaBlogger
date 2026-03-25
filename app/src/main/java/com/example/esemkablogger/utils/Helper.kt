package com.example.esemkablogger.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object Helper {
    val url = "http://10.0.2.2:5000/api/"
    val imageUrl = url.replace("api/", "images/")

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    suspend fun loadImage(image: String): Bitmap? {
        return withContext(Dispatchers.IO){
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
}