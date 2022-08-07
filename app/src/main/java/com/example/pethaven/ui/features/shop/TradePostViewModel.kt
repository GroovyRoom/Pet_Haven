package com.example.pethaven.ui.features.shop

import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.ReptileRepository

/**
 *  ViewModel for the TradePost Activity
 */
class TradePostViewModel(private val repository: ReptileRepository) : ViewModel() {
    fun addPost(post: Post) = repository.addPost(post)
}