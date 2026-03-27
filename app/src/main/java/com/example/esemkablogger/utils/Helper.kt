package com.example.esemkablogger.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
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

    fun getByteArrayFromUri(context: Context, uri: Uri): ByteArray {
        return context.contentResolver.openInputStream(uri).use {
            it!!.readBytes()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        context.contentResolver.query(uri, null, null, null).use { cursor ->
            if (cursor!!.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = cursor.getString(nameIndex)
            }
        }
        return fileName
    }
}