package com.example.reptilehaven.domain

import com.google.firebase.database.Exclude

class Reptile (
    @Exclude
    var key: String? = null,
    var name: String = "",
    var species: String = "",
    var age: Int = -1,
    var description: String = "",
    var imgUri: String? = null,
    )