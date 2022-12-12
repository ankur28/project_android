package com.example.projectandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_options, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.gMap -> {
            startActivity(Intent(this,MapsActivity::class.java))

            Toast.makeText(this@AboutActivity, "Maps selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.email -> {
            startActivity(Intent(this,EmailActivity::class.java))
            Toast.makeText(this@AboutActivity, "Email selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.email -> {
            startActivity(Intent(this,EmailActivity::class.java))

            Toast.makeText(this@AboutActivity, "About selected", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}