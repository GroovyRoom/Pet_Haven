package com.example.pethaven.ui.features.home

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.R
import com.example.pethaven.dialog.PictureDialog
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileDao
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.example.pethaven.util.Permissions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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

    private lateinit var reptileDao: ReptileDao

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

        reptileDao = ReptileDao()

        Permissions.checkImagePermissions(this)
    }

    ///-------------------------- Setting up Activity -------------------------///

    private fun setUpViews() {
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

        val reptile = Reptile(
            name = editName.text.toString(),
            species = editSpecies.text.toString(),
            age = editAge.text.toString(),
            description = editDescription.text.toString(),
        )
        addEditViewModel.insertToDatabase(reptile)

        finish()
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
        return TextUtils.isDigitsOnly(editAge.text.toString()) ||
                editAge.text.toString().toInt() in 1001 downTo -1

    }

    private fun isDescriptionValid(): Boolean {
        return (editDescription.text.toString().length <= editDescriptionLayout.counterMaxLength)
    }
}