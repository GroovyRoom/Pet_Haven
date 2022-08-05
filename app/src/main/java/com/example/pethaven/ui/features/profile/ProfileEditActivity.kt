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
    private lateinit var userReference: DatabaseReference

    private lateinit var iv_head: ImageView
    private lateinit var ed_name: EditText
    private lateinit var ed_address: EditText
    private lateinit var ed_phone: EditText
    private lateinit var tv_email:TextView
    private lateinit var sp_privacy: Spinner
    private lateinit var btn_save: Button

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setUpView()
        setUpViewModel()

        hasReceived = savedInstanceState?.getBoolean("test") ?: false

        receiverCurrentUserInfo()
    }

    private fun setUpView() {
        iv_head=findViewById(R.id.iv_head)

        ed_name=findViewById(R.id.ed_name)
        ed_address=findViewById(R.id.ed_address)
        ed_phone=findViewById(R.id.ed_phone)

        tv_email=findViewById(R.id.tv_email)
        sp_privacy=findViewById(R.id.sp_privacy)
        sp_privacy=findViewById(R.id.sp_privacy)

        progressBar = findViewById(R.id.progressBar)

        btn_save=findViewById(R.id.btn_save)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("test", hasReceived)
        super.onSaveInstanceState(outState)
    }

    ///-------------------------- Setting Up Activity -------------------------///

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(this)
        profileEditViewModel = ViewModelProvider(this, factory)[ProfileEditViewModel::class.java]
        profileEditViewModel.profileImg.observe(this) {
            iv_head.setImageBitmap(it)
        }
    }

    ///-------------------------- Updating Views ------------------------///
    fun updateView(user: User) {
        if (!hasReceived) {
            ed_name.setText(user.username)
            ed_phone.setText(user.phoneNumber)
            ed_address.setText(user.address)
            if(user.isOpen) sp_privacy.setSelection(0) else sp_privacy.setSelection(1)
        }

        if(profileEditViewModel.profileImgUri.value == null) {
            Glide.with(this)
                .load(user.profileImageUrl)
                .fitCenter()
                .into(iv_head)
        }


    }

    ///-------------------------- Check Validation ------------------------///
    private fun isFieldInputValid(): Boolean {
        val editName = ed_name.text.toString()
        val editPhone = ed_phone.text.toString()
        val editAddress = ed_address.text.toString()

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
            username = ed_name.text.toString()
            address = ed_phone.text.toString()
            phoneNumber = ed_address.text.toString()
            isOpen = sp_privacy.selectedItemPosition == 0
        }


        progressBar.isIndeterminate = true
        updateUserInDatabase(mUser!!)
    }

    ///-------------------------- DataBase Operations------------------------///

    private fun receiverCurrentUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser
        tv_email.text = user?.email ?: ""

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
                makeToast(it.message ?: "Unknown Exception Occured")
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