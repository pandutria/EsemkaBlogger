package com.example.esemkablogger.data.model

data class Post(
    val category: Category,
    val content: String,
    val date: String,
    val id: String,
    val imageContent: String,
    val likeCount: Int,
    val thumbnail: String,
    val title: String,
    val user: User,
)