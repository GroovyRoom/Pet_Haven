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
import com.example.pethaven.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*
import com.example.pethaven.R
import com.example.pethaven.ui.RegisterActivity
import com.google.firebase.database.ktx.getValue

class ProfileFragment : Fragment() {
    val TAG = "jcy-Profile"
    private var mUser: User? = null
    private lateinit var uid: String
    private lateinit var ref: DatabaseReference
    lateinit var iv_head:ImageView
    lateinit var tv_name:TextView
    lateinit var tv_address:TextView
    lateinit var tv_phone:TextView
    lateinit var tv_email:TextView
    lateinit var tv_privacy:TextView
    lateinit var tv_isOpen:TextView
    lateinit var btn_edit:Button
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
        tv_isOpen=view.findViewById(R.id.tv_isOpen)
        tv_email=view.findViewById(R.id.tv_email)
        btn_edit=view.findViewById(R.id.btn_edit)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
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
        tv_email.text = user?.email ?: ""

        btn_edit.setOnClickListener {
            if(mUser==null){
                return@setOnClickListener
            }

            startActivity(Intent(requireContext(),ProfileEditActivity::class.java))
        }
    }


    private fun updateUserInfo() {
        mUser?.let { user ->
            Log.d(TAG, "updateUserInfo : $user");
            if (!TextUtils.isEmpty(user.profileImageUrl)) {
                iv_head.setPadding(0)
                Picasso.get().load(user.profileImageUrl).into(iv_head)
            }
            if (!TextUtils.isEmpty(user.username)) {
                tv_name.text = user.username
            }
            if (!TextUtils.isEmpty(user.phoneNumber)) {
                tv_phone.text = user.phoneNumber
            }
            if (!TextUtils.isEmpty(user.address)) {
                tv_address.text = user.address
            }
            if(user.isOpen){
                tv_isOpen.text = "YES"
            }else{
                tv_isOpen.text = "NO"
            }
        }
    }



    override fun onResume() {
        super.onResume()
        initView(requireView())

    }



}