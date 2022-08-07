package com.example.pethaven.ui.features.shop

import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.ReptileDao
import com.example.pethaven.domain.ReptileRepository

class EditTradePostViewModel: ViewModel() {

    private var reptileDao = ReptileDao()
    private var reptileRepository = ReptileRepository(reptileDao).getInstance()

    fun getTradePost(key: String) = reptileRepository.getPost(key)

    fun deletePost(key: String) = reptileRepository.deletePost(key)

        fun editTradePost(key: String, post: Post) = reptileRepository.editTradePost(key, post)

}