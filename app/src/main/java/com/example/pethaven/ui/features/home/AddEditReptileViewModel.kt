package com.example.pethaven.ui.features.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository

class AddEditReptileViewModel(private val repository: ReptileRepository): ViewModel() {
    var reptileImg = MutableLiveData<Bitmap>()
    var reptileImgUri = MutableLiveData<Uri>()

    fun insertToDatabase(reptile: Reptile) {
        if (reptileImgUri.value!= null) {
            repository.uploadImage(reptileImgUri.value!!).addOnSuccessListener { taskSnapShop ->
                taskSnapShop.metadata?.reference?.let {
                    val result = taskSnapShop.storage.downloadUrl
                    result.addOnSuccessListener { uri ->
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

    fun uploadImage(uri: Uri) = repository.uploadImage(uri)


}