package com.example.pethaven.ui.features.fav

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository

class AddEditReptileViewModel(private val repository: ReptileRepository): ViewModel() {
    var reptileImg = MutableLiveData<Bitmap>()
    var reptileImgUri = MutableLiveData<Uri>()

    var reptileEditImgUriString = MutableLiveData<String>()

    fun insertToDatabase(reptile: Reptile) {
        if (reptileImgUri.value!= null) {
            repository.uploadImage(reptileImgUri.value!!).addOnSuccessListener { taskSnapShop ->
                taskSnapShop.metadata?.reference?.let {
                    val result = taskSnapShop.storage.downloadUrl
                    result.addOnSuccessListener { uri: Uri ->
                        val imgUriString = uri.toString()
                        reptile.imgUri = imgUriString
                        repository.addReptile(reptile)
                    }
                }
            }
        } else {
            reptile.imgUri = null
            repository.addReptile(reptile)
        }
    }

    fun insertToDatabase2(reptile: Reptile) = repository.addReptile(reptile)

    fun updateReptileInDatabase(key:String, reptile: Reptile) = repository.updateReptile(key, reptile)

    fun getReptileFromCurrentUser(key: String) = repository.getReptileFromCurrentUser(key)

    fun deleteReptile(key: String) = repository.deleteReptile(key)
    fun deleteImage(imgUri: String) = repository.deleteImage(imgUri)

    fun uploadImage(uri: Uri) = repository.uploadImage(uri)


}