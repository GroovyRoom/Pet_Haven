package com.example.pethaven.ui.features.home

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.pethaven.R
import com.example.pethaven.dialog.PictureDialog
import com.example.pethaven.util.Permissions

class AddEditReptileActivity : AppCompatActivity(), PictureDialog.OnImageResultListener {
    private lateinit var addEditViewModel: AddEditReptileViewModel

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

    private fun setUpViews() {
        reptileImgView = findViewById(R.id.editReptileImg)
    }

    private fun setUpViewModel() {
        addEditViewModel = ViewModelProvider(this).get(AddEditReptileViewModel::class.java)
        addEditViewModel.reptileImg.observe(this) {
            reptileImgView.setImageBitmap(it)
        }
    }

    fun onSaveClicked(view: View) {}
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

    override fun onResult(view: PictureDialog, which: Int, bitmap: Bitmap?) {
        addEditViewModel.reptileImg.value = bitmap
    }
}