package com.example.pethaven.domain

import android.net.Uri

class Reptile (
    var name: String = "",
    var species: String = "",
    var age: Int = -1,
    var description: String = "",
    var imgUri: String? = null,
    var price: Double = 0.0,
    var title: String = ""
)