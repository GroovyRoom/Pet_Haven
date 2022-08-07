package com.example.pethaven.ui.features.shop

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.ReptileDao
import com.example.pethaven.domain.ReptileRepository

class EditTradePostViewModel: ViewModel() {

    private var reptileDao = ReptileDao()
    var post: MutableLiveData<Post> = MutableLiveData()
    var databaseAccessed = MutableLiveData<Boolean>()
    var isEditModeEnabled = MutableLiveData<Boolean>()
    private var reptileRepository = ReptileRepository(reptileDao).getInstance()

    init {
        databaseAccessed.value = false
        isEditModeEnabled.value = false
    }

    fun getTradePost(key: String) = reptileRepository.getPost(key)

    fun deletePost(key: String) = reptileRepository.deletePost(key)

    fun editTradePost(key: String, post: Post) = reptileRepository.editTradePost(key, post)

}