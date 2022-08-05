package com.example.pethaven.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostViewModel: ViewModel() {
    val currentFilterButtonID = MutableLiveData(1)

    private val reptileDao: ReptileDao = ReptileDao()
    private val repository: ReptileRepository = ReptileRepository(reptileDao).getInstance()
    private val _allPosts: MutableLiveData<List<Post>> = MutableLiveData()
    val allPosts: LiveData<List<Post>> = _allPosts

    init {
        repository.loadPosts(_allPosts)
    }
}