package com.example.pethaven.ui.features.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.example.pethaven.R
import com.example.pethaven.domain.User
import com.example.pethaven.ui.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

/**
 *
 * @name:    ProfileEditActivity
 * @date:    2022-08-04 13:23
 * @comment:
 *
 */
class ProfileEditActivity:AppCompatActivity() {
    private var mUser: User? = null
    private lateinit var uid: String
    private lateinit var ref: DatabaseReference
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    lateinit var iv_head: ImageView
    lateinit var ed_name: EditText
    lateinit var ed_address: EditText
    lateinit var ed_phone: EditText
    lateinit var tv_email:TextView
    lateinit var sp_privacy: Spinner
    lateinit var btn_save: Button
    private val TAG = "jcy-ProfileEdit"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        iv_head=findViewById(R.id.iv_head)
        ed_name=findViewById(R.id.ed_name)
        ed_address=findViewById(R.id.ed_address)
        ed_phone=findViewById(R.id.ed_phone)
        tv_email=findViewById(R.id.tv_email)
        sp_privacy=findViewById(R.id.sp_privacy)
        btn_save=findViewById(R.id.btn_save)
        sp_privacy=findViewById(R.id.sp_privacy)
        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    it.data?.data?.let {
                        uploadImageToFirebaseStorage(it)
                    }
                }
            }
        var user = FirebaseAuth.getInstance().currentUser
        uid = FirebaseAuth.getInstance().uid ?: ""
        ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user = dataSnapshot.getValue<User>()
                mUser = user
                Log.w(TAG, "user $user")
                mUser?.let { user ->
                    Log.d(TAG, "updateUserInfo : $user");
                    if (!TextUtils.isEmpty(user.profileImageUrl)) {
                        iv_head.setPadding(0)
                        Picasso.get().load(user.profileImageUrl).into(iv_head)
                    }
                    if (!TextUtils.isEmpty(user.username)) {
                        ed_name.setText(user.username)
                    }
                    if (!TextUtils.isEmpty(user.phoneNumber)) {
                        ed_phone.setText(user.phoneNumber)
                    }
                    if (!TextUtils.isEmpty(user.address)) {
                        ed_address.setText(user.address)
                    }
                    if(user.isOpen==true){
                        sp_privacy.setSelection(0)
                    }else{
                        sp_privacy.setSelection(1)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        ref.addValueEventListener(postListener)
        tv_email.setText(user?.email ?: "")
        iv_head.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imageLauncher.launch(intent)
        }

        btn_save.setOnClickListener {
            var name = ed_name.text.toString();
            var phone = ed_phone.text.toString();
            var address = ed_address.text.toString();
            if(TextUtils.isEmpty(name)){
                Toast.makeText(this,"Please Input Name",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(phone)){
                Toast.makeText(this,"Please Input Phone Number",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(address)){
                Toast.makeText(this,"Please Input Address",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(mUser?.profileImageUrl)){
                Toast.makeText(this,"Please Select Photo",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(mUser==null){
                mUser = User(uid)
            }
            mUser?.username = name
            mUser?.address = address
            mUser?.phoneNumber = phone
            mUser?.isOpen = sp_privacy.selectedItemPosition==0
            var dialog = AlertDialog.Builder(this)
                .setTitle("Toast")
                .setMessage("Is Save Profile?")
                .setPositiveButton("Save") { dialog, switch ->
                    saving()
                    ref.setValue(mUser)
                        .addOnSuccessListener {
                            hideUploading()
                            Toast.makeText(this,"Save Success",Toast.LENGTH_LONG).show()
                            Log.d(TAG, "Finally we saved the user to Firebase Database")
                            finish()
                        }
                        .addOnFailureListener {
                            hideUploading()
                            Toast.makeText(this,"Save Failure ${it}",Toast.LENGTH_LONG).show()
                            Log.d(TAG, "Failed to set value to database: ${it.message}")
                        }
                }
                .setNegativeButton("CANCEL", null)
                .create()
            dialog.show()
        }
    }
    private fun uploadImageToFirebaseStorage(uri: Uri) {

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(uri)
            .addOnProgressListener {snapshot->
                showUploading(snapshot.bytesTransferred, snapshot.totalByteCount)
            }
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")
                    if (mUser == null) {
                        mUser = User(uid)
                    }
                    mUser?.profileImageUrl = it.toString();
                    if (!TextUtils.isEmpty(mUser!!.profileImageUrl)) {
                        iv_head.setPadding(0)
                        Picasso.get().load(mUser!!.profileImageUrl).into(iv_head)
                    }
                    hideUploading()
                }.addOnFailureListener {
                    hideUploading()
                }
            }
            .addOnFailureListener {
                hideUploading()
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    var mUploading: ProgressDialog? = null

    private fun showUploading(curSize: Long, allSize: Long) {
        if (mUploading == null) {
            mUploading = ProgressDialog(this)
            mUploading?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL) // 设置水平进度条
            mUploading?.setCancelable(true) // 设置是否可以通过点击Back键取消
            mUploading?.setCanceledOnTouchOutside(false) // 设置在点击Dialog外是否取消Dialog进度条
            mUploading?.setTitle("Updating Profile Image")
            mUploading?.max = 100
            mUploading?.setMessage("Uploading Image...")
            mUploading?.show()
        } else {
            val progress = (curSize.toDouble() / allSize * 100).toInt()
            mUploading!!.incrementProgressBy(progress)
        }
    }
    private fun saving() {
        if (mUploading == null) {
            mUploading = ProgressDialog(this)
            mUploading?.setCancelable(true) // 设置是否可以通过点击Back键取消
            mUploading?.setCanceledOnTouchOutside(false) // 设置在点击Dialog外是否取消Dialog进度条
            mUploading?.setTitle("Toast")
            mUploading?.setMessage("Saving")
            mUploading?.show()
        }
    }

    private fun hideUploading() {
        if (mUploading != null) {
            mUploading!!.dismiss()
            mUploading = null
        }
    }
}