package com.example.pethaven.ui.features.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, var username: String, var profileImageUrl: String, var phoneNumber: String, var address: String, var isOpen: Boolean=false) : Parcelable {
    constructor() : this("", "", "", "", "",false)
    constructor(uid: String) : this(uid, "", "", "", "",false)

    override fun toString(): String {
        return "User(uid='$uid', username='$username', profileImageUrl='$profileImageUrl', phoneNumber='$phoneNumber', address='$address', isOpen=$isOpen)"
    }//hh
}