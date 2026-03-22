package com.example.esemkablogger.utils

import android.content.Context
import android.widget.Toast

object Helper {
    val url = "http://10.0.2.2:5000/api/"

    fun toast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}