package com.example.pethaven.ui.features.shop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.pethaven.databinding.ActivityTradePostBinding
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.User
import com.example.pethaven.ui.features.chat.ChatFragment
import com.example.pethaven.ui.features.home.ReptileProfileViewModel
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class TradePostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTradePostBinding

    private lateinit var tradePostViewModel: TradePostViewModel

    private lateinit var reptileKey: String
    private lateinit var databaseReference: DatabaseReference
    private var valueEventListener: ValueEventListener? = null
    private lateinit var reptileProfileViewModel: ReptileProfileViewModel
    private var reptile: Reptile? = Reptile()
    var postOwner: User? = null


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
        fetchPostOwner()
        val view = binding.root
        setContentView(view)
        setUpViewModel()
        setUpReptileViewModel()
        addTradePostClickListener()
        cancelTradePostClickListener()
        onFocusChange()
        reptileKey = intent.getStringExtra(TRADE_REPTILE_KEY_TAG) ?: ""
        databaseReference = reptileProfileViewModel.getReptileFromCurrentUser(reptileKey)
        setupImage()
    }

    override fun onDestroy() {
        valueEventListener?.let { databaseReference.removeEventListener(it) }
        super.onDestroy()
    }

    private fun fetchPostOwner() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                postOwner = p0.getValue(User::class.java)
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


    ///-------------------------- Setting Up Activity -------------------------///
    private fun addTradePostClickListener() {
        binding.addTradePostButton.setOnClickListener {
            val title = binding.addTradeTitleEditText.text.toString()
            val price = binding.addTradePriceEditText.text.toString().toDoubleOrNull() ?: 0.0
            val description = binding.addTradeDescriptionEditText.text.toString()
            addTradePost(
                Post(
                        rid = reptileKey,
                        reptileName = reptile!!.name,
                        imgUri = reptile!!.imgUri,
                        ownerName = postOwner!!.username,
                        date = createDateString(),
                        title = title,
                        price = price,
                        description = description
                )
            )
            finish()
        }
    }

    private fun cancelTradePostClickListener() {
        binding.addTradeCancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        tradePostViewModel = ViewModelProvider(this, factory)[TradePostViewModel::class.java]
    }

    private fun setUpReptileViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        reptileProfileViewModel = ViewModelProvider(this, factory)[ReptileProfileViewModel::class.java]
    }


    ///-------------------------- Database Operations -------------------------///
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

    private fun createDateString(): String {
        val calendar = Calendar.getInstance()

        val month = calendar.getDisplayName(
                Calendar.MONTH,
                Calendar.SHORT_FORMAT,
                Locale.US
        )
        val date = calendar.get(Calendar.DAY_OF_MONTH)
        val year = calendar.get(Calendar.YEAR)

        return "$month $date $year"
    }

    ///-------------------------- Setting Up Views' properties -------------------------///
    private fun setupImage() {
        valueEventListener = databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    return
                }
                reptile = snapshot.getValue(Reptile::class.java)
                reptile?.let {
                    if(it.imgUri != null){
                        Glide.with(this@TradePostActivity)
                            .load(it.imgUri)
                            .fitCenter()
                            .into(binding.addTradeImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }
        })
    }

    private fun onFocusChange() {
        binding.addTradeDescriptionEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.addTradeDescriptionInputText.hint = "Description"
                } else {
                    binding.addTradeDescriptionInputText.hint = "Useful Information for the buyer"
                }
            }

    }
}