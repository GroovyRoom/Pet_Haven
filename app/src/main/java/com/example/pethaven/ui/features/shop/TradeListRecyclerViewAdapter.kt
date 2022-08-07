package com.example.pethaven.ui.features.shop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.pethaven.databinding.TradeListItemBinding
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.User
import com.example.pethaven.ui.features.chat.ChatFragment.Companion.USER_KEY
import com.example.pethaven.ui.features.chat.ChatLogActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TradeListRecyclerViewAdapter(var postList: ArrayList<Post>)
    : RecyclerView.Adapter<TradeListRecyclerViewAdapter.ViewHolder>()
    , Filterable {

    private lateinit var binding: TradeListItemBinding
    private lateinit var listener: Listener
//    private var postList: ArrayList<Post> = ArrayList()
    private var postListAll = ArrayList<Post>(postList)

    private lateinit var context: Context

    interface Listener {
        fun onClickContactSeller(position: Int)
    }

    private var tradeFilter = object : Filter() {
        // Run in Background Thread
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<Post>()
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(postListAll)
            } else {
                for (post in postListAll) {
                    if (post.description.lowercase().contains(constraint.toString().lowercase())) {
                        filteredList.add(post)
                    }
                }
            }
            val result = FilterResults().apply {
                values = filteredList
            }
            return result
        }

        //Runs on UI Thread
        override fun publishResults(constraint: CharSequence, result: FilterResults)  {
            postList.clear()
            postList.addAll(result.values as ArrayList<Post>)
            for (post in postList) {
                println("debug: post price: ${post.price}")
                println("debug: post desc: ${post.description}")
            }
            notifyDataSetChanged()
        }
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
            println("debug: OnBind description: ${post.description}")
            println("debug: UID: ${post.uid}")
            binding.tradePostPriceEditText.setText(post.price.toString())
            binding.tradePostDateEditText.setText(post.date)
            binding.tradePostDescriptionEditText.setText(post.description)
            binding.tradePostUid.setText(post.uid)
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            if (currentUid.compareTo(binding.tradePostUid.text.toString()) == 0) {
                binding.tradePostContactSellerButton.setEnabled(false)
                binding.tradePostContactSellerButton2.setEnabled(false)
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

            binding.tradePostContactSellerButton.setOnClickListener {
                val uid = binding.tradePostUid.text.toString()

                fetchToUser(uid,
                    object : OnGetDataListener {
                        override fun onSuccess(dataSnapshotValue: User?) {
                            val intent = Intent(context, ChatLogActivity::class.java)
                            intent.putExtra(USER_KEY, dataSnapshotValue)
                            context.startActivity(intent)
                        }
                    })
            }

            binding.tradePostContactSellerButton2.setOnClickListener {
                val uid = binding.tradePostUid.text.toString()

                fetchToUser(uid,
                    object : OnGetDataListener {
                        override fun onSuccess(dataSnapshotValue: User?) {
                            val intent = Intent(context, ChatLogActivity::class.java)
                            intent.putExtra(USER_KEY, dataSnapshotValue)
                            context.startActivity(intent)
                        }
                    })
            }
        }
    }

    interface OnGetDataListener {
        //this is for callbacks
        fun onSuccess(dataSnapshotValue: User?)
    }

    private fun fetchToUser(uid: kotlin.String, listener: OnGetDataListener) {
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                listener.onSuccess(p0.getValue(User::class.java));
            }
            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    fun updatePostList(postList: List<Post>) {
        this.postList.clear()
        this.postList.addAll(postList)

        this.postListAll.clear()
        this.postListAll.addAll(postList)
        println("debug: Updating postList - size: ${postList.size}")
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return tradeFilter
    }
}

