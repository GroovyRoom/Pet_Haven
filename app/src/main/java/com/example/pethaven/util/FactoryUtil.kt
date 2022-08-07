package com.example.pethaven.util

import android.content.Context
import com.example.pethaven.domain.ReptileDao
import com.example.pethaven.domain.ReptileRepository
import com.example.pethaven.domain.ReptileViewModelFactoryFav

object FactoryUtil {
    fun generateReptileViewModelFactory(context: Context) : ReptileViewModelFactoryFav {
        val repository = ReptileRepository(ReptileDao())
        return ReptileViewModelFactoryFav(repository)
    }
}