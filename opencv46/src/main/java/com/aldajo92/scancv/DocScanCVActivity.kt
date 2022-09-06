//package com.aldajo92.scancv
//
//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//
//class DocScanCVActivity : ComponentActivity() {
//    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            Log.i("docScanCV", "Permission granted")
//            shouldShowCamera.value = true
//        } else {
//            Log.i("docScanCV", "Permission denied")
//        }
//    }
//
//    private fun requestCameraPermission() {
//        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                Log.i("docScanCV", "Permission previously granted")
//                shouldShowCamera.value = true
//            }
//
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                Manifest.permission.CAMERA
//            ) -> Log.i("docScanCV", "Show camera permissions dialog")
//
//            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            if (shouldShowCamera.value) {
//                CameraView()
//            }
//        }
//        requestCameraPermission()
//    }
//
//    companion object {
//        fun openCamera(context: Context){
//            context.startActivity(
//                Intent(context, DocScanCVActivity::class.java)
//            )
//        }
//    }
//}
