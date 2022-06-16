package com.example.messenger.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.R
import com.example.messenger.messages.LatestMessagesActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    companion object {
        const val TAG = "Msg LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            perfomLogin()
        }

        dont_have_account_textview.setOnClickListener {
            Log.d(TAG, "Going to MainActivity")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun perfomLogin(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        Log.d(TAG, "Email is: $email")
        Log.d(TAG, "Password: $password")

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill the email/password.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                } else {
                    Toast.makeText(this, "Successfully Sing in", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Successfully Sing In: ${it.result.user?.uid}")

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "Failure: ${it.message}")
                Toast.makeText(this, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

}