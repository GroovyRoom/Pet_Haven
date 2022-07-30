package com.example.pethaven.dialog

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.example.pethaven.R
import com.example.pethaven.util.Permissions
import java.io.File

/**
 * Dialog Fragment supporting camera capture and gallery image
 **/
class PictureDialog : DialogFragment() {
    private lateinit var cameraButton: Button
    private lateinit var galleryButton: Button

    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    // Placeholder for the camera to write the image into
    private lateinit var tempImgUri: Uri
    private val tempImgFileName = "temp_profile_img.jpg"

    private var onImageResultListener: OnImageResultListener? = null

    interface OnImageResultListener {
        fun onResult(view : PictureDialog, which : Int, bitmap: Bitmap?)
    }

    companion object {
        const val CAMERA_BUTTON = 1
        const val GALLERY_BUTTON = 2
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog : Dialog

        //Set up Views
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_picture, null)
        setUpLauncher()
        setUpImage()
        setUpButtons(view)

        //Create dialog builder
        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(view)
        builder.setTitle("Pick Picture Profile")
        dialog = builder.create()

        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onImageResultListener = requireActivity() as? OnImageResultListener
    }

    private fun setUpImage() {
        //Create a temporary image uri placeholder for the camera to write into
        val tempImgFile = File(requireContext().getExternalFilesDir(null), tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(requireContext(), "com.example.pethaven",tempImgFile)
    }

    private fun setUpButtons(view: View){
        cameraButton = view.findViewById(R.id.cameraButton)
        galleryButton = view.findViewById(R.id.galleryButton)

        cameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
            imageLauncher.launch(intent)

        }

        galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }
    }

    private fun setUpLauncher() {
        imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val bitmap = Permissions.getBitmap(requireContext(), tempImgUri)
                onImageResultListener?.onResult(this, CAMERA_BUTTON, bitmap)
                dismiss()
            }
        }
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val dataUri = it.data?.data
                val bitmap = dataUri?.let { uri -> Permissions.getBitmap(requireContext(), uri) }
                onImageResultListener?.onResult(this, GALLERY_BUTTON, bitmap)

                dismiss()
            }
        }
    }

}