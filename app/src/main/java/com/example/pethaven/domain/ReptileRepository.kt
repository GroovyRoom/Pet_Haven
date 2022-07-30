package com.example.pethaven.domain

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ReptileRepository(private val reptileDao: ReptileDao) {
    private var firebaseStorageReference = FirebaseStorage.getInstance().reference

    fun addReptile(reptile: Reptile) = reptileDao.addReptile(reptile)

    fun uploadImage(uri: Uri) =
        uri.let {
            val fileReference =
                firebaseStorageReference.child("images/" + System.currentTimeMillis())
            fileReference.putFile(it)
        }

}