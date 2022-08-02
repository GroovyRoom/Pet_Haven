package com.example.pethaven.ui.features.shop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.databinding.ActivityTradePostBinding
import com.example.pethaven.domain.Post
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.firebase.database.FirebaseDatabase

class TradePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradePostBinding

    private lateinit var tradePostViewModel: TradePostViewModel

    private lateinit var reptileKey: String

    companion object {
        private const val TRADE_REPTILE_KEY_TAG = "Trade Reptile Key Tag"

        fun makeIntent(context: Context, reptileKey: String): Intent {
            val intent = Intent(context, TradePostActivity::class.java)
            intent.putExtra(TRADE_REPTILE_KEY_TAG, reptileKey)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTradePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setUpViewModel()
        reptileKey = intent.getStringExtra(TRADE_REPTILE_KEY_TAG) ?: ""
        makeToast(reptileKey)

        binding.addTradePostButton.setOnClickListener {
            val title = binding.addTradeTitleEditText.text.toString()
            val price = binding.addTradePriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val description = binding.addTradeDescriptionEditText.text.toString()

            val post = Post(
                rid = reptileKey,
                title = title,
                price = price,
                description = description
            )
            tradePostViewModel.addPost(post)
        }
    }

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        tradePostViewModel = ViewModelProvider(this, factory).get(TradePostViewModel::class.java)
    }

    private fun addTradePost(post: Post){
        binding.tradePostProgressBar.isIndeterminate = true

        tradePostViewModel.addPost(post)
            .addOnSuccessListener {
                makeToast("Trade Post added Successfully!")
                binding.tradePostProgressBar.isIndeterminate = false
            }
            .addOnFailureListener {
                makeToast(it.message ?: "")
            }
    }
}