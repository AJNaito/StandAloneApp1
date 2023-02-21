package com.example.standaloneapp

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.os.Environment
import android.graphics.BitmapFactory
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //Instance Variables
    private var firstName : String? = null
    private var lastName : String? = null
    private var midName : String? = null
    private var profilePicPath : String? = null

    // UI Elements
    private var editFirstName : EditText? = null
    private var editMidName : EditText? = null
    private var editLastName : EditText? = null
    private var submitButton : Button? = null
    private var imageButton : ImageButton? = null

    private var profilePic : Bitmap? = null
    private var displayIntent : Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // find UI elements on the resource file
        editFirstName = findViewById<View>(R.id.first_name) as EditText
        editMidName = findViewById<View>(R.id.mid_name) as EditText
        editLastName = findViewById<View>(R.id.last_name) as EditText

        submitButton = findViewById<View>(R.id.submit_but) as Button
        imageButton = findViewById<View>(R.id.profile_button) as ImageButton

        // register button clicks
        submitButton!!.setOnClickListener(this)
        imageButton!!.setOnClickListener(this)

        displayIntent = Intent(this, DisplayActivity::class.java)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        firstName = editFirstName!!.text.toString()
        midName = editMidName!!.text.toString()
        lastName = editLastName!!.text.toString()

        outState.putString("fn_data", firstName)
        outState.putString("mn_data", midName)
        outState.putString("ln_data", lastName)

        if (!profilePicPath.isNullOrBlank()) {
            outState.putString("tn_data", profilePicPath)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        firstName = savedInstanceState.getString("fn_data")
        midName = savedInstanceState.getString("mn_data")
        lastName = savedInstanceState.getString("ln_data")

        if (!firstName.isNullOrBlank()) {
            editFirstName!!.setText(firstName)
        }

        if (!midName.isNullOrBlank()) {
            editMidName!!.setText(midName)
        }

        if (!lastName.isNullOrBlank()) {
            editLastName!!.setText(lastName)
        }

        profilePicPath = savedInstanceState.getString("tn_data")
        if (!profilePicPath.isNullOrBlank()) {
            profilePic = BitmapFactory.decodeFile(profilePicPath)
            if (profilePic != null) {
                imageButton!!.setImageBitmap(profilePic)
            }
        }

    }


    private fun printErrorMessage(msg:String) {
        val fullMsg = "Complete Fields: $msg"

        Toast.makeText(this@MainActivity, fullMsg, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.submit_but -> {
                var errorMsg : String? = ""
                firstName = editFirstName!!.text.toString()
                midName = editMidName!!.text.toString()
                lastName = editLastName!!.text.toString()

                if (firstName.isNullOrBlank()) {
                    errorMsg += "First Name, "
                }

                if (midName.isNullOrBlank()) {
                    errorMsg += "Middle Name, "
                }

                if (lastName.isNullOrBlank()) {
                    errorMsg += "Last Name, "
                }

                if (profilePicPath.isNullOrBlank()) {
                    errorMsg += "Profile Pic"
                }

                if (errorMsg.isNullOrBlank()) {
                    firstName = firstName!!.replace("^\\s+".toRegex(), "")
                    midName = midName!!.replace("^\\s+".toRegex(), "")
                    lastName = lastName!!.replace("^\\s+".toRegex(), "")

                    displayIntent!!.putExtra("fn_data", firstName)
                    displayIntent!!.putExtra("mn_data", midName)
                    displayIntent!!.putExtra("ln_data", lastName)
                    displayIntent!!.putExtra("tn_data", profilePicPath)

                    startActivity(displayIntent)
                } else {
                    printErrorMessage(errorMsg)
                }
            }

            R.id.profile_button -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraLauncher.launch(cameraIntent)
                } catch (ex:ActivityNotFoundException) {
                }
            }
        }
    }

    private var cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result->
        // if we got a valid picture from the camera
        if (result.resultCode == RESULT_OK) {
            val extras = result.data!!.extras
            profilePic = extras!!["data"] as Bitmap?

            if (isExternalStorageWritable) {
                profilePicPath = saveImage(profilePic)
                imageButton!!.setImageBitmap(profilePic)
            }
        }

    }

    private fun saveImage(picture : Bitmap?) :String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()


        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val file_name = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, file_name)

        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)
            picture!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            Toast.makeText(this, "Couldn't Save", Toast.LENGTH_SHORT).show()
        }

        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }
}

