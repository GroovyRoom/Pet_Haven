package com.example.pethaven.ui.features.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pethaven.databinding.FragmentTradePostBinding
import com.google.firebase.database.FirebaseDatabase

class TradePostFragment : Fragment() {

    private var _binding: FragmentTradePostBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: TradePostViewModel
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTradePostBinding.inflate(inflater, container, false)
        val view = binding.root
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}