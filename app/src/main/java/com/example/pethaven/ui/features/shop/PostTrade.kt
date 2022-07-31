package com.example.pethaven.ui.features.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pethaven.R

class PostTrade : Fragment() {

    companion object {
        fun newInstance() = PostTrade()
    }

    private lateinit var viewModel: PostTradeViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_post_trade, container, false)
    }

}