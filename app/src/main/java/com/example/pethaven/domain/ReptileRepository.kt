package com.example.pethaven.domain

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class ReptileRepository(private val reptileDao: ReptileDao) {
    private var firebaseStorageReference = FirebaseStorage.getInstance().reference

    ///-------------------------- Operations for Post Objects-------------------------///
    fun addPost(post: Post) = reptileDao.addPost(post)
    fun getAllPost(post: Post) = reptileDao.getAllPost()

    ///-------------------------- Operations for Reptile Objects-------------------------///
    fun addReptile(reptile: Reptile) = reptileDao.addReptile(reptile)
    fun updateReptile(key: String, reptile: Reptile) = reptileDao.updateReptile(key, reptile)

    fun deleteReptile(key: String) = reptileDao.deleteReptile(key)
    fun deleteImage(imgUri: String) = reptileDao.deleteImageFromStorage(imgUri)

    fun getReptileFromCurrentUser(key: String) = reptileDao.getReptileFromCurrentUser(key)
    fun getAllUserReptile() = reptileDao.getAllUserReptiles()

    ///------------------------- Operations for Uploading heavy data -------------------------///
    fun uploadImage(uri: Uri) =
        uri.let {
            val fileReference = firebaseStorageReference.child("images/" + System.currentTimeMillis())
            fileReference.putFile(it)
        }

}