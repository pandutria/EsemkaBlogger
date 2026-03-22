package com.example.esemkablogger.data.local

import android.content.Context

class TokenManager(context: Context) {
    val pref = "pref"
    val key = "key"

    val shared = context.getSharedPreferences(pref, Context.MODE_PRIVATE)

    fun save(token: String) {
        shared.edit().putString(key, token)
    }

    fun get(): String? {
        return shared.getString(key, null)
    }

    fun remove() {
        shared.edit().remove(key)
    }
}