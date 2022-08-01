package com.example.pethaven.ui.features.shop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.databinding.TradeListItemBinding
import com.example.pethaven.domain.PostRepository
import kotlinx.android.synthetic.main.activity_main.view.*

class MyTradePostListRecyclerViewAdapter:
        RecyclerView.Adapter<MyTradePostListRecyclerViewAdapter.ViewHolder>() {

    private lateinit var binding: TradeListItemBinding
    private lateinit var listener: Listener
    private lateinit var repository: PostRepository

    interface Listener {
        fun onClickShowMore(position: Int)
        fun onClickShowLess(position: Int)
        fun onClickContactSeller(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = TradeListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent.container,
                false
        )
        val view = binding.root
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onClickShowMore(position)
            }
        }

        override fun
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    fun setExercises(exerciseList: List<Exercise>) {
        this.exerciseList = exerciseList
        notifyDataSetChanged()
    }

}