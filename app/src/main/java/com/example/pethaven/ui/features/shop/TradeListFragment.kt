package com.example.pethaven.ui.features.shop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
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
    private lateinit var uid: String

    companion object {
        private const val FILTER_ALL_BUTTON_ID = 1
        private const val FILTER_USER_BUTTON_ID = 2
        private const val FILTER_OTHER_BUTTON_ID = 3
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTradeListBinding.inflate(inflater, container, false)
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        setUpSearchView()
        setUpFilterButtons()

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    ///------------------------ Initializing Views-----------------------///
    private fun setUpFilterButtons() {
        binding.filterAllButton.setOnClickListener { switchFilterType(FILTER_ALL_BUTTON_ID) }
        binding.filterUserButton.setOnClickListener { switchFilterType(FILTER_USER_BUTTON_ID) }
        binding.filterOtherButton.setOnClickListener { switchFilterType(FILTER_OTHER_BUTTON_ID) }
    }

    private fun switchFilterType(filterButtonID: Int) {
        tradeListViewModel.currentFilterButtonID.value = filterButtonID
    }

    private fun setUpSearchView() {
        binding.tradeListSearchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean { return false }

            override fun onQueryTextChange(newText: String?): Boolean {
                when (tradeListViewModel.currentFilterButtonID.value) {
                    FILTER_ALL_BUTTON_ID -> adapter.filter.filter(newText)
                    FILTER_USER_BUTTON_ID -> adapter.getUserFilter(uid).filter(newText)
                    FILTER_OTHER_BUTTON_ID -> adapter.getOtherFilter(uid).filter(newText)
                    else -> adapter.filter.filter(newText)
                }
                return true
            }
        })
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

        tradeListViewModel.currentFilterButtonID.observe(requireActivity()) {
            when (tradeListViewModel.currentFilterButtonID.value) {
                FILTER_ALL_BUTTON_ID -> adapter.filter.filter(binding.tradeListSearchView.query)
                FILTER_USER_BUTTON_ID -> adapter.getUserFilter(uid).filter(binding.tradeListSearchView.query)
                FILTER_OTHER_BUTTON_ID -> adapter.getOtherFilter(uid).filter(binding.tradeListSearchView.query)
                else -> adapter.filter.filter(binding.tradeListSearchView.query)
            }
        }
    }
}