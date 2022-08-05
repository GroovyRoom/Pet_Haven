package com.example.pethaven.ui.features.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.ReptileRepository
import com.example.pethaven.domain.User

class ProfileEditViewModel(private val repository: ReptileRepository): ViewModel() {
    var profileImg = MutableLiveData<Bitmap>()
    var profileImgUri = MutableLiveData<Uri>()

    fun getCurrentUserObject() = repository.getCurrentUserObject()
    fun updateUser(user: User) = repository.updateUser(user)
    fun uploadImage(uri: Uri) = repository.uploadImage(uri)
}