package com.example.pethaven.ui.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository

class HomeTestViewModel(private val repository: ReptileRepository): ViewModel() {
    var isFabChecked = MutableLiveData(false)
    fun getAllUserReptile() = repository.getAllUserReptile()
}