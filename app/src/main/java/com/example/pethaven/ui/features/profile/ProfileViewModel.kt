package com.example.pethaven.ui.features.profile

import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.ReptileRepository

class ProfileViewModel(private val repository: ReptileRepository): ViewModel() {
    fun getCurrentUserObject() = repository.getCurrentUserObject()
}