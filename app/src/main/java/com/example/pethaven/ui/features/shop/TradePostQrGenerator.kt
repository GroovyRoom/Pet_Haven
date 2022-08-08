package com.example.pethaven.ui.features.shop

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.pethaven.R
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.lang.Exception

/**
 * Activity to display QR code of the value passed as the argument
 */
class TradePostQrGenerator : AppCompatActivity() {
    private lateinit var valueToGenerate: String
    private lateinit var qrImageView: ImageView

    companion object {
        private const val VALUE_TO_GENERATE_TAG = "Value to Generate Tag"
        fun makeIntent(context: Context, value: String): Intent {
            val intent = Intent(context, TradePostQrGenerator::class.java)
            intent.putExtra(VALUE_TO_GENERATE_TAG, value)
            return intent
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_post_qr_generator)
        qrImageView = findViewById(R.id.qrImageView)

        valueToGenerate = intent.getStringExtra(VALUE_TO_GENERATE_TAG) ?: ""
        generateQrCode(valueToGenerate)
    }

    private fun generateQrCode(value: String) {
        if (value == "") {
            makeToast("Retrieving reptile id failed!")
            return
        }

        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(value, BarcodeFormat.QR_CODE, 400, 400)
            qrImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            makeToast("An error has occurred")
            e.printStackTrace()
        }
    }
}