package com.example.crud_firebase_kotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class UploadActivity : AppCompatActivity() {
    lateinit var uploadImage: ImageView
    lateinit var saveButton: Button
    lateinit var uploadTopic: EditText
    lateinit var uploadDesc: EditText
    lateinit var uploadLang: EditText

    private lateinit var imageUri: Uri
    private lateinit var databaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var pref:preferences//para el shared preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        pref = preferences(this@UploadActivity)
        uploadImage = findViewById(R.id.uploadImage)
        uploadDesc = findViewById(R.id.uploadDesc)
        uploadTopic = findViewById(R.id.uploadTopic)
        uploadLang = findViewById(R.id.uploadLang)
        saveButton = findViewById(R.id.saveButton)

        //databaseRef = FirebaseDatabase.getInstance().reference.child("data")
        databaseRef = FirebaseDatabase.getInstance().getReference("data")

        storageRef = FirebaseStorage.getInstance().reference.child("images")

        uploadImage.setOnClickListener {
            openImagePicker()
        }

        saveButton.setOnClickListener {
            uploadData()
        }
    }
    private fun openImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadImage.setImageURI(imageUri)
        }
    }
    private fun uploadData() {
        val textData = uploadTopic.text.toString()

        val imageRef = storageRef.child("${System.currentTimeMillis()}_image.jpg")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                val empId = databaseRef.push().key!!
                val data = Data(empId, textData, downloadUri,  pref.prefIdUser)
                databaseRef.child(empId).setValue(data)//.push()
                    .addOnSuccessListener {
                        showToast("Datos cargados exitosamente")
                    }
                    .addOnFailureListener { e ->
                        showToast("Error al cargar los datos: ${e.message}")
                    }
            } else {
                showToast("Error al cargar la imagen: ${task.exception?.message}")
            }
        }

    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
