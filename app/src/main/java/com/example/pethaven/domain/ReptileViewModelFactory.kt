package com.example.pethaven.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.ui.features.home.AddEditReptileViewModel
import com.example.pethaven.ui.features.home.HomeTestViewModel
import com.example.pethaven.ui.features.home.ReptileProfileViewModel
import com.example.pethaven.ui.features.profile.ProfileEditViewModel
import com.example.pethaven.ui.features.shop.TradePostViewModel
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
            modelClass.isAssignableFrom(TradePostViewModel::class.java) -> {
                TradePostViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileEditViewModel::class.java) -> {
                ProfileEditViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unsupported View Model")
        }
    }
}