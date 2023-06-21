package com.example.crud_firebase_kotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class EditProfileActivity : AppCompatActivity() {
    private lateinit var editName: TextInputEditText
    private lateinit var editEmail: TextInputEditText
    private lateinit var editUsername: TextInputEditText
    private lateinit var editPassword: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var reference: DatabaseReference

    lateinit var nameUser: String
    lateinit var emailUser: String
    lateinit var usernameUser: String
    lateinit var passwordUser: String

    private lateinit var pref: preferences//para el shared preferences
    private var favorito = false//para saber si esta en favoritos o no

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        pref = preferences(this@EditProfileActivity)
        reference = FirebaseDatabase.getInstance().getReference("users")

        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)
        saveButton = findViewById(R.id.saveButton)
        val IdUser = reference.push().key!!
        val checkUserDatabase = reference.orderByChild("idusers").equalTo(pref.prefIdUser)
        checkUserDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val DataUserID = dataSnapshot.key!!
                        val value = dataSnapshot.getValue(HelperClass::class.java)

                        nameUser = dataSnapshot.child("name").getValue().toString()
                        emailUser = dataSnapshot.child("email").getValue().toString()
                        usernameUser = dataSnapshot.child("username").getValue().toString()
                        passwordUser = dataSnapshot.child("password").getValue().toString()
                        pref.prefNombreUser = nameUser
                        pref.prefEmailUser = emailUser
                        pref.prefUsername = usernameUser
                        pref.prefPasswordUser = passwordUser
                        if (value?.idusers == pref.prefIdUser) {
                            editName.setText(nameUser)
                            editEmail.setText(emailUser)
                            editUsername.setText(usernameUser)
                            editPassword.setText(passwordUser)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })



        saveButton.setOnClickListener {
            if (isNameChanged() || isPasswordChanged() || isEmailChanged()) {
                Toast.makeText(this@EditProfileActivity, "Saved", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@EditProfileActivity, MainActivity::class.java))
            } else {
                Toast.makeText(this@EditProfileActivity, "No Changes Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isNameChanged(): Boolean {
        if (!nameUser.equals(editName.text.toString())) {
            reference.child(usernameUser).child("name").setValue(editName.text.toString())
            nameUser = editName.text.toString()
            return true
        } else {
            return false
        }
    }

    private fun isEmailChanged(): Boolean {
        if (!emailUser.equals(editEmail.text.toString())) {//si el email no es igual al que esta en la base de datos
            reference.child(usernameUser).child("email").setValue(editEmail.text.toString())
            emailUser = editEmail.text.toString()
            return true
        } else {
            return false
        }
    }

    private fun isPasswordChanged(): Boolean {
        if (!passwordUser.equals(editPassword.text.toString())) {
            reference.child(usernameUser).child("password").setValue(editPassword.text.toString())
            passwordUser = editPassword.text.toString()
            return true
        } else {
            return false
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}