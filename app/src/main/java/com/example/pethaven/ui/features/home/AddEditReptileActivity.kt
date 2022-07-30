package com.example.pethaven.ui.features.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.R
import com.example.pethaven.dialog.PictureDialog
import com.example.pethaven.domain.Reptile
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.example.pethaven.util.Permissions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

class AddEditReptileActivity : AppCompatActivity(), PictureDialog.OnImageResultListener {
    private lateinit var addEditViewModel: AddEditReptileViewModel

    private lateinit var editName: TextInputEditText
    private lateinit var editSpecies: TextInputEditText
    private lateinit var editAge: TextInputEditText
    private lateinit var editDescription: TextInputEditText

    private lateinit var editNameLayout: TextInputLayout
    private lateinit var editSpeciesLayout: TextInputLayout
    private lateinit var editAgeLayout: TextInputLayout
    private lateinit var editDescriptionLayout: TextInputLayout

    private lateinit var progressBar: ProgressBar
    private var storageTask: StorageTask<UploadTask.TaskSnapshot>? = null

    companion object {
        private const val PICTURE_OPTION_DIALOG_TAG = "Picture Option Dialog Tag"
    }

    // Views
    private lateinit var reptileImgView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_reptile)
        setUpViews()
        setUpViewModel()

        Permissions.checkImagePermissions(this)
    }

    ///-------------------------- Setting up Activity -------------------------///

    private fun setUpViews() {
        progressBar = findViewById(R.id.progressBar)
        reptileImgView = findViewById(R.id.editReptileImg)

        editNameLayout = findViewById(R.id.editReptileNameLayout)
        editName = findViewById(R.id.editReptileName)
        editName.doOnTextChanged { text, start, before, count ->
            editNameLayout.helperText = if (TextUtils.isEmpty(text)) "*Required" else ""
        }

        editSpeciesLayout = findViewById(R.id.editReptileSpeciesLayout)
        editSpecies = findViewById(R.id.editReptileSpecies)
        editSpecies.doOnTextChanged { text, start, before, count ->
            editSpeciesLayout.helperText = if (TextUtils.isEmpty(text)) "*Required" else ""
        }

        editAgeLayout = findViewById(R.id.editReptileAgeLayout)
        editAge = findViewById(R.id.editReptileAge)
        editAge.doOnTextChanged { text, start, before, count ->
            editAgeLayout.helperText =  if (TextUtils.isEmpty(text)) "*Required" else ""
            editAgeLayout.error = if (!TextUtils.isDigitsOnly(text)) {
                "Only digits allowed"
            } else if (!TextUtils.isEmpty(text) && text.toString().toInt() !in 1001 downTo -1) {
                "Please enter a valid age"
            } else {
                null
            }
        }

        editDescriptionLayout = findViewById(R.id.editReptileDescriptionLayout)
        editDescription = findViewById(R.id.editReptileDescription)
        editDescription.doOnTextChanged { text, start, before, count ->
            text?.let {
                editDescriptionLayout.error =
                    if (it.length > editDescriptionLayout.counterMaxLength) "Max Character Count Reached!" else null
            }
        }
    }

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        addEditViewModel = ViewModelProvider(this, factory).get(AddEditReptileViewModel::class.java)
        addEditViewModel.reptileImg.observe(this) {
            reptileImgView.setImageBitmap(it)
        }
    }

    ///-------------------------- On CLick Listeners ------------------------///

    fun onSaveClicked(view: View) {
        if (!isEditFormValid()) {
            makeToast("Please make sure all inputs are written properly")
            return
        }

        if (storageTask != null && storageTask!!.isInProgress) {
            makeToast("Data is currently being uploaded")

        }

        val reptile = Reptile(
            name = editName.text.toString(),
            species = editSpecies.text.toString(),
            age = editAge.text.toString().toInt(),
            description = editDescription.text.toString(),
        )
//        addEditViewModel.insertToDatabase(reptile)
        addEditViewModel.reptileImgUri.value?.let {
            storageTask = addEditViewModel.uploadImage(addEditViewModel.reptileImgUri.value!!).
                            addOnSuccessListener {
                                addToDatabaseAndFinish(reptile)
                            }.
                            addOnFailureListener {
                                makeToast(it.message ?: "Unknown Exception Occurred")
                            }.
                            addOnProgressListener {
                                val progress = (100.0 * it.bytesTransferred/ it.totalByteCount)
                                progressBar.progress = progress.toInt()
                            }
        }
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
    private fun addToDatabaseAndFinish(reptile: Reptile) {
        addEditViewModel.insertToDatabase2(reptile).addOnSuccessListener {
            makeToast("Upload Success")
            finish()
        }.addOnFailureListener {
            makeToast(it.message ?: "Unknown Exception Occurred")
        }
    }

    ///-------------------------- Picture Dialog Listener -------------------------///

    override fun onResult(view: PictureDialog, which: Int, bitmap: Bitmap?, uri: Uri) {
        addEditViewModel.reptileImg.value = bitmap
        addEditViewModel.reptileImgUri.value = uri
    }

    ///-------------------------- Edit Text Validation -------------------------///
    private fun isEditFormValid(): Boolean {
        return ! (
                TextUtils.isEmpty(editName.text.toString()) ||
                TextUtils.isEmpty(editSpecies.text.toString()) ||
                !isAgeValid() ||
                !isDescriptionValid()
                )

    }
    private fun isAgeValid(): Boolean {
        return !TextUtils.isEmpty(editAge.text.toString())&&
                TextUtils.isDigitsOnly(editAge.text.toString()) &&
                editAge.text.toString().toInt() in 1001 downTo -1

    }

    private fun isDescriptionValid(): Boolean {
        return (editDescription.text.toString().length <= editDescriptionLayout.counterMaxLength)
    }
}