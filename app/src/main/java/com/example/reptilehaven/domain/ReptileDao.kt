package com.example.reptilehaven.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ReptileDao {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var databaseReference: DatabaseReference = FirebaseDatabase
        .getInstance()
        .getReference(firebaseAuth.currentUser!!.uid)
    private var firebaseStorageReference = FirebaseStorage.getInstance()


    fun addReptile(reptile: Reptile) =
        databaseReference.child(Reptile::class.java.simpleName).push().setValue(reptile)

    fun deleteReptile(key: String) =
        databaseReference.child(Reptile::class.java.simpleName).child(key).removeValue()

    fun deleteImageFromStorage(imgUri: String) =
        firebaseStorageReference.getReferenceFromUrl(imgUri)

    fun updateReptile(key: String, reptile: Reptile) =
        databaseReference.child(Reptile::class.java.simpleName).child(key).setValue(reptile)

    fun getReptileFromCurrentUser(key: String) =
        databaseReference.child(Reptile::class.java.simpleName).child(key)

    fun getAllUserReptiles() =
        databaseReference.child(Reptile::class.java.simpleName)

}