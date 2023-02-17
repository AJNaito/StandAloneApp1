package com.example.standaloneapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.view.View
import android.content.Intent
import android.os.Environment

class DisplayActivity : AppCompatActivity(), View.OnClickListener {
    // instance var
    private var firstName : String? = null
    private var lastName : String? = null
    private var profPath : String? = null

    // UI ELEMENTSSSSSSSS
    private var displayName : TextView? = null
    private var displayPic : ImageView? = null
    private var backButton : Button? = null

    private var mainIntent : Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        displayName = findViewById<View>(R.id.display_name) as TextView
        displayPic = findViewById<View>(R.id.display_pic) as ImageView
        backButton = findViewById<View>(R.id.back_button) as Button

        val receivedIntent = intent
        firstName = receivedIntent.getStringExtra("fn_data")
        lastName = receivedIntent.getStringExtra("ln_data")

        displayName!!.text = "$firstName $lastName is logged in!"

        profPath = receivedIntent.getStringExtra("tn_data")
        val profileImg = BitmapFactory.decodeFile(profPath)
        if (profileImg != null) {
            displayPic!!.setImageBitmap(profileImg)
        }

        backButton!!.setOnClickListener(this)
        mainIntent = Intent(this, MainActivity::class.java)
    }

    override fun onClick(view:View) {
        when (view.id) {
            R.id.back_button -> {
                startActivity(mainIntent)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("fn_data", firstName)
        outState.putString("ln_data", lastName)
        outState.putString("tn_data", profPath)
    }
}