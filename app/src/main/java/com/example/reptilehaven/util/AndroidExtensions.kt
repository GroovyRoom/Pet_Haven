package com.example.reptilehaven.util

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment

object AndroidExtensions {
    fun Activity.makeToast(value: String) = Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
    fun Fragment.makeToast(value: String) = Toast.makeText(activity, value, Toast.LENGTH_SHORT).show()
}