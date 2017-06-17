package org.hackprague.ictusdetection

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64.DEFAULT
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    val CAMERA_REQUEST_CODE = 0
    val TIME_TO_PHOTO: Long = 5000

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        object : CountDownTimer(TIME_TO_PHOTO, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                countdown.setText("In " + millisUntilFinished / 1000 + " seconds camera will be " +
                        "open and you should capture a photo of your face ")
            }

            override fun onFinish() {
                countdown.visibility = View.INVISIBLE
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "New Picture")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }.start()
    }

    private fun encodeImage(): String? {
        val bm = BitmapFactory.decodeFile(imageUri?.path)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, DEFAULT)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK && imageUri != null) {
                    photoImageView.visibility = View.VISIBLE
                    photoImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri))
                }
            }
        }
    }
}
