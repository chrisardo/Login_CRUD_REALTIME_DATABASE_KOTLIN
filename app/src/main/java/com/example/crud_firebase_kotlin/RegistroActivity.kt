package com.example.crud_firebase_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class RegistroActivity : AppCompatActivity() {
    lateinit var signupName: TextInputEditText
    lateinit var signupUsername: TextInputEditText
    lateinit var signupEmail: TextInputEditText
    lateinit var signupPassword: TextInputEditText
    lateinit var loginRedirectText: TextView
    lateinit var signupButton: AppCompatButton
    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        signupName = findViewById(R.id.signup_name)
        signupUsername = findViewById(R.id.signup_username)
        signupEmail = findViewById(R.id.emailEt)
        signupPassword = findViewById(R.id.passET)
        loginRedirectText = findViewById(R.id.textView)
        signupButton = findViewById(R.id.button)

        signupButton.setOnClickListener {
            if (validateName() && validateUsername() && validatePassword() && validateCorreo()) {
                val name = signupName.text.toString()
                val email = signupEmail.text.toString()
                val username = signupUsername.text.toString()
                val password = signupPassword.text.toString()

                database = FirebaseDatabase.getInstance()
                reference = database.getReference("users")
                val checkUserDatabase = reference.orderByChild("username").equalTo(username)
                checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            //Existe el username
                            signupUsername.error = "User exist"
                            signupUsername.requestFocus()
                        } else {
                            //No existe el usuario
                            val IdUser = reference.push().key!!
                            val helperClass = HelperClass(IdUser, name, email, username, password, "Usuario")
                            reference.child(username).setValue(helperClass)

                            Toast.makeText(
                                this@RegistroActivity,
                                "You have signed up successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle onCancelled event
                        Toast.makeText(this@RegistroActivity, error.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
        loginRedirectText.setOnClickListener {
            val intent = Intent(this@RegistroActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validatePassword(): Boolean {
        val validarpassword = signupPassword.text.toString().trim()
        return if (validarpassword.isEmpty()) {
            signupPassword.error = "Password cannot be empty"
            false
        } else {
            signupPassword.error = null
            true
        }
    }

    private fun validateUsername(): Boolean {
        val validarusuario = signupUsername.text.toString().trim()
        return if (validarusuario.isEmpty()) {
            signupUsername.error = "Username cannot be empty"
            false
        } else {
            signupUsername.error = null
            true
        }
    }

    private fun validateName(): Boolean {
        val validarusuario = signupName.text.toString().trim()
        return if (validarusuario.isEmpty()) {
            signupName.error = "Username cannot be empty"
            false
        } else {
            signupName.error = null
            true
        }
    }

    private fun validateCorreo(): Boolean {
        val validarusuario = signupEmail.text.toString().trim()
        return if (validarusuario.isEmpty()) {
            signupEmail.error = "Username cannot be empty"
            false
        } else {
            signupEmail.error = null
            true
        }
    }
}