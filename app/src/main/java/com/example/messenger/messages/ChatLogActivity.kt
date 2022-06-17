package com.example.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.messenger.R
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.example.messenger.views.ChatFromItem
import com.example.messenger.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupieAdapter
import kotlinx.android.synthetic.main.activity_chat_log.*

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
        }

        listenForMessages()
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if(chatMessage.fromId == fromId){
                        val currentUser = LatestMessagesActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                } else {
                    Log.d(TAG, "chatMessage == null")
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onCancelled(error: DatabaseError) {

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

        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push() //push() cria um id automaticamente
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, timeStamp)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Save our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
            }
            .addOnFailureListener{
                Log.d(TAG, "Error sending message: ${it.message}")
            }

        toReference.setValue(chatMessage)
            .addOnFailureListener{
                Log.d(TAG, "toReference Error: ${it.message}")
            }

        val latestMessageReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageReference.setValue(chatMessage)

        val latestMessageToReference = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToReference.setValue(chatMessage)
    }
}