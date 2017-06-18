package org.hackprague.ictusdetection

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class ImageTestFragmentHolder : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val transition = this.fragmentManager.beginTransaction()
        transition.replace(R.id.base_content, ImageTestFragment())
        transition.addToBackStack(null)
        transition.commit()

        setContentView(R.layout.activity_image_test_holder)
    }

}
