package com.example.pethaven.ui.features.shop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.example.pethaven.R
import com.example.pethaven.domain.Post
import com.example.pethaven.domain.User
import com.example.pethaven.ui.features.chat.ChatFragment.Companion.USER_KEY
import com.example.pethaven.ui.features.chat.ChatLogActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class TradeTestAdapter(var context: Context)
    : RecyclerView.Adapter<TradeTestAdapter.ViewHolder>()
    , Filterable {

    private var postList: ArrayList<Post> = ArrayList()
    private var postListAll = ArrayList<Post>(postList)

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
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.trade_list_item, parent, false)
        return ViewHolder(view, parent)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val post = postList[position]
        viewHolder.bind(post)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class ViewHolder(private val itemView: View, private val parent: ViewGroup)
        : RecyclerView.ViewHolder(itemView) {

        var postNameView: TextInputEditText = itemView.findViewById(R.id.trade_post_reptile_name_edit_text)
        var postPriceView: TextInputEditText = itemView.findViewById(R.id.trade_post_price_edit_text)
        var postDateView: TextInputEditText = itemView.findViewById(R.id.trade_post_date_edit_text)
        var postOwnerView: TextInputEditText = itemView.findViewById(R.id.trade_post_owner_name_edit_text)
        var postDescriptionView: TextInputEditText = itemView.findViewById(R.id.trade_post_description_edit_text)
        var postImageView: ImageView = itemView.findViewById(R.id.editReptileImg)

        var showLessView: LinearLayout = itemView.findViewById(R.id.show_less_view)
        var postExpandable: LinearLayout = itemView.findViewById(R.id.trade_post_expandable)

        var showMoreButton: Button = itemView.findViewById(R.id.trade_post_show_more_button)
        var showLessButton: Button = itemView.findViewById(R.id.trade_post_show_less_button)

        var postUidView: TextView = itemView.findViewById(R.id.trade_post_uid)
        var contactSellerButton1: Button = itemView.findViewById(R.id.trade_post_contact_seller_button)
        var contactSellerButton2: Button = itemView.findViewById(R.id.trade_post_contact_seller_button_2)

        fun bind(post: Post) {
            postNameView.setText(post.reptileName)
            postPriceView.setText(post.price.toString())
            postDateView.setText(post.date)
            postOwnerView.setText(post.ownerName)
            postDescriptionView.setText(post.description)
            postUidView.setText(post.uid)

            val currentUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            if (currentUid == post.uid) {
                contactSellerButton1.isEnabled = false
                contactSellerButton2.isEnabled = false
            } else { // Don't Delete this, this part is needed
                contactSellerButton1.isEnabled = true
                contactSellerButton2.isEnabled = true
            }

            post.let {
                if (it.imgUri != null) {
                    Glide.with(context)
                        .load(it.imgUri)
                        .fitCenter()
                        .into(postImageView)
                }
            }
        }

        init {
            showMoreButton.setOnClickListener {
                showLessView.visibility = View.GONE
                TransitionManager.beginDelayedTransition(parent, AutoTransition())
                postExpandable.visibility = View.VISIBLE
            }

            showLessButton.setOnClickListener {
                TransitionManager.beginDelayedTransition(parent, AutoTransition())
                postExpandable.visibility = View.GONE
                showLessView.visibility = View.VISIBLE
            }

            contactSellerButton1.setOnClickListener {
                val uid = postUidView.text.toString()

                fetchToUser(uid,
                    object : OnGetDataListener {
                        override fun onSuccess(dataSnapshotValue: User?) {
                            val intent = Intent(context, ChatLogActivity::class.java)
                            intent.putExtra(USER_KEY, dataSnapshotValue)
                            context.startActivity(intent)
                        }
                    })
            }

            contactSellerButton2.setOnClickListener {
                val uid = postUidView.text.toString()

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
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return tradeFilter
    }

    fun getUserFilter(uid: String): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredList = ArrayList<Post>()
                if (constraint.toString().isEmpty()) {
                    filteredList.addAll(postListAll.filter { it.uid == uid })
                } else {
                    filteredList.addAll(
                        postListAll.filter {
                            it.description.lowercase().contains(constraint.toString().lowercase())
                                    && it.uid == uid
                        }
                    )
                }
                val result = FilterResults().apply {
                    values = filteredList
                }
                return result
            }

            override fun publishResults(constraint: CharSequence, result: FilterResults) {
                postList.clear()
                postList.addAll(result.values as ArrayList<Post>)
                notifyDataSetChanged()
            }
        }

    }

    fun getOtherFilter(uid: String): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filteredList = ArrayList<Post>()
                if (constraint.toString().isEmpty()) {
                    filteredList.addAll(postListAll.filter { it.uid != uid })
                } else {
                    filteredList.addAll(
                        postListAll.filter {
                            it.description.lowercase().contains(constraint.toString().lowercase())
                                    && it.uid != uid
                        }
                    )

                    println("debug: filtered list size - $filteredList")
                }

                val result = FilterResults().apply {
                    values = filteredList
                }
                return result
            }

            override fun publishResults(constraint: CharSequence, result: FilterResults) {
                postList.clear()
                postList.addAll(result.values as ArrayList<Post>)
                notifyDataSetChanged()
            }
        }
    }
}

