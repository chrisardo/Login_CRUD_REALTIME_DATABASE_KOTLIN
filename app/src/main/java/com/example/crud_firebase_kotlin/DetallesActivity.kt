package com.example.crud_firebase_kotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class DetallesActivity : AppCompatActivity() {
    private lateinit var etEmpName: TextInputEditText
    private lateinit var detailImage: ImageView
    private lateinit var tvName: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private var key = ""
    private var imageUrl = ""
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        detailImage = findViewById(R.id.detailImage)
        tvName = findViewById(R.id.tvName) as TextView
        val bundle = intent.extras
        if (bundle != null) {
            tvName.text = bundle.getString("empName")
            key = bundle.getString("empId") ?: ""
            imageUrl = bundle.getString("Image") ?: ""

            Glide.with(this).load(bundle.getString("Image")).into(detailImage)
        }
        btnUpdate = findViewById(R.id.actualizarButton) as Button
        btnUpdate.setOnClickListener {
            openUpdateDialog(
                intent.getStringExtra("empId").toString(),
                intent.getStringExtra("empName").toString(),
                imageUrl
            )
        }
        btnDelete = findViewById(R.id.deleteButton) as Button
        btnDelete.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("data").child(key)
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.getReferenceFromUrl(imageUrl)
            storageReference.delete().addOnSuccessListener {
                // File deleted successfully
                dbRef.removeValue()
                Toast.makeText(this@DetallesActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }.addOnFailureListener { error ->
                Toast.makeText(this, "Deleting Err ${error.message}", Toast.LENGTH_LONG).show()
                // Uh-oh, an error occurred!
            }

        }
    }

    private fun openUpdateDialog(
        empId: String,
        empName: String,
        imageUrl: String,
    ) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.item_data, null)

        mDialog.setView(mDialogView)
        etEmpName = mDialogView.findViewById<TextInputEditText>(R.id.edNombre)

        val uploadImage = mDialogView.findViewById<ImageView>(R.id.uploadImage)
        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        // tvEmpId.text = intent.getStringExtra("empId")

        etEmpName.setText(intent.getStringExtra("empName").toString())
        Glide.with(this).load(imageUrl).into(uploadImage)
        mDialog.setTitle("Editar: $empName")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            val newName = etEmpName.text.toString()
            if (validarTexto(newName)) {
                val dbRef = FirebaseDatabase.getInstance().getReference("data").child(empId)
                //val empInfo = Data(empId, newName, imageUrl)
                dbRef.child("text").setValue(newName)
                dbRef.child("imageUrl").setValue(imageUrl)
                //dbRef.setValue(empInfo)
                tvName.text = newName
                // Guardar imagen en Firebase Storage solo si imageUri está inicializada
                if (::imageUri.isInitialized) {
                    // Guardar imagen en Firebase Storage
                    if (imageUri != null) {
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference
                        val imageRef = storageRef.child("images/$empId.jpg")
                        val uploadTask = imageRef.putFile(imageUri)

                        uploadTask.continueWithTask { task ->
                            if (!task.isSuccessful) {
                                throw task.exception!!
                            }
                            // Continuar con la tarea para obtener la URL de descarga de la imagen
                            imageRef.downloadUrl
                        }.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val downloadUri = task.result
                                if (downloadUri != null) {
                                    val imageUrl = downloadUri.toString()
                                    dbRef.child("imageUrl").setValue(imageUrl)
                                }
                            } else {
                                // Error al obtener la URL de descarga de la imagen
                                Toast.makeText(this@DetallesActivity, "Error al guardar la imagen", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

            } else {
                etEmpName.error = "Username cannot be empty"
            }
            alertDialog.dismiss()

        }
        uploadImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

    }

    private fun validarTexto(empName: String): Boolean {
        //val validarusuario = empName.text.toString().trim()
        val validarTexto = empName.trim()
        return if (validarTexto.isEmpty()) {
            etEmpName.error = "Username cannot be empty"
            false
        } else {
            etEmpName.error = null
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                imageUri = selectedImageUri
                openUpdateDialog(
                    intent.getStringExtra("empId").toString(),
                    intent.getStringExtra("empName").toString(),
                    selectedImageUri.toString()
                )
            }
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