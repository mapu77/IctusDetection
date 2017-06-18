package org.hackprague.ictusdetection

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_image_test_holder.*

/**
 * A placeholder fragment containing a simple view.
 */
class ImageTestFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_image_test_holder, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        next.setOnClickListener({
            val inputText = description.text.toString()
            val text: String
            if (inputText.equals("orange")) {
                text = "Ok"
            } else {
                text = "Oops, ictus!"
            }
            Toast.makeText(view!!.context, text, Toast.LENGTH_LONG).show()
        })
    }
}
