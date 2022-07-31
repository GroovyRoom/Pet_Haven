package com.example.pethaven.ui.features.chat

class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}