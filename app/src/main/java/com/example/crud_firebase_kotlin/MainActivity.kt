package com.example.crud_firebase_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var tvUserName: TextView
    lateinit var fab: FloatingActionButton

    private lateinit var empRecyclerView: RecyclerView
    private lateinit var tvLoadingData: TextView
    private lateinit var empList: ArrayList<Data>
    private lateinit var dbRef: DatabaseReference
    lateinit var mAdapter: MyAdapter

    private var favorito = false//para saber si esta en favoritos o no

    private lateinit var mSpiner: Spinner

    private lateinit var pref: preferences//para el shared preferences
    var userId: String? = null

    var hasSelectedItem = false // Variable para verificar si se encontró un elemento seleccionado
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pref = preferences(this@MainActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val upArrow = ContextCompat.getDrawable(this, R.drawable.ic_favorite)
        upArrow?.setBounds(
            0,
            0,
            resources.getDimensionPixelSize(R.dimen.icon_size),
            resources.getDimensionPixelSize(R.dimen.icon_size)
        )
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        progressBar = findViewById(R.id.progressBar)
        mSpiner = findViewById(R.id.idspiner)

        tvUserName = findViewById(R.id.tvUserName)
        tvUserName.text = pref.prefNombreUser
        userId = pref.prefIdUser
        fab = findViewById(R.id.fab);

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }

        empRecyclerView = findViewById(R.id.idRv)
        empRecyclerView.layoutManager = LinearLayoutManager(this)
        empRecyclerView.setHasFixedSize(true)
        tvLoadingData = findViewById(R.id.tvLoadingData)

        empList = arrayListOf<Data>()

        getEmployeesData(userId.toString())

        cargarSpinner()
    }

    private fun cargarSpinner() {
        dbRef = FirebaseDatabase.getInstance().getReference("data")

        // Escuchar los datos de Firebase y configurar el adaptador del Spinner
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val spinnerData: MutableList<String> = mutableListOf("Seleccionar opción")
                val spinnerIds: MutableList<String> = mutableListOf() // Lista para almacenar los IDs correspondientes

                // Iterar a través de los datos obtenidos del dataSnapshot
                for (snapshot in dataSnapshot.children) {
                    val value = snapshot.getValue(Data::class.java)
                    if (value?.idusers == userId) {
                        value?.text?.let {
                            spinnerData.add(it)
                            spinnerIds.add(snapshot.key!!) // Agregar el ID del elemento a la lista
                        }
                    }
                }

                // Configurar el adaptador del Spinner
                val adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_spinner_item, spinnerData)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Asignar el adaptador al Spinner
                mSpiner.adapter = adapter
                // Agregar el listener para capturar la selección del Spinner
                mSpiner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val selectedData = spinnerData[position] // Obtener el elemento seleccionado del Spinner
                        if (selectedData == "Seleccionar opción") {
                            // Se seleccionó la opción "Seleccionar opción"
                            // Realiza las acciones necesarias (por ejemplo, limpiar el RecyclerView)
                        } else {
                            // Se seleccionó un elemento válido
                            val selectedItemId =
                                spinnerIds[position - 1] // Restar 1 para obtener el ID correspondiente al elemento seleccionado
                            getEmployeesData(selectedItemId) // Llamar a getEmployeesData() con el ID seleccionado
                            Toast.makeText(
                                this@MainActivity,
                                "ID: $selectedItemId" + " Texto: ${selectedData.toString()}",
                                Toast.LENGTH_SHORT
                            ).show() // Mostrar el ID en un Toast
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No se seleccionó ningún elemento
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores, si es necesario
            }
        })
    }


    private fun getEmployeesData(selectedId: String?) {
        empRecyclerView.visibility = View.GONE
        tvLoadingData.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        /*val swipeGesture = SwipeGesture(this@MainActivity.taskId){
            override fun
        }*/
        val swipeGesture = object : SwipeGesture(this.taskId) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        Toast.makeText(this@MainActivity, "Elemento eliminado", Toast.LENGTH_SHORT).show()
                    }

                    ItemTouchHelper.RIGHT -> {
                        val position = viewHolder.adapterPosition
                        val selectedItem = empList[position]
                        val selectedItemId = selectedItem.empId
                        val selectedUserId = selectedItem.idusers
                        val selectedText = selectedItem.text
                        val selectedImage = selectedItem.imageUrl
                        val selectedData = Data(
                            selectedItemId,
                            selectedUserId,
                            selectedText,
                            selectedImage
                        )
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("Eliminar")
                        builder.setMessage("¿Estás seguro de eliminar este elemento?")
                        builder.setPositiveButton("Sí") { dialog, which ->
                            dbRef.child(selectedItemId!!).removeValue()
                            mAdapter.notifyItemRemoved(position)
                            empList.removeAt(position)
                            Toast.makeText(this@MainActivity, "Elemento eliminado", Toast.LENGTH_SHORT).show()
                        }
                        builder.setNegativeButton("No") { dialog, which ->
                            mAdapter.notifyItemChanged(position)
                        }
                        builder.show()
                    }
                }
                super.onSwiped(viewHolder, direction)

            }
        }
        itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(empRecyclerView)

        dbRef = FirebaseDatabase.getInstance().getReference("data")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                empList.clear() // Limpiar la lista antes de agregar elementos

                if (snapshot.exists()) {

                    for (empSnap in snapshot.children) {
                        val DataID = empSnap.key
                        val empData = empSnap.getValue(Data::class.java)

                        if (empData?.idusers == userId) {
                            if (empData?.empId == selectedId) {
                                empList.add(empData!!)
                                hasSelectedItem = true
                            } else if (!hasSelectedItem && (selectedId == null || selectedId == "" || selectedId == "Seleccionar opción")) {
                                empList.add(empData!!)
                            }
                        }
                    }

                    mAdapter = MyAdapter(empList)
                    empRecyclerView.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : MyAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            Toast.makeText(this@MainActivity, "" + empList[position].text, Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, DetallesActivity::class.java)

                            //put extras
                            intent.putExtra("empId", empList[position].empId)
                            intent.putExtra("empName", empList[position].text)
                            intent.putExtra("Image", empList[position].imageUrl)
                            startActivity(intent)
                        }

                        override fun onItemDeleteClick(position: Int) {
                            //mAdapter.deleteItem(position)
                            val dbRef =
                                FirebaseDatabase.getInstance().getReference("data").child(empList[position].empId!!)
                            val storage = FirebaseStorage.getInstance()
                            val storageReference = storage.getReferenceFromUrl(empList[position].imageUrl!!)
                            storageReference.delete().addOnSuccessListener {
                                // File deleted successfully
                                dbRef.removeValue()
                                Toast.makeText(this@MainActivity, "Eliminado", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(applicationContext, MainActivity::class.java))
                                finish()
                            }.addOnFailureListener { error ->
                                Toast.makeText(this@MainActivity, "Deleting Err ${error.message}", Toast.LENGTH_LONG)
                                    .show()
                                // Uh-oh, an error occurred!
                            }
                        }

                        override fun onItemMenuClick(position: Int, view: View) {
                            val popupMenus = PopupMenu(this@MainActivity, view)
                            popupMenus.inflate(R.menu.show_menu_recyclerview)
                            popupMenus.setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.editText -> {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "" + empList[position].text,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this@MainActivity, DetallesActivity::class.java)
                                        intent.putExtra("empId", empList[position].empId)
                                        intent.putExtra("empName", empList[position].text)
                                        intent.putExtra("Image", empList[position].imageUrl)
                                        startActivity(intent)
                                        true
                                    }

                                    R.id.delete -> {
                                        AlertDialog.Builder(this@MainActivity)
                                            .setTitle("Eliminar elemento ${empList[position].text}")
                                            .setMessage("¿Estás seguro de eliminar este elemento?")
                                            .setPositiveButton("Sí") { _, _ ->
                                                val dbRef = FirebaseDatabase.getInstance().getReference("data")
                                                    .child(empList[position].empId!!)
                                                val storage = FirebaseStorage.getInstance()
                                                val storageReference =
                                                    storage.getReferenceFromUrl(empList[position].imageUrl!!)
                                                storageReference.delete().addOnSuccessListener {
                                                    // File deleted successfully
                                                    dbRef.removeValue()
                                                    Toast.makeText(this@MainActivity, "Eliminado", Toast.LENGTH_SHORT)
                                                        .show()
                                                    startActivity(Intent(applicationContext, MainActivity::class.java))
                                                    finish()
                                                }.addOnFailureListener { error ->
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Deleting Err ${error.message}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    // Uh-oh, an error occurred!
                                                }
                                            }
                                            .setNegativeButton("No") { _, _ ->
                                                // No hacer nada
                                            }
                                            .show()
                                        true
                                    }

                                    else -> false
                                }
                            }
                            popupMenus.show()
                            // Opcional: Mostrar íconos en el menú emergente
                            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                            popup.isAccessible = true
                            val menu = popup.get(popupMenus)
                            menu.javaClass
                                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                                .invoke(menu, true)
                        }
                    })

                    empRecyclerView.visibility = View.VISIBLE
                    tvLoadingData.visibility = View.GONE
                    progressBar.visibility = View.GONE
                }

                if (!hasSelectedItem && selectedId != null) {
                    // Si no se encontró ningún elemento seleccionado, mostrar la lista completa del usuario
                    //Toast.makeText(this@MainActivity, "No se encontró el elemento seleccionado", Toast.LENGTH_SHORT).show()
                    getEmployeesData(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores, si es necesario
            }
        })
    }


    //para el menu
    private fun setFavoriteIcon(menuItem: MenuItem) {
        val id = if (favorito) R.drawable.ic_favorite;
        else R.drawable.ic_favorite_border;

        menuItem.icon = ContextCompat.getDrawable(this, id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        setFavoriteIcon(menu?.findItem(R.id.favorito)!!)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Buscar..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)

        return super.onCreateOptionsMenu(menu)
    }

    fun searchList(text: String) {
        val searchList = java.util.ArrayList<Data>()
        for (dataClass in empList) {
            if (dataClass.text?.lowercase()
                    ?.contains(text.lowercase(Locale.getDefault())) == true
            ) {
                searchList.add(dataClass)
            }
        }
        mAdapter.searchDataList(searchList)
    }

    //para el menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorito -> {
                favorito = !favorito
                setFavoriteIcon(item)
            }

            R.id.compartir -> {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Dale like a mi página en Facebook: https://www.facebook.com/hcodeYoutube/"
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            R.id.editarperfil -> {
                val intent = Intent(this@MainActivity, EditProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.salir -> {
                pref.prefClear()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

}