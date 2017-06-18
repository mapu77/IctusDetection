package org.hackprague.ictusdetection

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            if (indexLabel < labels.size-1) {
                indexLabel++
                displayImage(labels[indexLabel])
                selectedLabel = labels[indexLabel]
            } else {
                if (wrong < 2) {
                    Toast.makeText(context, "You are healthy", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Oops, ictus!", Toast.LENGTH_LONG).show()
                }
            }
            description.setText("")
        })
    }

    private fun displayImage(selectedLabel: String) {
        val identifier = context.resources.getIdentifier(selectedLabel, "drawable", context.packageName)
        imageTest.setImageDrawable(context.getDrawable(identifier))
    }
}
