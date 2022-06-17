package com.example.messenger.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.example.messenger.models.User
import com.example.messenger.views.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    companion object{
        val TAG = "Msg NewMessageActivity"
        val USER_KEY = "USER_KEY"
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
                        if(user.uid != FirebaseAuth.getInstance().uid){
                            adapter.add(UserItem(user))
                        }
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "DatabaseError: $error")
            }
        })
    }
}
