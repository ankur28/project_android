package com.example.projectandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class PlacesActivity : AppCompatActivity() {

    var adapter: RecyclerViewAdapter? = null
    lateinit var recyclerView: RecyclerView
  lateinit var pList:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places)
        recyclerView = findViewById(R.id.recycler_view)
        pList= ArrayList()

        val bundle = intent.extras
        if (bundle != null) {
            pList = bundle.getStringArrayList("data") as ArrayList<String>
        }

        addDataInRecyclerView(pList)
    }

    private fun addDataInRecyclerView(pList: ArrayList<String>) {
        if (pList.size > 0) {
            adapter = RecyclerViewAdapter(pList, context = applicationContext)

            // Setting the Adapter with the recyclerview
            recyclerView.adapter = adapter
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    // creating a menu to change map type
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.gMap -> {
              startActivity(Intent(this,MapsActivity::class.java))

            Toast.makeText(this@PlacesActivity, "Maps selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.email -> {
            startActivity(Intent(this,EmailActivity::class.java))
            Toast.makeText(this@PlacesActivity, "Email selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.about -> {
            startActivity(Intent(this,AboutActivity::class.java))

            Toast.makeText(this@PlacesActivity, "About selected", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onRestart() {
        super.onRestart()
        addDataInRecyclerView(pList)
    }
}