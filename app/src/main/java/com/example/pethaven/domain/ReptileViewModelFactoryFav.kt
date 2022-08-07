package com.example.pethaven.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.ui.features.fav.AddEditReptileViewModelFav
import com.example.pethaven.ui.features.fav.FavTestViewModel
import com.example.pethaven.ui.features.fav.ReptileProfileViewModelFav
import com.example.pethaven.ui.features.profile.ProfileEditViewModel
import com.example.pethaven.ui.features.profile.ProfileViewModel
import com.example.pethaven.ui.features.shop.TradePostViewModel
import java.lang.IllegalArgumentException


class ReptileViewModelFactoryFav(private val repository: ReptileRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddEditReptileViewModelFav::class.java) -> {
                AddEditReptileViewModelFav(repository) as T
            }
            modelClass.isAssignableFrom(FavTestViewModel::class.java) -> {
                FavTestViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ReptileProfileViewModelFav::class.java) -> {
                ReptileProfileViewModelFav(repository) as T
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