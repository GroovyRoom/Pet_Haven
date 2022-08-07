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

    ///-------------------------- Operations for User Objects -------------------------///
    fun getCurrentUserObject() = FirebaseDatabase.getInstance()
        .reference
        .child("users")
        .child(firebaseAuth.currentUser!!.uid)


    fun updateUser(user: User) =
        FirebaseDatabase
            .getInstance()
            .reference
            .child("users")
            .child(firebaseAuth.currentUser!!.uid)
            .setValue(user)

    ///-------------------------- Operations for Post Objects -------------------------///
    fun addPost(post: Post) = postReference.push().setValue (
        post.apply {
            post.uid = firebaseAuth.currentUser!!.uid
        }
    )

    fun getPost(key: String) = postReference.child(key)

    fun getAllPost() = postReference

    fun deletePost(key: String) = postReference.child(key).removeValue()

    fun getPostByReptileID(rid: String) = postReference.orderByChild("rid").equalTo(rid)

    fun editTradePost(key:String, post:Post) = postReference.child(key).setValue(post)

    ///-------------------------- Operations for Reptile Objects-------------------------///
    fun addReptile(reptile: Reptile) =
        userReference.child(Reptile::class.java.simpleName).push().setValue(reptile)

    fun updateReptile(key: String, reptile: Reptile) =
        userReference.child(Reptile::class.java.simpleName).child(key).setValue(reptile)

    fun deleteReptile(key: String) =
        userReference.child(Reptile::class.java.simpleName).child(key).removeValue()

    fun deleteImageFromStorage(imgUri: String) =
        firebaseStorage.getReferenceFromUrl(imgUri).delete()

    fun getReptile(uid: String, rid: String) = FirebaseDatabase.getInstance()
        .getReference(uid)
        .child(Reptile::class.java.simpleName)
        .child(rid)

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