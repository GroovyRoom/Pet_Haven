package com.example.pethaven.ui.features.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.pethaven.R
import com.example.pethaven.domain.Reptile
import com.example.pethaven.ui.features.shop.TradePostActivity
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ReptileProfileActivity : AppCompatActivity() {
    companion object {
        private const val REPTILE_INFO_KEY_TAG = "Reptile Info Key Tag"

        fun makeIntent(context: Context, reptileKey: String): Intent {
            val intent = Intent(context, ReptileProfileActivity::class.java)
            intent.putExtra(REPTILE_INFO_KEY_TAG, reptileKey)
            return intent
        }
    }

    private lateinit var reptileKey: String
    private lateinit var reptileProfileViewModel: ReptileProfileViewModel

    private lateinit var nameTextView: TextView
    private lateinit var speciesTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var descTextView: TextView
    private lateinit var reptileImgView: ImageView

    private lateinit var postFab: FloatingActionButton

    private lateinit var databaseReference: DatabaseReference
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reptile_profile)
        setUpTextView()
        setUpViewModel()
        setUpFloatingActionButton()

        reptileKey = intent.getStringExtra(REPTILE_INFO_KEY_TAG) ?: ""
        databaseReference = reptileProfileViewModel.getReptileFromCurrentUser(reptileKey)
        valueEventListener = databaseReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    return
                }
                val reptile = snapshot.getValue(Reptile::class.java)
                reptile?.let {
                    nameTextView.text = it.name
                    speciesTextView.text = it.species
                    ageTextView.text = it.age.toString()
                    descTextView.text = it.description

                    if(it.imgUri != null){
                        Glide.with(this@ReptileProfileActivity)
                            .load(it.imgUri)
                            .fitCenter()
                            .into(reptileImgView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }

        })
    }

    private fun setUpFloatingActionButton() {
        postFab = findViewById(R.id.addPostFab)
        postFab.setOnClickListener{
            val intent = TradePostActivity.makeIntent(this, reptileKey)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_reptile_info, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.editReptileMenuItem -> {
                val intent = AddEditReptileActivity.makeIntent(this, true, reptileKey)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        valueEventListener?.let { databaseReference.removeEventListener(it) }
        super.onDestroy()
    }

    ///-------------------------- Setting up Activity -------------------------///

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        reptileProfileViewModel = ViewModelProvider(this, factory).get(ReptileProfileViewModel::class.java)
    }

    private fun setUpTextView() {
        nameTextView = findViewById(R.id.reptileInfoNameText)
        speciesTextView = findViewById(R.id.reptileInfoSpeciesText)
        ageTextView = findViewById(R.id.reptileInfoAgeText)
        descTextView = findViewById(R.id.reptileInfoDescriptionText)
        reptileImgView = findViewById(R.id.reptileInfoImage)
    }

    ///-------------------------- Receiving Reptile Information -------------------------///

}