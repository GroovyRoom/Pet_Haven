package com.example.reptilehaven.util

import android.content.Context
import com.example.reptilehaven.domain.ReptileDao
import com.example.reptilehaven.domain.ReptileRepository
import com.example.reptilehaven.domain.ReptileViewModelFactory

object FactoryUtil {
    fun generateReptileViewModelFactory(context: Context) : ReptileViewModelFactory {
        val repository = ReptileRepository(ReptileDao())
        return ReptileViewModelFactory(repository)
    }
}