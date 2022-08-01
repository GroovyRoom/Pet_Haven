package com.example.pethaven.ui.features.shop

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.databinding.TradeListItemBinding
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.PostRepository
import com.google.android.material.card.MaterialCardView

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
        val view2 = LayoutInflater.from(parent.context).inflate(
                R.layout.trade_list_item,
                parent,
                false
        )

        binding = TradeListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        val view = binding.root
        testPostArrayList = ArrayList()
        examplePost = Post()

        return ViewHolder(view2)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 5
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            val showLess = view.findViewById<Button>(R.id.trade_post_show_less_button)
            val showMore = view.findViewById<Button>(R.id.trade_post_show_more_button)
            val showLessView = view.findViewById<LinearLayout>(R.id.show_less_view)
            val showMoreView = view.findViewById<LinearLayout>(R.id.trade_post_expandable)
            val cardView = view.findViewById<MaterialCardView>(R.id.trade_list_item_card)

            showMore.setOnClickListener {
                showLessView.visibility = View.GONE
                TransitionManager.beginDelayedTransition(cardView, AutoTransition())
                showMoreView.visibility = View.VISIBLE

            }
            showLess.setOnClickListener {
                showLessView.visibility = View.VISIBLE
                showMoreView.visibility = View.GONE

            }
//            binding.tradePostShowMoreButton.setOnClickListener {
//                binding.showLessView.visibility = View.GONE
//                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
//                binding.tradePostExpandable.visibility = View.VISIBLE
//            }
//            binding.tradePostShowLessButton.setOnClickListener {
//                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
//                binding.tradePostExpandable.visibility = View.GONE
//                binding.showLessView.visibility = View.VISIBLE
//            }

        }
    }

}