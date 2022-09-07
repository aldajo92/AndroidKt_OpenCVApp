package com.aldajo92.opencvapplication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.aldajo92.opencvapplication.databinding.ActivityMainBinding
import com.aldajo92.scancv.DocScanCV2Activity
import com.aldajo92.scancv.DocScanCV2Activity.Companion.PHOTO_IMAGE_BUNDLE_KEY

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    private val requestPhotoResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            it.data
                ?.getStringExtra(PHOTO_IMAGE_BUNDLE_KEY)
                ?.let { pathString: String ->
                    Log.i("alejo", pathString)
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.buttonCamera.setOnClickListener {
            requestPhotoResult.launch(DocScanCV2Activity.openCameraIntent(this))
        }
    }

}
