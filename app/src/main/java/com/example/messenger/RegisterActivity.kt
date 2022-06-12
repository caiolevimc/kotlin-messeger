package com.example.messenger

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var selectedPhotoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //funcao para pegar a imagem
        val getImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                Log.d("Msg RegisterActivity", "Uri: ${it}")
                selectedPhotoUri = it.normalizeScheme()

                select_photo_imageview_register.setImageDrawable(it.toDrawable())
                select_photo_button_register.alpha = 0F

                Log.d("Msg RegisterActivity", "Photo was selected")
            })

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_textview.setOnClickListener{
            Log.d("Msg RegisterActivity", "Going to LoginActivity")
            //going to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        select_photo_button_register.setOnClickListener {
            Log.d("Msg RegisterActivity", "Try to show photo selector")
            getImage.launch("image/*")
        }
    }

    private fun performRegister(){
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        Log.d("Msg RegisterActivity", "Email is: $email")
        Log.d("Msg RegisterActivity", "Password: $password")

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill the email/password.", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                } else {
                    Toast.makeText(this, "Successfully created user", Toast.LENGTH_SHORT).show()
                    Log.d("Msg RegisterActivity", "Successfully created user with uid: ${it.result.user?.uid}")

                    uploadImageToFirebaseStorage()
                }
            }
            .addOnFailureListener{
                Log.d("Msg RegisterActivity", "Failure: ${it.message}")
                Toast.makeText(this, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) {
            Log.d("Msg RegisterActivity", "The uri is null")
            return
        }

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Msg RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl
                    .addOnSuccessListener {
                        Log.d("Msg RegisterActivity", "File Location: $it")

                        saveUserToFirebaseDatabase(it.toString())
                    }
            }
            .addOnFailureListener{
                Log.d("Msg RegisterActivity", "Failed uploading image: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(
            uid,
            username_edittext_register.text.toString(),
            profileImageUrl
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Msg RegisterActivity", "Finally we saved the user to Firebase Database")
            }
            .addOnFailureListener{
                Log.d("Msg RegisterActivivty", "Failed to save user: ${it.message}")
            }
    }

    private fun Uri.toDrawable(): Drawable = Drawable.createFromStream(contentResolver.openInputStream(this), this.toString())
}

class User(val uid: String, val username: String, val profileImage: String)
