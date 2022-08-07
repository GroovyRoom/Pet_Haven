package com.example.pethaven.ui.features.profile

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.pethaven.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.pethaven.R
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.firebase.database.ktx.getValue

/**
 * Fragment revealing User settings and Profile Information
 */
class ProfileFragment : Fragment() {
    val TAG = "jcy-Profile"
    private var mUser: User? = null

    private lateinit var userImageView:ImageView

    private lateinit var nameTextView:TextView
    private lateinit var addressTextView:TextView
    private lateinit var phoneTextView:TextView
    private lateinit var emailTextView:TextView
    private lateinit var privacyTextView:TextView
    private lateinit var isOpenTextView:TextView

    private lateinit var editBtn:Button
    private lateinit var progressBar: ProgressBar

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        setUpUserInfoView(view)
        setUpButton(view)
        setUpProgressBar(view)

        setUpViewModel()

        return view
    }

    private fun setUpUserInfoView(view: View) {
        userImageView=view.findViewById(R.id.iv_head)

        nameTextView=view.findViewById(R.id.tv_name)
        emailTextView=view.findViewById(R.id.tv_email)
        addressTextView=view.findViewById(R.id.tv_address)
        phoneTextView=view.findViewById(R.id.tv_phone)
        privacyTextView=view.findViewById(R.id.tv_privacy)
        isOpenTextView=view.findViewById(R.id.tv_isOpen)
    }

    private fun setUpButton(view: View) {
        editBtn=view.findViewById(R.id.btn_edit)
        editBtn.setOnClickListener {
            if(mUser==null) return@setOnClickListener
            startActivity(Intent(requireActivity(),ProfileEditActivity::class.java))
        }
    }

    private fun setUpViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(requireActivity())
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun setUpProgressBar(view: View){
        progressBar = view.findViewById(R.id.userProfileProgressBar)
        progressBar.isIndeterminate = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        emailTextView.text = currentUser?.email ?: ""

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user = dataSnapshot.getValue<User>()
                Log.w(TAG, "user $user")

                mUser = user
                user?.let {  updateView(user) }
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
    }


    private fun updateView(user: User) {
        Log.d(TAG, "updateUserInfo : $user")
        if (!TextUtils.isEmpty(user.profileImageUrl)) {
            Glide.with(this)
                .load(user.profileImageUrl)
                .fitCenter()
                .into(userImageView)
        }

        nameTextView.text = user.username
        phoneTextView.text = user.phoneNumber
        addressTextView.text = user.address

        if(user.isOpen) isOpenTextView.text = "YES" else isOpenTextView.text = "NO"
    }

}