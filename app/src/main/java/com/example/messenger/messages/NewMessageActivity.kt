package com.example.messenger.messages

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.example.messenger.models.User
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    companion object{
        val TAG = "Msg NewMessageActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        fetchUsers()
    }

    private fun fetchUsers() {
        val adapter = GroupAdapter<GroupieViewHolder>()

        val ref = FirebaseDatabase.getInstance().getReference("/users/")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach{
                    Log.d(TAG, it.toString())
                    val user : User? = it.getValue(User::class.java)

                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "DatabaseError: $error")
            }
        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>(){

    override fun getLayout() = R.layout.user_row_new_message

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Picasso.get().load(user.profileImage).into(viewHolder.itemView.imageview_new_message_row)
        viewHolder.itemView.username_textview_new_message.text = user.username
    }

}