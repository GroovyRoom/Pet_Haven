package com.example.pethaven.domain

data class Post(
    var uid: String = "",
    var rid: String = "",
    var title: String = "",
    var price: Double = 0.0,
    var description: String = ""
)
