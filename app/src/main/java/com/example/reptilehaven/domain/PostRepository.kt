package com.example.reptilehaven.domain

import com.google.firebase.database.FirebaseDatabase

class PostRepository {
    private val databaseReference = FirebaseDatabase.getInstance().getReference("posts")
}