package com.example.messenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.messenger.R
import com.example.messenger.models.User
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        supportActionBar?.title = "Chat Log"

        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = user?.username

        val adapter = GroupieAdapter()

        adapter.add(ChatFromItem())
        adapter.add(ChatToItem())
        adapter.add(ChatToItem())
        adapter.add(ChatFromItem())

        recyclerview_chat_log.adapter = adapter
    }
}

class ChatFromItem : Item<GroupieViewHolder>(){
    override fun getLayout() = R.layout.chat_from_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}

class ChatToItem : Item<GroupieViewHolder>(){
    override fun getLayout() = R.layout.chat_to_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }
}