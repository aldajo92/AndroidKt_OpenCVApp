package com.aldajo92.opencvapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aldajo92.opencvapplication.databinding.ActivityMainBinding
import com.aldajo92.scancv.DocScanCV2Activity

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityMainBinding
//    private lateinit var cameraExecutor: ExecutorService
//    private var ivBitmap: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.buttonCamera.setOnClickListener {
            DocScanCV2Activity.openCamera(this)
        }

//        // Request camera permissions
//        if (allPermissionsGranted()) {
//            startCamera()
//        } else {
//            ActivityCompat.requestPermissions(
//                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//
//        ivBitmap = viewBinding.ivBitmap
//
//        cameraExecutor = Executors.newSingleThreadExecutor()

    }

//    external fun processFrame(matAddr: Long)
//
//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            // Used to bind the lifecycle of cameras to the lifecycle owner
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
//                }
//
//            val imageAnalyzer = ImageAnalysis.Builder()
//                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//                .build()
//                .also {
//                    it.setAnalyzer(cameraExecutor, OpenCVAnalyzer { bitmap ->
//                        CoroutineScope(Dispatchers.Main).launch {
//                            ivBitmap?.setImageBitmap(bitmap)
//                        }
//                    })
//                }
//
//            try {
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll()
//
//                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(
//                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
//
//            } catch(exc: Exception) {
//                Log.e(TAG, "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(
//            baseContext, it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CODE_PERMISSIONS) {
//            if (allPermissionsGranted()) {
//                startCamera()
//            } else {
//                Toast.makeText(this,
//                    "Permissions not granted by the user.",
//                    Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        }
//    }
//
//    companion object {
//        init {
//            if (!OpenCVLoader.initDebug())
//                Log.d("aldajo", "Unable to load OpenCV")
//            else
//                Log.d("aldajo", "OpenCV loaded")
//
//            System.loadLibrary("native-lib")
//        }
//        private const val TAG = "CameraXApp"
//        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS =
//            mutableListOf (
//                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO
//            ).apply {
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                }
//            }.toTypedArray()
//    }
}
