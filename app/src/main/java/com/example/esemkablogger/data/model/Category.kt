package com.example.esemkablogger.data.model

data class Category(
    val id: String,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}