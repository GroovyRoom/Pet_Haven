package com.example.pethaven.ui.features.shop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.example.pethaven.databinding.TradeListItemBinding
import com.example.pethaven.domain.Post

class TradeListRecyclerViewAdapter: RecyclerView.Adapter<TradeListRecyclerViewAdapter.ViewHolder>() {

    private lateinit var binding: TradeListItemBinding
    private lateinit var listener: Listener
    private var postList: ArrayList<Post> = ArrayList()
    private lateinit var context: Context

    interface Listener {
        fun onClickContactSeller(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = TradeListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val post = postList[position]
        viewHolder.bind(post)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(binding: TradeListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tradePostReptileNameEditText.setText(post.reptileName)
            binding.tradePostPriceEditText.setText(post.price.toString())
            binding.tradePostDateEditText.setText(post.date)
            post.let {
                if (it.imgUri != null) {
                    Glide.with(context)
                        .load(it.imgUri)
                        .fitCenter()
                        .into(binding.editReptileImg)
                }
            }
        }

        init {
            binding.tradePostShowMoreButton.setOnClickListener {
                binding.showLessView.visibility = View.GONE
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.tradePostExpandable.visibility = View.VISIBLE
            }

            binding.tradePostShowLessButton.setOnClickListener {
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                binding.tradePostExpandable.visibility = View.GONE
                binding.showLessView.visibility = View.VISIBLE

            }
        }
    }

    fun updatePostList(postList: List<Post>) {
        this.postList.clear()
        this.postList.addAll(postList)
        notifyDataSetChanged()
    }
}

