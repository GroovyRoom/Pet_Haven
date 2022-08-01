package com.example.reptilehaven.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import java.io.FileNotFoundException

object BitmapUtil {
    /*
        Function from Xin Dong Yang Lecture notes
     */
    fun getBitmap(context: Context, imgUri: Uri): Bitmap? {
        return try{
            val bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
            val matrix = Matrix()
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: FileNotFoundException){
            null
        }
    }
}