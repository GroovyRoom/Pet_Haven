package com.example.pethaven.ui.features.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.pethaven.R
import com.example.pethaven.databinding.ActivityAddEditReptileBinding
import com.example.pethaven.dialog.PictureDialog
import com.example.pethaven.domain.Reptile
import com.example.pethaven.ui.MainActivity
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.example.pethaven.util.Permissions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

/**
 *  Activity to add or edit Reptile to the database
 */
class AddEditReptileActivity : AppCompatActivity(), PictureDialog.OnImageResultListener {
    private lateinit var addEditViewModel: AddEditReptileViewModel

    private lateinit var binding: ActivityAddEditReptileBinding

    private lateinit var progressBar: ProgressBar
    private var storageTask: StorageTask<UploadTask.TaskSnapshot>? = null

    private lateinit var reptileImgView: ImageView
    private var isEditMode: Boolean = false
    private var reptileKeyToEdit: String? = null
    private var reptileToEdit: Reptile ?= null

    private lateinit var reptile : Reptile
    /*
        Check if data has already been received to prevent editText from being updated again during
        orientation change
     */
    private var hasReceived: Boolean = false

    companion object {
        private const val PICTURE_OPTION_DIALOG_TAG = "Picture Option Dialog Tag"
        private const val IS_EDIT_MODE = "Edit Reptile Mode"
        private const val EDIT_REPTILE_KEY_TAG = "Key of Reptile to edit"
        private const val HAS_RECEIVED_TAG = "Has Received Tag"

        fun makeIntent(context: Context, isEditMode: Boolean = false, key: String): Intent {
            val intent = Intent(context, AddEditReptileActivity::class.java).apply {
                putExtra(IS_EDIT_MODE, isEditMode)
                putExtra(EDIT_REPTILE_KEY_TAG, key)
            }
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditReptileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpViews()
        setUpViewModel()

        Permissions.checkImagePermissions(this)

        // Check if activity is on Edit Mode
        hasReceived = savedInstanceState?.getBoolean(HAS_RECEIVED_TAG) ?: false
        checkIsEditMode()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(HAS_RECEIVED_TAG, hasReceived)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEditMode) {
            menuInflater.inflate(R.menu.menu_edit_reptile, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.deleteReptileMenuItem -> {
                reptileKeyToEdit?.let { deleteReptileInDatabase(it) }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    ///-------------------------- Setting up Activity -------------------------///

    private fun setUpViews() {
        progressBar = findViewById(R.id.progressBar)
        reptileImgView = findViewById(R.id.editReptileImg)

        binding.editReptileName.doOnTextChanged { text, _, _, _ ->
            binding.editReptileNameLayout.helperText = if (TextUtils.isEmpty(text)) "*Required" else ""
        }

        binding.editReptileSpecies.doOnTextChanged { text, _, _, _ ->
            binding.editReptileSpeciesLayout.helperText = if (TextUtils.isEmpty(text)) "*Required" else ""
        }

        binding.editReptileAge.doOnTextChanged { text, _, _, _ ->
            binding.editReptileAgeLayout.helperText =  if (TextUtils.isEmpty(text)) "*Required" else ""
            binding.editReptileAgeLayout.error = if (!TextUtils.isDigitsOnly(text)) {
                "Only digits allowed"
            } else if (!TextUtils.isEmpty(text) && text.toString().toInt() !in 1000 downTo -1) {
                "Please enter a valid age"
            } else {
                null
            }
        }

        binding.editReptileDescription.doOnTextChanged { text, _, _, _ ->
            text?.let {
                binding.editReptileDescriptionLayout.error =
                    if (it.length > binding.editReptileDescriptionLayout.counterMaxLength) "Max " + "Character Count Reached!" else null
            }
        }
    }

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        addEditViewModel = ViewModelProvider(this, factory)[AddEditReptileViewModel::class.java]
        addEditViewModel.reptileImg.observe(this) {
            reptileImgView.setImageBitmap(it)
        }
    }

    private fun checkIsEditMode() {
        isEditMode = intent.getBooleanExtra(IS_EDIT_MODE, false)
        if (isEditMode) {
            reptileKeyToEdit = intent.getStringExtra(EDIT_REPTILE_KEY_TAG)
            if (reptileKeyToEdit == null) {
                makeToast("Error in receiving reptile Key!")
                return
            }
            getReptileFromDatabase()
        }
    }

    ///-------------------------- Updating Views  -------------------------///
    fun updateView(reptile: Reptile) {
        if (!hasReceived) {
            binding.editReptileName.setText(reptile.name)
            binding.editReptileSpecies.setText(reptile.species)
            binding.editReptileAge.setText(reptile.age.toString())
            binding.editReptileDescription.setText(reptile.description)
        }

        if(addEditViewModel.reptileImg.value == null) {
            Glide.with(this)
                .load(reptile.imgUri)
                .fitCenter()
                .into(reptileImgView)
        }

        hasReceived = true
    }
    ///-------------------------- On Click Listeners ------------------------///
    fun onSaveClicked(view: View) {
        if (!isEditFormValid()) {
            makeToast("Please make sure all inputs are written properly")
            return
        }

        if (storageTask != null && storageTask!!.isInProgress) {
            makeToast("Data is currently being uploaded")
            return
        }

        if (storageTask != null && storageTask!!.isComplete) {
            return
        }

        if (isEditMode) {
            reptile = Reptile(
                name = binding.editReptileName.text.toString(),
                species = binding.editReptileSpecies.text.toString(),
                age = binding.editReptileAge.text.toString().toInt(),
                description = binding.editReptileDescription.text.toString(),
                isFav = reptileToEdit!!.isFav
            )
        } else {
            reptile = Reptile(
                name = binding.editReptileName.text.toString(),
                species = binding.editReptileSpecies.text.toString(),
                age = binding.editReptileAge.text.toString().toInt(),
                description = binding.editReptileDescription.text.toString()
            )
        }

        if (isEditMode) updateReptileInDatabase(reptile) else addToDatabaseAndFinish(reptile)
    }

    fun onCancelClicked(view: View) {
        finish()
    }

    fun onImageClicked(view: View) {
        if (!Permissions.hasImagePermissions(this)) {
            Permissions.requestImagePermissions(this)
            Toast.makeText(this, "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show()
            return
        }
        val pictureDialog = PictureDialog()
        pictureDialog.show(supportFragmentManager, PICTURE_OPTION_DIALOG_TAG)
    }
    ///-------------------------- Database  -------------------------///
    private fun updateReptileInDatabase(reptile: Reptile) {
        if (reptileKeyToEdit == null) {
            return
        }

        progressBar.isIndeterminate = true
        if (addEditViewModel.reptileImgUri.value != null) {
                uploadImageToDatabase(addEditViewModel.reptileImgUri.value!!){
                    reptile.imgUri = it.toString()
                    updateReptileInDatabaseAux(reptile)
                }
        } else {
            reptile.imgUri = reptileToEdit?.imgUri
            updateReptileInDatabaseAux(reptile)
        }
        //updatePostsInDatabase(reptileKeyToEdit!!, reptile)
    }

    private fun updateReptileInDatabaseAux(reptile: Reptile) {
        addEditViewModel.updateReptileInDatabase(reptileKeyToEdit!!, reptile)
            .addOnSuccessListener {
                makeToast("Edit Successful!")
                finish()
            }
            .addOnFailureListener {
                makeToast(it.message ?: "Unknown Exception Occurred")
            }
        updatePostsInDatabase(reptileKeyToEdit!!, reptile)

    }

    private fun updatePostsInDatabase(key: String, reptile: Reptile) {
        addEditViewModel.getPostsByReptileID(key).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    postSnapshot.ref.child("imgUri").setValue(reptile.imgUri)
                    postSnapshot.ref.child("reptileName").setValue(reptile.name)
                    println("Debug: Reptile Name: ${reptile.name}")
                    }
                }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addToDatabaseAndFinish(reptile: Reptile) {
        //addEditViewModel.insertToDatabase(reptile)
        addEditViewModel.reptileImgUri.value?.let {
            progressBar.isIndeterminate = true
            uploadImageToDatabase(it) { downloadedUri ->
                reptile.imgUri = downloadedUri.toString()
                addToDatabaseAndFinishAux(reptile)
            }
        }
    }

    private fun addToDatabaseAndFinishAux(reptile: Reptile) {
        addEditViewModel.insertToDatabase(reptile).addOnSuccessListener {
            makeToast("Upload Success")
            finish()
        }.addOnFailureListener {
            makeToast(it.message ?: "Unknown Exception Occurred")
        }
    }

    private fun deleteReptileInDatabase(key: String) {
        deletePostInDatabase(key)
        addEditViewModel.deleteImage(reptileToEdit!!.imgUri!!)
            .addOnSuccessListener {
                addEditViewModel.deleteReptile(key).addOnSuccessListener {
                    makeToast("Delete Successful")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                makeToast(it.message ?: "Unknown Exception Occurred")
            }
    }

    private fun deletePostInDatabase(key: String) {
        addEditViewModel.getPostsByReptileID(key).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapShot in snapshot.children) {
                    postSnapShot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun getReptileFromDatabase() {
        val reptileReference = addEditViewModel.getReptileFromCurrentUser(reptileKeyToEdit!!)
        reptileReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) {
                    return
                }
                reptileToEdit = snapshot.getValue(Reptile::class.java)
                reptileToEdit?.let {
                    updateView(it)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }
        })
    }

    private fun uploadImageToDatabase(imgUri: Uri, uriSuccessListener: OnSuccessListener<Uri>) {
        storageTask = addEditViewModel.uploadImage(imgUri)
            .addOnSuccessListener { taskSnapShop ->
                taskSnapShop.metadata?.reference?.let {
                    val result = taskSnapShop.storage.downloadUrl
                    result.addOnSuccessListener(uriSuccessListener)
                }
            }.addOnFailureListener {
                progressBar.progress = 0
                makeToast(it.message ?: "Unknown Exception Occurred")
            }.addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                progressBar.progress = progress.toInt()
            }
    }

    ///-------------------------- Picture Dialog Listener -------------------------///

    override fun onResult(view: PictureDialog, which: Int, bitmap: Bitmap?, uri: Uri) {
        addEditViewModel.reptileImg.value = bitmap
        addEditViewModel.reptileImgUri.value = uri
    }

    ///-------------------------- Reptile Information Validation -------------------------///
    private fun isEditFormValid(): Boolean {
        return ! (
                TextUtils.isEmpty(binding.editReptileName.text.toString()) ||
                        TextUtils.isEmpty(binding.editReptileSpecies.text.toString()) ||
                        !isAgeValid() ||
                        !isDescriptionValid() ||
                        !isImageValid()
                )

    }

    private fun isImageValid(): Boolean {
        if (isEditMode) {
            return true
        }
        return addEditViewModel.reptileImgUri.value != null
    }

    private fun isAgeValid(): Boolean {
        return !TextUtils.isEmpty(binding.editReptileAge.text.toString())&&
                TextUtils.isDigitsOnly(binding.editReptileAge.text.toString()) &&
                binding.editReptileAge.text.toString().toInt() in 1000 downTo -1

    }

    private fun isDescriptionValid(): Boolean {
        return (binding.editReptileDescription.text.toString().length <=
                                            binding.editReptileDescriptionLayout.counterMaxLength)
    }
}