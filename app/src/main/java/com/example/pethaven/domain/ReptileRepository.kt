package com.example.pethaven.domain

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class ReptileRepository(private val reptileDao: ReptileDao) {
    private var firebaseStorageReference = FirebaseStorage.getInstance().reference

    fun addReptile(reptile: Reptile) = reptileDao.addReptile(reptile)
    fun getReptileFromCurrentUser(key: String) = reptileDao.getReptileFromCurrentUser(key)
    fun getAllUserReptile() = reptileDao.getAllUserReptiles()

    fun uploadImage(uri: Uri) =
        uri.let {
            val fileReference = firebaseStorageReference.child("images/" + System.currentTimeMillis())
            fileReference.putFile(it)
        }

}