package com.example.reptilehaven.ui.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.reptilehaven.domain.ReptileRepository

class HomeTestViewModel(private val repository: ReptileRepository): ViewModel() {
    var isFabChecked = MutableLiveData(false)
    var reptileList = getAllUserReptile()

    fun getAllUserReptile() = repository.getAllUserReptile()
}