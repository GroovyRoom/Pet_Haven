package com.example.pethaven.ui.features.profile

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.pethaven.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.example.pethaven.R
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.firebase.database.ktx.getValue

class ProfileFragment : Fragment() {
    val TAG = "jcy-Profile"
    private var mUser: User? = null
    private lateinit var uid: String
    private lateinit var ref: DatabaseReference

    private lateinit var iv_head:ImageView

    private lateinit var tv_name:TextView
    private lateinit var tv_address:TextView
    private lateinit var tv_phone:TextView
    private lateinit var tv_email:TextView
    private lateinit var tv_privacy:TextView
    private lateinit var tv_isOpen:TextView

    private lateinit var btn_edit:Button
    private lateinit var progressBar: ProgressBar

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initView(view)
        setUpViewModel()
        setUpButton(view)

        return view
    }

    private fun setUpButton(view: View) {
        btn_edit=view.findViewById(R.id.btn_edit)
        btn_edit.setOnClickListener {
            if(mUser==null) return@setOnClickListener
            startActivity(Intent(requireActivity(),ProfileEditActivity::class.java))
        }
    }

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(requireActivity())
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun initView(view: View){
        iv_head=view.findViewById(R.id.iv_head)

        tv_name=view.findViewById(R.id.tv_name)
        tv_email=view.findViewById(R.id.tv_email)
        tv_address=view.findViewById(R.id.tv_address)
        tv_phone=view.findViewById(R.id.tv_phone)
        tv_privacy=view.findViewById(R.id.tv_privacy)
        tv_isOpen=view.findViewById(R.id.tv_isOpen)

        progressBar = view.findViewById(R.id.userProfileProgressBar)
        progressBar.isIndeterminate = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var user = FirebaseAuth.getInstance().currentUser
//        uid = FirebaseAuth.getInstance().uid ?: ""
//        ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addValueEventListener(postListener)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user = dataSnapshot.getValue<User>()
                Log.w(TAG, "user $user")

                mUser = user
                user?.let {  updateView(user)}
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                makeToast(databaseError.message)
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                progressBar.visibility = View.GONE
            }
        }
        profileViewModel.getCurrentUserObject().addValueEventListener(postListener)
        tv_email.text = user?.email ?: ""
    }


    private fun updateView(user: User) {
        Log.d(TAG, "updateUserInfo : $user")
        if (!TextUtils.isEmpty(user.profileImageUrl)) {
            Glide.with(this)
                .load(user.profileImageUrl)
                .fitCenter()
                .into(iv_head)
        }

        tv_name.text = user.username
        tv_phone.text = user.phoneNumber
        tv_address.text = user.address

        if(user.isOpen) tv_isOpen.text = "YES" else tv_isOpen.text = "NO"
    }

}