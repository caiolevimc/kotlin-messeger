package com.example.messenger.views

import android.util.Log
import com.example.messenger.R
import com.example.messenger.messages.LatestMessagesActivity
import com.example.messenger.models.ChatMessage
import com.example.messenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>() {
    var chatPartnerUser: User? = null

    override fun getLayout() = R.layout.latest_message_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest_message.text = chatMessage.text

        val chatPartnerId: String
        chatPartnerId = if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatMessage.toId
        } else {
            chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.username_textview_latest_message.text = chatPartnerUser?.username

                //load profile image
                val uri = chatPartnerUser?.profileImage
                val targetImageView = viewHolder.itemView.imageview_latest_message
                Picasso.get().load(uri).into(targetImageView)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(LatestMessagesActivity.TAG, "Finding chatPartner Error: $error")
            }
        })
    }
}