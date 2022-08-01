package com.example.pethaven.ui.features.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pethaven.databinding.FragmentPostTradeBinding
import com.google.firebase.database.FirebaseDatabase

class PostTradeFragment : Fragment() {

    private var _binding: FragmentPostTradeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostTradeViewModel
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostTradeBinding.inflate(inflater, container, false)
        val view = binding.root
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}