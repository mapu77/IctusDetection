package org.hackprague.ictusdetection

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import android.hardware.SensorManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), SensorEventListener {

    val CAMERA_REQUEST_CODE = 0
    val TIME_TO_HOLD_DEVICE: Long = 10000
    val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private var imageUri: Uri? = null

    var cloudVision: CloudVision? = null;
    var ictusDetection = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cloudVision = CloudVision();
        toNextButton.setOnClickListener({
            startActivity(Intent(applicationContext, ImageTestFragmentHolder::class.java))
        })

        activateGyroscopeSensor()

        object : CountDownTimer(TIME_TO_HOLD_DEVICE, 1000) {

            var giro = false
            override fun onTick(millisUntilFinished: Long) {
                val l = millisUntilFinished / 1000
                countdown.text = "Put your smartphone in front of your face and hold it until the " +
                        "countdown finishes: " + l
                if (l < 5 && !giro) {
                    activateGyroscopeSensor()
                    giro = true
                }

            }

            override fun onFinish() {
                if (!ictusDetection) takePhoto()
                else {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder.setMessage("Te ha dado un ictus loco")
                    builder.create().show()
                }
            }
        }.start()


    }

    private fun activateGyroscopeSensor() {
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL
        )

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
                    photoImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri))

                    cloudVision?.callCloudVision(imageUri, this.applicationContext)
                    photoImageView.setImageBitmap(MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri))
                    progressBar.visibility = View.VISIBLE
                    progressBar.isIndeterminate = true
                    Toast.makeText(applicationContext, "Analysing image", Toast.LENGTH_LONG).show()

                    if (!ictusDetection) {
                        toNextButton.visibility = View.VISIBLE

                    }
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        System.out.println("onAccuracyChanged" + accuracy)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val MAX_MOVEMENT = 1
        if (event!!.values.get(0) > MAX_MOVEMENT ||
                event.values.get(1) > MAX_MOVEMENT ||
                event.values.get(2) > MAX_MOVEMENT) {
            System.out.println("Que te pega el ictus")
            ictusDetection = true
        }
    }
}
