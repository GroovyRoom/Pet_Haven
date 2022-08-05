package com.example.pethaven.ui.features.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.databinding.FragmentTradeListBinding
import com.example.pethaven.domain.PostViewModel
import com.google.firebase.auth.FirebaseAuth


class TradeListFragment : Fragment() {
    private var _binding: FragmentTradeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var tradeListViewModel: PostViewModel
    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: TradeListRecyclerViewAdapter
    private lateinit var adapter: TradeTestAdapter

    private var isUserButtonChecked: Boolean = false
    private var isOtherButtonChecked: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTradeListBinding.inflate(inflater, container, false)
        setUpSearchView()
        setUpFilterButtons()
        return binding.root
    }

    private fun setUpFilterButtons() {
        binding.filterUserButton.setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            isUserButtonChecked = true
            adapter.getUserFilter(uid).filter(binding.tradeListSearchView.query)
        }

        binding.filterOtherButton.setOnClickListener {
            isOtherButtonChecked = true
        }
    }

    private fun setUpSearchView() {
        binding.tradeListSearchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    ///------------------------ Initializing Recycler View and ViewModel -----------------------///
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
            Dense: Switch the adapter here
         */
        //adapter = TradeListRecyclerViewAdapter(ArrayList())
        adapter = TradeTestAdapter(requireActivity())
        recyclerView = binding.tradeListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        tradeListViewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // Check if there is a new post added to the list and notify the adapter.
        tradeListViewModel.allPosts.observe(viewLifecycleOwner) {
            binding.tradeListProgressBar.visibility = View.GONE
            adapter.updatePostList(it)
            adapter.filter.filter(binding.tradeListSearchView.query)
        }
    }
}