package com.example.pethaven.ui.features.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository

/**
 *  ViewModel for AddEditReptile Activity
 */
class AddEditReptileViewModel(private val repository: ReptileRepository): ViewModel() {
    var reptileImg = MutableLiveData<Bitmap>()
    var reptileImgUri = MutableLiveData<Uri>()

    fun insertToDatabase(reptile: Reptile) = repository.addReptile(reptile)

    fun updateReptileInDatabase(key:String, reptile: Reptile) = repository.updateReptile(key, reptile)

    fun getReptileFromCurrentUser(key: String) = repository.getReptileFromCurrentUser(key)

    fun deleteReptile(key: String) = repository.deleteReptile(key)

    fun deleteImage(imgUri: String) = repository.deleteImage(imgUri)

    fun uploadImage(uri: Uri) = repository.uploadImage(uri)

    fun getPostsByReptileID(rid: String) = repository.getPostsByReptileId(rid)

}