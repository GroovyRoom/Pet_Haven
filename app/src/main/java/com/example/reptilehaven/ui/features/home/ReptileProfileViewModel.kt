package com.example.reptilehaven.ui.features.home

import androidx.lifecycle.ViewModel
import com.example.reptilehaven.domain.ReptileRepository

class ReptileProfileViewModel(private val repository: ReptileRepository): ViewModel() {

    fun getReptileFromCurrentUser(key: String) = repository.getReptileFromCurrentUser(key)

}