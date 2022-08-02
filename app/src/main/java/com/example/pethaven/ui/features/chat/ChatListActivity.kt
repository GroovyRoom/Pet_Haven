package com.example.pethaven.ui.features.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.pethaven.R
import com.example.pethaven.ui.LoginActivity
import com.example.pethaven.ui.features.chat.NewChatActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_list.*
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class ChatListActivity  : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "ChatListActivity"
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        recyclerview_latest_messages.adapter = adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,
            DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener {item, view ->
            Log.d(TAG, "Testing Click on Message")
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LatestMessageRow
            row.chatPartnerUser
            intent.putExtra(NewChatActivity.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenForLatestMessages()

        fetchCurrentUser()

        checkUserLoggedIn()
    }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages() {
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, previousChildName: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onChildMoved(p0: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>() {
        var chatPartnerUser: User? = null

        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

            val chatPartnerId: String
            if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                chatPartnerId = chatMessage.toId
            } else {
                chatPartnerId = chatMessage.fromId
            }
            val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    chatPartnerUser = p0.getValue(User::class.java)
                    viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username

                    val targetImageView = viewHolder.itemView.imageview_latest_message
                    Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })

            viewHolder.itemView.username_textview_latest_message.text = "theName"
        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("ChatListActicity: ", "Current user ${currentUser?.username}")
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    // Please work
    private fun checkUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        // user is not logged-in then send user to login page.
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewChatActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

}
