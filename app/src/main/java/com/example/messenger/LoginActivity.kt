package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            perfomLogin()
        }

        dont_have_account_textview.setOnClickListener {
            Log.d("LoginActivity", "Going to MainActivity")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun perfomLogin(){
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        Log.d("LoginActivity", "Email is: $email")
        Log.d("LoginActivity", "Password: $password")

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill the email/password.", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                } else {
                    successCreateUser(it)
                }
            }
            .addOnFailureListener{
                failedCreateUser(it)
            }
    }

    private fun successCreateUser(it : Task<AuthResult>){
        Toast.makeText(this, "Successfully Sing in", Toast.LENGTH_SHORT).show()
        Log.d("LoginActivity", "Successfully Sing In: ${it.result.user?.uid}")
    }

    private fun failedCreateUser(it: Exception){
        Log.d("LoginActivity", "Failure: ${it.message}")
        Toast.makeText(this, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
    }
}