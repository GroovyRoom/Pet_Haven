package com.example.pethaven.domain

data class Post(
    var uid: String = "",
    var rid: String = "",
    var pid: String? = "",
    var imgUri: String? = null,
    var date: String? = null,
    var reptileName: String = "",
    var ownerName: String = "",
    var title: String = "",
    var price: Double = 0.0,
    var description: String = ""
)
