package com.example.pethaven.util

import androidx.lifecycle.MutableLiveData

/*
    From George Andreas My Runs Assignment
 */
object LiveDataExtensions {
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}