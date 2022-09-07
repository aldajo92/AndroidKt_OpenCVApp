package com.aldajo92.scancv

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aldajo92.scancv.databinding.ActivityDocScanV2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.OpenCVLoader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DocScanCV2Activity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityDocScanV2Binding
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var ivBitmap: ImageView

    private var imageResult: Bitmap? = null

    private val cvAnalyzer = OpenCVAnalyzer { bitmap ->
        CoroutineScope(Dispatchers.Main).launch {
            ivBitmap.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityDocScanV2Binding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        ivBitmap = viewBinding.ivBitmap

        viewBinding.button.setOnClickListener {
            cvAnalyzer.cropImageFromBitmap { bitmapResult ->
                CoroutineScope(Dispatchers.Main).launch {
                    imageResult = bitmapResult
                    viewBinding.ivResult.setImageBitmap(bitmapResult)
                }
            }
        }

        viewBinding.doneButton.setOnClickListener {
            imageResult?.let {
                val imageUri = saveMediaToStorage(it, this)
                setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(PHOTO_IMAGE_BUNDLE_KEY, imageUri?.path)
                )
                finish()
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val imageAnalyzer = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, cvAnalyzer)
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    imageAnalyzer
                )

            } catch (exc: Exception) {
                Log.e(this::class.java.name, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        init {
            if (!OpenCVLoader.initDebug()) Log.d(this::class.java.name, "Unable to load OpenCV")
            else Log.d(this::class.java.name, "OpenCV loaded")
            System.loadLibrary("native-lib")
        }

        private const val REQUEST_CODE_PERMISSIONS = 10

        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        const val PHOTO_IMAGE_BUNDLE_KEY = "PHOTO_IMAGE_BUNDLE_KEY"

        fun openCameraIntent(context: Context) = Intent(context, DocScanCV2Activity::class.java)

    }
}
