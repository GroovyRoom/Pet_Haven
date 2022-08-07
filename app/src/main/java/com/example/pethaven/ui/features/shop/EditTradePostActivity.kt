package com.example.pethaven.ui.features.shop

import android.content.Context
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
    private var hasAccess = false

    companion object {
        private const val POST_KEY_TAG = "post key"
        fun makeIntent(context: Context, postKey: String): Intent {
            val intent = Intent(context,EditTradePostActivity::class.java)
            intent.putExtra(POST_KEY_TAG, postKey)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTradePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        onFocusChange()
        onSaveClicked()
        onCancelClicked()

        if (editTradePostViewModel.databaseAccessed.value!!) {
            post = editTradePostViewModel.post.value!!
            checkPermission()
            updateView(post!!)
        } else {
            getPostFromDatabase()
        }

        if (editTradePostViewModel.isEditModeEnabled.value!!) {
            enableEditing()
        }
    }

    override fun onDestroy() {
        valueEventListener?.let { databaseReference.removeEventListener(it) }
        super.onDestroy()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_trade_post, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.editPostMenuItem -> {
                if (editTradePostViewModel.isEditModeEnabled.value!!) {
                    post!!.pid?.let {
                        deletePostInDatabase(it)
                    }
                }
                else {
                    if (hasAccess) {
                        item.setIcon(R.drawable.ic_delete)
                        enableEditing()
                        editTradePostViewModel.isEditModeEnabled.value = true
                    }
                    else {
                        makeToast("You don't have access to edit this post")
                    }
                }
                true
            }
            R.id.postQrGenerate -> {
                launchQrGenerator()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (editTradePostViewModel.isEditModeEnabled.value!!) {
            menu.findItem(R.id.editPostMenuItem).setIcon(R.drawable.ic_delete)
        }
        else {
            menu.findItem(R.id.editPostMenuItem).setIcon(R.drawable.ic_edit)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun editPostInDatabase() {
        titleEditText = findViewById(R.id.edit_trade_title_edit_text)
        descriptionEditText = findViewById(R.id.edit_trade_description_edit_text)
        priceEditText = findViewById(R.id.edit_trade_price_edit_text)
        post!!.title = titleEditText.text.toString()
        post!!.description = descriptionEditText.text.toString()
        post!!.price = priceEditText.text.toString().toDouble()
        editTradePostViewModel.editTradePost(post!!.pid!!, post!!)
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
        postKeyToEdit = intent.getStringExtra(POST_KEY_TAG)
        val postReference = editTradePostViewModel.getTradePost(postKeyToEdit!!)
        postReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) {
                    makeToast("Data Does not exit")
                    finish()
                    return
                }
                post = snapshot.getValue(Post::class.java)
                post?.let {
                    editTradePostViewModel.databaseAccessed.value = true
                    editTradePostViewModel.post.value = it
                    editTradePostViewModel.post.value!!.pid = postKeyToEdit!!
                    checkPermission()
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
        finish()
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

    private fun launchQrGenerator() {
        if (postKeyToEdit == null) {
            makeToast("Post Id is null")
            return
        }
        val intent = TradePostQrGenerator.makeIntent(this, postKeyToEdit!!)
        startActivity(intent)
    }
}