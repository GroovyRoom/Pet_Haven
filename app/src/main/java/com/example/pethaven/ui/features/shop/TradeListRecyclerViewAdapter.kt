package com.example.pethaven.ui.features.shop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
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
            binding.tradePostOwnerNameEditText.setText(post.ownerName)
            binding.tradePostDescriptionEditText.setText(post.description)
            binding.tradePostUid.setText(post.uid)
            val currentUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            if (currentUid.compareTo(binding.tradePostUid.text.toString()) == 0) {
                binding.tradePostContactSellerButton.setEnabled(false)
            }
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
        }
    }

    interface OnGetDataListener {
        //this is for callbacks
        fun onSuccess(dataSnapshotValue: User?)
    }

    private fun fetchToUser(uid: kotlin.String, listener: OnGetDataListener) {
        val uid = binding.tradePostUid.text
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
        notifyDataSetChanged()
    }
}

