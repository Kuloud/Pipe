package com.kuloud.android.pipe.client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButtonToggleGroup
import com.kuloud.android.pipe.OnPipeConnectedListener
import com.kuloud.android.pipe.Pump
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var mLog: TextView
    private lateinit var mPump: Pump

    private val GALLERY_IMAGE_REQ_CODE = 102

    private var mGalleryFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPump = Pump(applicationContext, "hello")

        findViewById<MaterialButtonToggleGroup>(R.id.toggleGroup).addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.start -> {
                        mPump.connect(object : OnPipeConnectedListener {
                            override fun onPipeConnected(status: Int) {
                                Log.d("kuloud", "onPipeConnected $status")
                                mLog.text = "Pipe connected $status"
                            }


                        })
                    }
                    R.id.stop -> {
                        mPump.disconnect()
                        mLog.text = "Pipe disconnected"
                    }
                }
            }

        }
        findViewById<Button>(R.id.send).setOnClickListener {
            pickGalleryImage(it)
        }
        mLog = findViewById(R.id.log)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.e("TAG", "Path:${ImagePicker.getFilePath(data)}")
            // File object will not be null for RESULT_OK
            val file = ImagePicker.getFile(data)!!
            when (requestCode) {
                GALLERY_IMAGE_REQ_CODE -> {
                    mGalleryFile = file
                    val res = mPump.pumps(file.absolutePath)
                    Log.e("kuloud", "$res")
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    fun pickGalleryImage(view: View) {
        ImagePicker.with(this)
            // Crop Image(User can choose Aspect Ratio)
            .crop()
            // User can only select image from Gallery
            .galleryOnly()

            .galleryMimeTypes(  //no gif images at all
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            // Image resolution will be less than 1080 x 1920
            .maxResultSize(1080, 1920)
            .start(GALLERY_IMAGE_REQ_CODE)
    }
}