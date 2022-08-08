package com.example.pethaven.ui.features.fav

import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.ReptileRepository

class FavReptileProfileViewModel(private val repository: ReptileRepository): ViewModel() {
    fun getReptileFromCurrentUser(key: String) = repository.getReptileFromCurrentUser(key)

}