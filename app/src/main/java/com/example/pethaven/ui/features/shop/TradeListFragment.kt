package com.example.pethaven.ui.features.shop

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.databinding.FragmentTradeListBinding
import com.example.pethaven.domain.PostViewModel


class TradeListFragment : Fragment() {
    private var _binding: FragmentTradeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var tradeListViewModel: PostViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TradeListRecyclerViewAdapter

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTradeListBinding.inflate(inflater, container, false)
        setUpProgressBar()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpProgressBar() {
        binding.tradeListProgressBar.isIndeterminate = true
    }

    ///------------------------ Initializing Recycler View and ViewModel -----------------------///
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TradeListRecyclerViewAdapter()
        recyclerView = binding.tradeListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        tradeListViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // Check if there is a new post added to the list and notify the adapter.
        tradeListViewModel.allPosts.observe(viewLifecycleOwner) {
            binding.tradeListProgressBar.visibility = View.GONE
            adapter.updatePostList(it)
        }
    }
}