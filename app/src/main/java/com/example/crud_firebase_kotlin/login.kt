package com.example.crud_firebase_kotlin

class login {
    /*

        val bundle = intent.extras

        if (bundle != null) {
            Nameuser = bundle.getString("name")
            userEmail = bundle.getString("email")
            userName = bundle.getString("username")
            passworduser = bundle.getString("password")
        }
        if (nameUser == null) {
            // Lógica para manejar el caso en el que no se recibe "name"
        }

    ------------------------LoginActivty------------------------------
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.textView.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


     */
    /* ------------------------RegistroActivity------------------------------
    private lateinit var binding: ActivityRegistroBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }

     */
    /* ------------------------DetallesActivity------------------------------
    class DetallesActivity : AppCompatActivity() {
    //private var uploadImage: ImageView? = null

    //private lateinit var tvEmpId: TextView
    private lateinit var detailImage: ImageView
    private lateinit var tvName: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri
    private var key = ""
    private var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        detailImage = findViewById(R.id.detailImage)
        tvName = findViewById(R.id.tvName) as TextView
        // tvName.text = intent.getStringExtra("empName")
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
                intent.getStringExtra("empName").toString()
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

        ) {
        val mDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.item_data, null)

        mDialog.setView(mDialogView)
        val etEmpName = mDialogView.findViewById<EditText>(R.id.edNombre)

        val uploadImage = mDialogView.findViewById<ImageView>(R.id.uploadImage)
        val btnUpdateData = mDialogView.findViewById<Button>(R.id.btnUpdateData)

        // tvEmpId.text = intent.getStringExtra("empId")

        etEmpName.setText(intent.getStringExtra("empName").toString())
        mDialog.setTitle("Updating $empName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("data").child(empId)
            val empInfo = Data(empId, etEmpName.text.toString())
            dbRef.setValue(empInfo)
            tvName.text = etEmpName.text.toString()
            alertDialog.dismiss()

        }
        uploadImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    private fun saveData() {
        storageReference = FirebaseStorage.getInstance().getReference().child("images")
            .child(imageUri.lastPathSegment!!)
        val builder = AlertDialog.Builder(this@DetallesActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()
        storageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageUrl = urlImage.toString()
            updateData()
            dialog.dismiss()
        }.addOnFailureListener { e ->
            dialog.dismiss()
        }
    }
    private fun updateData() {

        val dataClass = Data(key, tvName.text.toString(), imageUrl)
        databaseReference.setValue(dataClass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                reference.delete()
                Toast.makeText(this@DetallesActivity, "Updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this@DetallesActivity, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}

     */
     */
}