package com.example.reptilehaven.ui.features.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reptilehaven.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        val fab = view.findViewById<FloatingActionButton>(R.id.addReptileFab)
        fab.setOnClickListener{
            val intent = Intent(requireActivity(), AddEditReptileActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}