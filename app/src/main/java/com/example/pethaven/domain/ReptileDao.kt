package com.example.pethaven.domain

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class ReptileDao {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var userReference: DatabaseReference = FirebaseDatabase
        .getInstance()
        .getReference(firebaseAuth.currentUser!!.uid)
    private var postReference: DatabaseReference = FirebaseDatabase
        .getInstance()
        .getReference(Post::class.java.simpleName)
    private var firebaseStorage = FirebaseStorage.getInstance()


    ///-------------------------- Operations for Post Objects -------------------------///
    fun addPost(post: Post) = postReference.push().setValue(
        post.apply { post.uid = firebaseAuth.currentUser!!.uid}
    )

    fun getAllPost() = postReference

    ///-------------------------- Operations for Reptile Objects-------------------------///
    fun addReptile(reptile: Reptile) =
        userReference.child(Reptile::class.java.simpleName).push().setValue(reptile)

    fun updateReptile(key: String, reptile: Reptile) =
        userReference.child(Reptile::class.java.simpleName).child(key).setValue(reptile)

    fun deleteReptile(key: String) =
        userReference.child(Reptile::class.java.simpleName).child(key).removeValue()

    fun deleteImageFromStorage(imgUri: String) =
        firebaseStorage.getReferenceFromUrl(imgUri).delete()

    fun getReptileFromCurrentUser(key: String) =
        userReference.child(Reptile::class.java.simpleName).child(key)

    fun getAllUserReptiles() =
        userReference.child(Reptile::class.java.simpleName)

    ///-------------------------- Operations for Uploading to Storage  -------------------------///
    fun uploadImage(uri: Uri) =
        uri.let {
            val fileReference = firebaseStorage.reference
                .child("images/" + System.currentTimeMillis())
            fileReference.putFile(it)
        }

}