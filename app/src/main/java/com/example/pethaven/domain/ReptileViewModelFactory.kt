package com.example.pethaven.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.ui.features.home.AddEditReptileViewModel
import java.lang.IllegalArgumentException


class ReptileViewModelFactory(private val repository: ReptileRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddEditReptileViewModel::class.java) -> {
                AddEditReptileViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unsupported View Model")
        }
    }
}