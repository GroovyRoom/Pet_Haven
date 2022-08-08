package com.example.pethaven.util

import android.content.Context
import com.example.pethaven.domain.ReptileDao
import com.example.pethaven.domain.ReptileRepository
import com.example.pethaven.domain.ReptileViewModelFactory

object FactoryUtil {
    fun generateReptileViewModelFactory(context: Context) : ReptileViewModelFactory {
        val repository = ReptileRepository(ReptileDao())
        return ReptileViewModelFactory(repository)
    }
}