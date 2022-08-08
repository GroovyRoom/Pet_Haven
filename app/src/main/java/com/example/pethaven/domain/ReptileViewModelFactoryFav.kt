package com.example.pethaven.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.ui.features.fav.FavTestViewModel
import com.example.pethaven.ui.features.fav.FavReptileProfileViewModel
import com.example.pethaven.ui.features.home.AddEditReptileViewModel
import com.example.pethaven.ui.features.profile.ProfileEditViewModel
import com.example.pethaven.ui.features.profile.ProfileViewModel
import com.example.pethaven.ui.features.shop.TradePostViewModel
import java.lang.IllegalArgumentException


class ReptileViewModelFactoryFav(private val repository: ReptileRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddEditReptileViewModel::class.java) -> {
                AddEditReptileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FavTestViewModel::class.java) -> {
                FavTestViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FavReptileProfileViewModel::class.java) -> {
                FavReptileProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TradePostViewModel::class.java) -> {
                TradePostViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileEditViewModel::class.java) -> {
                ProfileEditViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unsupported View Model")
        }
    }
}