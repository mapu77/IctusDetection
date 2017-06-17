package org.hackprague.ictusdetection

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val CAMERA_REQUEST_CODE = 0
    val TIME_TO_PHOTO: Long = 5000

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        object : CountDownTimer(TIME_TO_PHOTO, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                countdown.text = "In " + millisUntilFinished / 1000 + " seconds camera will be " +
                        "open and you should capture a photo of your face"
            }

            override fun onFinish() {
                takePhoto()
            }
        }.start()
    }

    private fun takePhoto() {
        countdown.visibility = View.INVISIBLE
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && imageUri != null) {
                    photoImageView.visibility = View.VISIBLE
                    photoImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri))
                    progressBar.visibility = View.VISIBLE
                    progressBar.isIndeterminate = true
                    Toast.makeText(applicationContext, "Analysing image", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
