package com.example.reptilehaven.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.reptilehaven.ui.features.home.AddEditReptileViewModel
import com.example.reptilehaven.ui.features.home.HomeTestViewModel
import com.example.reptilehaven.ui.features.home.ReptileProfileViewModel
import java.lang.IllegalArgumentException


class ReptileViewModelFactory(private val repository: ReptileRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddEditReptileViewModel::class.java) -> {
                AddEditReptileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeTestViewModel::class.java) -> {
                HomeTestViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ReptileProfileViewModel::class.java) -> {
                ReptileProfileViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unsupported View Model")
        }
    }
}