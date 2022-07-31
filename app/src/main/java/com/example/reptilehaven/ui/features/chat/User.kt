package com.example.reptilehaven.ui.features.chat

class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}