package com.example.messenger.views

import com.example.messenger.R
import com.example.messenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

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