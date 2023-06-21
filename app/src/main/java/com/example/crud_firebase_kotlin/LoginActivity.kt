package com.example.crud_firebase_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.crud_firebase_kotlin.databinding.ActivityLoginBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class LoginActivity : AppCompatActivity() {
    private lateinit var loginUsername: TextInputEditText
    private lateinit var loginPassword: TextInputEditText
    private lateinit var loginButton: AppCompatButton
    private lateinit var signupRedirectText: TextView

    private lateinit var pref: preferences//para el shared preferences

    private lateinit var nameFromDB: String
    private lateinit var usernameFromDB: String
    private lateinit var passwordFromDB: String
    private lateinit var emailFromDB: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pref = preferences(this@LoginActivity)

        loginUsername = findViewById(R.id.emailEt)
        loginPassword = findViewById(R.id.passET)
        loginButton = findViewById(R.id.button)
        signupRedirectText = findViewById(R.id.textView)

        loginButton.setOnClickListener {
            if (validateUsername() && validatePassword()) {
                checkUser()
            }
        }

        signupRedirectText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUsername(): Boolean {
        val validarusuario = loginUsername.text.toString().trim()
        return if (validarusuario.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val validarpassword = loginPassword.text.toString().trim()
        return if (validarpassword.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    private fun checkUser() {
        val userUsername = loginUsername.text.toString().trim()
        val userPassword = loginPassword.text.toString().trim()

        val reference = FirebaseDatabase.getInstance().getReference("users")
        val checkUserDatabase = reference.orderByChild("username").equalTo(userUsername)
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val user = item.getValue<HelperClass>()
                        loginUsername.error = null
                        passwordFromDB = snapshot.child(userUsername).child("password").getValue(String::class.java) as String
                        if (passwordFromDB == userPassword) {
                            val IdUser = reference.push().key!!
                            loginUsername.error = null
                            pref.prefStatus = true
                            pref.prefIdUser = user!!.idusers
                            pref.prefNombreUser = user!!.name
                            pref.prefEmailUser = user!!.email
                            pref.prefUsername = user!!.username
                            pref.prefPasswordUser = user!!.password
                            nameFromDB = snapshot.child(userUsername).child("name").getValue(String::class.java) as String
                            /*emailFromDB = snapshot.child(userUsername).child("email").getValue(String::class.java) as String
                            usernameFromDB = snapshot.child(userUsername).child("username").getValue(String::class.java) as String
                            */
                            if (nameFromDB == "Administrador") {
                                Toast.makeText(this@LoginActivity, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
                            } else {
                                var intent: Intent? = null
                                intent = Intent(this@LoginActivity, MainActivity::class.java)

                                startActivity(intent)
                                finish()
                            }

                        } else {
                            loginPassword.error = "Invalid Credentials"
                            loginPassword.requestFocus()
                        }
                    }
                } else {
                    loginUsername.error = "User does not exist"
                    loginUsername.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
                Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
    override fun onStart() {
        super.onStart()
        if (pref.prefStatus) {

            if (pref.prefLevel == "Administrador") {
                //Intent(this, AdminActivity::class.java)
                Toast.makeText(this@LoginActivity, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
            } else {
                var intent: Intent? = null
                intent = Intent(this, MainActivity::class.java)
                /*if (intent.hasExtra("name")) {
                    val Nameuser = intent.getStringExtra("name")
                    intent.putExtra("name", nameFromDB) // Enviar nuevamente el parámetro
                }*/
                startActivity(intent)
                finish()
            }

        }
    }
}