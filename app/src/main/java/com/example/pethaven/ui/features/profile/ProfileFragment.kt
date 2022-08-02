package com.example.pethaven.ui.features.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.pethaven.ui.features.chat.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import com.example.pethaven.R
import com.example.pethaven.ui.RegisterActivity
import com.google.firebase.database.ktx.getValue

typealias onInputBack = (String) -> Unit

class ProfileFragment : Fragment() {
    val TAG = "jcy-Profile"
    private var isEdit = MutableLiveData(false);
    private var mUser: User? = null
    private lateinit var uid: String
    private lateinit var ref: DatabaseReference
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    lateinit var iv_head:ImageView
    lateinit var tv_name:TextView
    lateinit var tv_address:TextView
    lateinit var tv_phone:TextView
    lateinit var tv_email:TextView
    lateinit var tv_privacy:TextView
    lateinit var btn_save:Button
    lateinit var rl_address:RelativeLayout
    lateinit var rl_privacy:RelativeLayout
    lateinit var rl_number:RelativeLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        return view
    }

    private fun initView(view: View){
        iv_head=view.findViewById(R.id.iv_head)
        tv_name=view.findViewById(R.id.tv_name)
        tv_address=view.findViewById(R.id.tv_address)
        tv_phone=view.findViewById(R.id.tv_phone)
        tv_privacy=view.findViewById(R.id.tv_privacy)
        tv_email=view.findViewById(R.id.tv_email)
        btn_save=view.findViewById(R.id.btn_save)
        rl_address=view.findViewById(R.id.rl_address)
        rl_privacy=view.findViewById(R.id.rl_privacy)
        rl_number=view.findViewById(R.id.rl_number)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
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
                updateUserInfo()
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
        tv_name.setOnClickListener {
            showEditDialog("User Name", mUser?.username ?: "", InputType.TYPE_CLASS_TEXT) {
                if (mUser == null) {
                    mUser = User(uid)
                }
                mUser?.username = it
                isEdit.value=true
                updateUserInfo()
            }
        }

        rl_address.setOnClickListener {
            showEditDialog("Address", mUser?.address ?: "", InputType.TYPE_CLASS_TEXT) {
                if (mUser == null) {
                    mUser = User(uid)
                }
                mUser?.address = it
                isEdit.value=true
                updateUserInfo()
            }
        }

        rl_number.setOnClickListener {
            showEditDialog("Phone Number", mUser?.phoneNumber ?: "", InputType.TYPE_CLASS_PHONE) {
                if (mUser == null) {
                    mUser = User(uid)
                }
                mUser?.phoneNumber = it
                isEdit.value=true
                updateUserInfo()
            }
        }

        rl_privacy.setOnClickListener {
            var checkedItem = if (mUser?.isOpen == true) {
                0
            } else {
                1
            }
            var dialog = AlertDialog.Builder(requireContext())
                .setTitle(tv_privacy.text.toString())
                .setSingleChoiceItems(arrayOf("YES", "NO"), checkedItem) { dg, index ->
                    if (mUser == null) {
                        mUser = User(uid)
                    }
                    mUser?.isOpen = index == 0
                    isEdit.value=true
                    dg.dismiss()
                }
                .create()
            dialog.show()
        }
        btn_save.setOnClickListener {
            if(mUser==null){
                return@setOnClickListener
            }
            var dialog = AlertDialog.Builder(requireContext())
                .setTitle("Toast")
                .setMessage("Is Save Profile?")
                .setPositiveButton("Save") { dialog, switch ->
                    saving()
                    ref.setValue(mUser)
                        .addOnSuccessListener {
                            hideUploading()
                            Toast.makeText(requireContext(),"Save Success",Toast.LENGTH_LONG).show()
                            Log.d(RegisterActivity.TAG, "Finally we saved the user to Firebase Database")
                        }
                        .addOnFailureListener {
                            hideUploading()
                            Toast.makeText(requireContext(),"Save Failure ${it}",Toast.LENGTH_LONG).show()
                            Log.d(RegisterActivity.TAG, "Failed to set value to database: ${it.message}")
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
                Log.d(RegisterActivity.TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(RegisterActivity.TAG, "File Location: $it")
                    if (mUser == null) {
                        mUser = User(uid)
                    }
                    mUser?.profileImageUrl = it.toString();
                    isEdit.value=true
                    updateUserInfo()
                    hideUploading()
                }.addOnFailureListener {
                    hideUploading()
                }
            }
            .addOnFailureListener {
                hideUploading()
                Log.d(RegisterActivity.TAG, "Failed to upload image to storage: ${it.message}")
            }
    }


    private fun showEditDialog(title: String, text: String, inputType: Int, back: onInputBack) {
        var edInput = EditText(requireContext())
        edInput.inputType = inputType
        edInput.setText(text)
        var dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(edInput)
            .setPositiveButton("OK") { dialog, switch ->
                var input = edInput.text.toString();
                if (input.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Please Input $title", Toast.LENGTH_LONG)
                        .show()
                } else {
                    back.invoke(input)
                    dialog.dismiss()
                }

            }
            .setNegativeButton("CANCEL", null)
            .create()
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun updateUserInfo() {
        mUser?.let { user ->
            Log.d(TAG, "updateUserInfo : $user");
            if (!TextUtils.isEmpty(user.profileImageUrl)) {
                iv_head.setPadding(0)
                Picasso.get().load(user.profileImageUrl).into(iv_head)
            }
            if (!TextUtils.isEmpty(user.username)) {
                tv_name.setText(user.username)
            }
            if (!TextUtils.isEmpty(user.phoneNumber)) {
                tv_phone.setText(user.phoneNumber)
            }
            if (!TextUtils.isEmpty(user.address)) {
                tv_address.setText(user.address)
            }
        }
    }



    override fun onResume() {
        super.onResume()
        initView(requireView())
        isEdit.observe(this) {
            if (it) {
                btn_save.visibility = View.VISIBLE
            } else {
                btn_save.visibility = View.GONE
            }
        }
    }

    var mUploading: ProgressDialog? = null

    private fun showUploading(curSize: Long, allSize: Long) {
        if (mUploading == null) {
            mUploading = ProgressDialog(requireContext())
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
            mUploading = ProgressDialog(requireContext())
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