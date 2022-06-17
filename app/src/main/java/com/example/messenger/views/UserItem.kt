package com.example.messenger.views

import com.example.messenger.R
import com.example.messenger.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItem(val user: User): Item<GroupieViewHolder>(){

    override fun getLayout() = R.layout.user_row_new_message

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(user.profileImage).into(viewHolder.itemView.imageview_new_message_row)
        viewHolder.itemView.username_textview_new_message.text = user.username
    }

}