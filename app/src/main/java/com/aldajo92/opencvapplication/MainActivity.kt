package com.aldajo92.opencvapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aldajo92.opencvapplication.databinding.ActivityMainBinding
import com.aldajo92.scancv.DocScanCV2Activity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.buttonCamera.setOnClickListener {
            DocScanCV2Activity.openCamera(this)
        }
    }

}
