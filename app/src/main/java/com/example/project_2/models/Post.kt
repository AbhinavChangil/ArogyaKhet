package com.example.project_2.models

data class Post(
    val text: String = "",
    val createdBy: User = User(),
    val createdAt: Long = 0L,
    var plantImageUrl: String = "https://lh3.googleusercontent.com/a/AGNmyxa6fBHqWqQAx5DN1F-Rw4nGTAvkKi5zU4Euj4sQ=s96-c",
    val likedBy: ArrayList<Any> = ArrayList()
)