package com.example.esemkablogger.data.local

import android.content.Context

class ExpTokenManager(context: Context) {
    val pref = "exp"
    val key = "exp"

    val shared = context.getSharedPreferences(pref, Context.MODE_PRIVATE)

    fun save(exp: String) {
        shared.edit().putString(key, exp).apply()
    }

    fun get(): String? {
        return shared.getString(key, null)
    }

    fun remove() {
        shared.edit().remove(key).apply()
    }
}