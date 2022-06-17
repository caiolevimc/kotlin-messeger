package com.example.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.messenger.R
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "Msg ChatLogActivity"
    }

    val adapter = GroupieAdapter()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        if(toUser == null){
            supportActionBar?.title = "Chat Log"
        } else {
            supportActionBar?.title = toUser!!.username
        }

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage()
            edittext_chat_log.setText("")
        }

        listenForMessages()
    }

    private fun listenForMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if(
                        chatMessage.fromId == FirebaseAuth.getInstance().uid &&
                        chatMessage.toId == toUser!!.uid
                    ){
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))

                    } else if (
                        chatMessage.fromId == toUser!!.uid &&
                        chatMessage.toId == FirebaseAuth.getInstance().uid
                    ){
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

                } else {
                    Log.d(TAG, "chatMessage == null")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Error: ${error.toException().message}")
            }
        })
    }

    private fun performSendMessage() {
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid
        val timeStamp = System.currentTimeMillis() / 1000

        if (fromId == null) return;
        if (toId == null) return;

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push() //push() cria um id automaticamente

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, timeStamp)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Save our chat message: ${reference.key}")
            }
            .addOnFailureListener{
                Log.d(TAG, "Error sending message: ${it.message}")
            }
    }
}

class ChatFromItem(val text : String, val user: User) : Item<GroupieViewHolder>(){
    override fun getLayout() = R.layout.chat_from_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        //load profile image
        val uri = user.profileImage
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }
}

class ChatToItem(val text : String, val user: User) : Item<GroupieViewHolder>(){
    override fun getLayout() = R.layout.chat_to_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text
        //load profile image
        val uri = user.profileImage
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }
}