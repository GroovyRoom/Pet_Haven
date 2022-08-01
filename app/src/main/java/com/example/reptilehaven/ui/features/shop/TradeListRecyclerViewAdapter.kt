package com.example.reptilehaven.ui.features.shop

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reptilehaven.databinding.TradeListItemBinding
import com.example.reptilehaven.domain.Post
import com.example.reptilehaven.domain.PostRepository

class TradeListRecyclerViewAdapter: RecyclerView.Adapter<TradeListRecyclerViewAdapter.ViewHolder>() {

    private lateinit var binding: TradeListItemBinding
    private lateinit var listener: Listener
    private lateinit var repository: PostRepository
    private lateinit var testPostArrayList: ArrayList<Post>
    private lateinit var examplePost: Post

    interface Listener {
        fun onClickShowMore(position: Int)
        fun onClickShowLess(position: Int)
        fun onClickContactSeller(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(
//                R.layout.trade_list_item,
//                parent,
//                false
//        )

        binding = TradeListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        val view = binding.root
        testPostArrayList = ArrayList()
        examplePost = Post()

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        binding.tradePostShowLessButton.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return 0
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            binding.tradePostShowMoreButton.setOnClickListener {
                binding.tradePostShowMoreButton.visibility = View.GONE
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.tradePostExpandable.visibility = View.VISIBLE
            }

        }
    }

}