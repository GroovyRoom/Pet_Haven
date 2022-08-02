package com.example.pethaven.ui.features.shop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pethaven.databinding.ActivityTradePostBinding
import com.google.firebase.database.FirebaseDatabase

class TradePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradePostBinding

    private lateinit var viewModel: TradePostViewModel
    private lateinit var mFirebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTradePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}