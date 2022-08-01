package com.example.reptilehaven.ui.features.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.reptilehaven.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_receiving.view.*
import kotlinx.android.synthetic.main.chat_sending.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<User>(NewChatActivity.USER_KEY)

        if (user != null) {
            supportActionBar?.title = user.username
        }

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Debug Chat Log: ")
            performSendMessage()
        }

        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem("From Message"))
        adapter.add(ChatToItem("To Message"))

        recyclerview_chat_log.adapter = adapter
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
    }
}

class ChatFromItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_receiving.text = "From Message..."

    }

    override fun getLayout(): Int {
        return R.layout.chat_receiving
    }
}

class ChatToItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_sending.text = "Longer Message ..."
    }

    override fun getLayout(): Int {
        return R.layout.chat_sending
    }
}