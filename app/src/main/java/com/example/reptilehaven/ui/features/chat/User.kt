package com.example.reptilehaven.ui.features.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String, val phoneNumber: String, val address: String) : Parcelable {
    constructor() : this("", "", "", "", "")
}