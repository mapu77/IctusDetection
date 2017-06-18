package org.hackprague.ictusdetection

import android.app.AlertDialog
import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_image_test_holder.*
import java.util.*


/**
 * A placeholder fragment containing a simple view.
 */
class ImageTestFragment : Fragment() {

    private var labels = listOf("orange", "tree", "window", "house")

    private var indexLabel: Int = 0

    private var generator: Random = Random()

    private var wrong: Int = 0

    val EMERGENCY_NUMBER = "+34695648474"

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Collections.shuffle(labels, generator)
        return inflater!!.inflate(R.layout.fragment_image_test_holder, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        var selectedLabel = labels[indexLabel]
        displayImage(selectedLabel)
        next.setOnClickListener({
            val inputText = description.text.toString()
            if (selectedLabel != inputText.toLowerCase()) {
                wrong++
            }
            if (indexLabel < labels.size - 1) {
                indexLabel++
                displayImage(labels[indexLabel])
                selectedLabel = labels[indexLabel]
            }
            if (wrong > 2) {
                treatIctus();
            } else if (indexLabel == labels.size - 1) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("No symptoms detected")
                builder.create().show()

                object : CountDownTimer(4000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                    }

                    override fun onFinish() {

                    }
                }.start()
            }
            description.setText("")
        })
    }

    private fun treatIctus() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Ictus synthoms detected! Calling emergency service")
        builder.create().show()

        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                val intent = Intent(Intent.ACTION_CALL)
                intent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER))
                startActivity(intent)

            }
        }.start()
    }

    private fun displayImage(selectedLabel: String) {
        val identifier = context.resources.getIdentifier(selectedLabel, "drawable", context.packageName)
        imageTest.setImageDrawable(context.getDrawable(identifier))
    }
}
