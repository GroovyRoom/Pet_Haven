package com.example.pethaven.ui.features.home

import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.ReptileRepository

/**
 * ViewModel for ReptileProfile Activity
 */
class ReptileProfileViewModel(private val repository: ReptileRepository): ViewModel() {

    fun getReptileFromCurrentUser(key: String) = repository.getReptileFromCurrentUser(key)

}