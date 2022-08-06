package com.example.pethaven.ui.features.profile


import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.pethaven.R
import com.example.pethaven.dialog.PictureDialog
import com.example.pethaven.domain.User
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.example.pethaven.util.Permissions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask

/**
 *
 * @name:    ProfileEditActivity
 * @date:    2022-08-04 13:23
 * @comment:
 *
 */
class ProfileEditActivity : AppCompatActivity(), PictureDialog.OnImageResultListener {
    private var mUser: User? = null
    private lateinit var uid: String

    private lateinit var editImage: ImageView
    private lateinit var editName: EditText
    private lateinit var editAddress: EditText
    private lateinit var editPhone: EditText
    private lateinit var emailTextView:TextView
    private lateinit var privacySpinner: Spinner
    private lateinit var saveBtn: Button

    private lateinit var progressBar: ProgressBar
    private lateinit var profileEditViewModel: ProfileEditViewModel

    private var storageTask: StorageTask<UploadTask.TaskSnapshot>? = null

    private val TAG = "jcy-ProfileEdit"

    /*
        Check if data has already been received to prevent editText from being updated again during
        orientation change
    */
    private var hasReceived: Boolean = false


    companion object {
        private const val PROFILE_PICTURE_DIALOG_TAG = "Profile Picture Dialog Tag"
        private const val USER_HAS_RECEIVED_TAG = "User Has Received Tag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setUpView()
        setUpViewModel()

        hasReceived = savedInstanceState?.getBoolean(USER_HAS_RECEIVED_TAG) ?: false

        receiverCurrentUserInfo()
    }

    private fun setUpView() {
        editImage=findViewById(R.id.iv_head)

        editName=findViewById(R.id.ed_name)
        editAddress=findViewById(R.id.ed_address)
        editPhone=findViewById(R.id.ed_phone)

        emailTextView=findViewById(R.id.tv_email)
        privacySpinner=findViewById(R.id.sp_privacy)
        privacySpinner=findViewById(R.id.sp_privacy)

        progressBar = findViewById(R.id.progressBar)

        saveBtn=findViewById(R.id.btn_save)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(USER_HAS_RECEIVED_TAG, hasReceived)
        super.onSaveInstanceState(outState)
    }

    ///-------------------------- Setting Up Activity -------------------------///

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        profileEditViewModel = ViewModelProvider(this, factory)[ProfileEditViewModel::class.java]
        profileEditViewModel.profileImg.observe(this) {
            editImage.setImageBitmap(it)
        }
    }

    ///-------------------------- Updating Views ------------------------///
    fun updateView(user: User) {
        if (!hasReceived) {
            editName.setText(user.username)
            editPhone.setText(user.phoneNumber)
            editAddress.setText(user.address)
            if(user.isOpen) privacySpinner.setSelection(0) else privacySpinner.setSelection(1)
        }

        if(profileEditViewModel.profileImgUri.value == null) {
            Glide.with(this)
                .load(user.profileImageUrl)
                .fitCenter()
                .into(editImage)
        }


    }

    ///-------------------------- Check Validation ------------------------///
    private fun isFieldInputValid(): Boolean {
        val editName = editName.text.toString()
        val editPhone = editPhone.text.toString()
        val editAddress = editAddress.text.toString()

        if(TextUtils.isEmpty(editName)){
            makeToast("Please Insert Name")
            return false
        }
        if(TextUtils.isEmpty(editPhone)){
            makeToast("Please Insert Phone Number")
            return false
        }
        if(TextUtils.isEmpty(editAddress)){
            makeToast("Please Insert Address")
            return false
        }
        if(TextUtils.isEmpty(mUser?.profileImageUrl) || mUser?.profileImageUrl == ""){
            makeToast("Please Insert Photo")
            return false
        }

        return true
    }

    ///-------------------------- On Click Listeners ------------------------///
    fun onProfileImageClicked(view: View) {
        if (!Permissions.hasImagePermissions(this)) {
            Permissions.requestImagePermissions(this)
            makeToast("Please enable camera and storage permissions")
            return
        }
        val pictureDialog = PictureDialog()
        pictureDialog.show(supportFragmentManager, PROFILE_PICTURE_DIALOG_TAG)
    }

    fun onProfileSaveClicked(view: View) {
        if (storageTask != null && storageTask!!.isInProgress) {
            makeToast("Data is being Uploaded")
            return
        }

        if (!isFieldInputValid()) {
            return
        }

        if(mUser==null) mUser = User(uid)
        mUser!!.apply {
            username = editName.text.toString()
            address = editAddress.text.toString()
            phoneNumber = editPhone.text.toString()
            isOpen = privacySpinner.selectedItemPosition == 0
        }


        progressBar.isIndeterminate = true
        updateUserInDatabase(mUser!!)
    }

    fun onProfileCancelClicked(view: View) {
        finish()
    }

    ///-------------------------- DataBase Operations------------------------///

    private fun receiverCurrentUserInfo() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        emailTextView.text = currentUser?.email ?: ""

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user = dataSnapshot.getValue<User>()
                Log.w(TAG, "user $user")

                mUser = user
                mUser?.let {
                    Log.d(TAG, "updateUserInfo : $it")
                    updateView(it)
                }

                hasReceived = true
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                makeToast(databaseError.message)
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        profileEditViewModel.getCurrentUserObject().addListenerForSingleValueEvent(postListener)
    }
    private fun updateUserInDatabase(user: User) {
        progressBar.isIndeterminate = true
        if (profileEditViewModel.profileImgUri.value != null) {
            uploadImageToDatabase(profileEditViewModel.profileImgUri.value!!){
                user.profileImageUrl = it.toString()
                updateReptileInDatabaseAux(user)
            }
        } else {
            user.profileImageUrl = mUser!!.profileImageUrl
            updateReptileInDatabaseAux(user)
        }
    }

    private fun updateReptileInDatabaseAux(user: User) {
        profileEditViewModel.updateUser(user)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                makeToast("Save Successful")
                finish()
            }
            .addOnFailureListener {
                makeToast(it.message ?: "Unknown Exception Occurred")
            }
    }

    private fun uploadImageToDatabase(imgUri: Uri, uriSuccessListener: OnSuccessListener<Uri>) {
        storageTask = profileEditViewModel.uploadImage(imgUri)
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
        profileEditViewModel.profileImg.value = bitmap
        profileEditViewModel.profileImgUri.value = uri
    }


}