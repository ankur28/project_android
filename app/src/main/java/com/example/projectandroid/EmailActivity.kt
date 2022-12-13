package com.example.projectandroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.model.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class EmailActivity : AppCompatActivity() {

    lateinit var emailEditText: EditText
    lateinit var send_mail: Button
    var location: String? = ""
    var address: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        emailEditText = findViewById(R.id.emailEditText)
        send_mail = findViewById(R.id.sendEmailbutton)
        getFromSharedPrefs()

        send_mail.setOnClickListener(
            View.OnClickListener{
                println("called email client")
                composeEmail()

            }
        )
    }

    private fun composeEmail() {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")

        val sharedPreferences = applicationContext.getSharedPreferences("mysettings",
            AppCompatActivity.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString("email_address", emailEditText.text.toString())
        editor.apply()


        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailEditText.text.toString()))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My Location")
        emailIntent.putExtra(Intent.EXTRA_TEXT, location+address)
        emailIntent.selector = selectorIntent

        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun getFromSharedPrefs() {

        val prefsEditor = getSharedPreferences("mysettings", Context.MODE_PRIVATE)
        if(emailEditText.text.toString() == ""){
            emailEditText.setText(prefsEditor.getString("email_address", ""))
        }
        location =  prefsEditor.getString("location","")
        address = prefsEditor.getString("address","")
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
            Toast.makeText(this@EmailActivity, "Maps selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.gPlaces -> {
            startActivity(Intent(this,PlacesActivity::class.java))
            Toast.makeText(this@EmailActivity, "Places selected", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.about -> {
            startActivity(Intent(this,AboutActivity::class.java))
            Toast.makeText(this@EmailActivity, "About selected", Toast.LENGTH_SHORT).show()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onRestart() {
        super.onRestart()
        getFromSharedPrefs()
    }
}