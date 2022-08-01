package com.example.pethaven.ui.features.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.pethaven.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_receiving.view.*
import kotlinx.android.synthetic.main.chat_sending.view.*
import kotlinx.android.synthetic.main.user_chat_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    var toUser : User? = null
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
        }
    }

    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId.toString(), System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
            }

        edittext_chat_log.setText("")
    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId.compareTo(FirebaseAuth.getInstance().uid.toString()) == 0) {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    } else {
                        val currentUser = ChatListActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }
}

class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_receiving.text = text

        val uri = user.profileImageUrl
        val targetImgageView = viewHolder.itemView.imageView_receiving
        Picasso.get().load(uri).into(targetImgageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_receiving
    }
}

class ChatToItem(val text: String, val user : User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_sending.text = text

        val uri = user.profileImageUrl
        val targetImgageView = viewHolder.itemView.imageView_sending
        Picasso.get().load(uri).into(targetImgageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_sending
    }
}