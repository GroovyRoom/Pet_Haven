package com.example.pethaven.domain

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ReptileDao {
    private lateinit var databaseReference: DatabaseReference
    init {
        databaseReference = FirebaseDatabase.getInstance().getReference(Reptile::class.java.simpleName)
    }

}