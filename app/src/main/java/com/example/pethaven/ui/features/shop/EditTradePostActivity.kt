package com.example.pethaven.ui.features.shop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.pethaven.R
import com.example.pethaven.databinding.ActivityEditTradePostBinding
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.Reptile
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class EditTradePostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditTradePostBinding

    private lateinit var databaseReference: DatabaseReference
    private var valueEventListener: ValueEventListener? = null
    private val editTradePostViewModel: EditTradePostViewModel by viewModels()

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText

    private var post: Post? = Post()
    private var postKeyToEdit: String? = null
    private var editMode = false
    private var hasAccess = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTradePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        onFocusChange()
        onSaveClicked()
        onCancelClicked()
        getPostFromDatabase()
        checkPermission()
    }

    override fun onDestroy() {
        valueEventListener?.let { databaseReference.removeEventListener(it) }
        super.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_reptile_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.editReptileMenuItem -> {
                if (editMode) {
                    postKeyToEdit?.let {
                        deletePostInDatabase(it)
                    }
                }
                else {

                    if (hasAccess) {
                        item.setIcon(R.drawable.ic_delete)
                        enableEditing()
                        editMode = true

                    }
                    else {
                        makeToast("You don't have access to edit this post")
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editPostInDatabase() {
        titleEditText = findViewById(R.id.edit_trade_title_edit_text)
        descriptionEditText = findViewById(R.id.edit_trade_description_edit_text)
        priceEditText = findViewById(R.id.edit_trade_price_edit_text)
        post!!.title = titleEditText.text.toString()
        post!!.description = descriptionEditText.text.toString()
        post!!.price = priceEditText.text.toString().toDouble()
        editTradePostViewModel.editTradePost(postKeyToEdit!!, post!!)
    }

    private fun onSaveClicked() {
        binding.editTradeSaveButton.setOnClickListener {
            editPostInDatabase()
            finish()
        }
    }

    private fun onCancelClicked() {
        binding.editTradeCancelButton.setOnClickListener {
            finish()
        }
    }


    ///-------------------------- Setting Up Activity -------------------------///


    ///-------------------------- Database Operations -------------------------///


    ///-------------------------- Setting Up Views' properties -------------------------///

    private fun onFocusChange() {
        binding.editTradeDescriptionEditText.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.editTradeDescriptionInputText.hint = "Description"
                } else {
                    binding.editTradeDescriptionInputText.hint = "Useful Information for the buyer"
                }
            }
    }

    fun updateView(post: Post) {
        setupImage()
        binding.editTradeTitleEditText.setText(post.title)
        binding.editTradePriceEditText.setText(post.price.toString())
        binding.editTradeDescriptionEditText.setText(post.description)
    }

    private fun getPostFromDatabase() {
        postKeyToEdit = intent.getStringExtra("post key")
        val postReference = editTradePostViewModel.getTradePost(postKeyToEdit!!)
        postReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                println("debug: ondatachanged called")
                if(!snapshot.exists()) {
                    return
                }
                post = snapshot.getValue(Post::class.java)
                post?.let {
                    updateView(it)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }
        })
    }

    private fun deletePostInDatabase(key: String) {
        editTradePostViewModel.deletePost(key).addOnSuccessListener {
            makeToast("Delete Successful")
        }
    }

    private fun enableEditing() {
        binding.editTradeTitleEditText.isEnabled = true
        binding.editTradePriceEditText.isEnabled = true
        binding.editTradeDescriptionEditText.isEnabled = true
    }

    private fun setupImage() {
        post?.let {
            if(it.imgUri != null){
                Glide.with(this@EditTradePostActivity)
                    .load(it.imgUri)
                    .fitCenter()
                    .into(binding.editTradeImage)
            }
        }
    }

    private fun checkPermission() {
        FirebaseAuth.getInstance().currentUser?.let {
            if (it.uid == post!!.uid) {
                hasAccess = true
            }
            else {
                binding.editTradeSaveButton.isEnabled = false
                return
            }
        }
    }
}