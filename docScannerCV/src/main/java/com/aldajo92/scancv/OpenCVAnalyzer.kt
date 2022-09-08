package com.aldajo92.scancv

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.opencv.android.Utils
import org.opencv.core.Mat

class OpenCVAnalyzer() : ImageAnalysis.Analyzer {

    private val _bitmapStreamLiveData = MutableLiveData<Bitmap>()
    val bitmapStreamLiveData: LiveData<Bitmap> = _bitmapStreamLiveData

    private lateinit var bitmapBuffer: Bitmap
    private var imageRotationDegrees: Int = 0

    private var shapeDetected: Boolean = false

    private var pauseAnalysis = false
    private var rotationMatrix = android.graphics.Matrix().apply {
        postRotate(90f)
    }

    override fun analyze(image: ImageProxy) {

        if (!::bitmapBuffer.isInitialized) {
            imageRotationDegrees = image.imageInfo.rotationDegrees
            bitmapBuffer = Bitmap.createBitmap(
                image.width,
                image.height,
                Bitmap.Config.ARGB_8888
            )
        }

        if (pauseAnalysis) {
            image.close()
            return
        }

        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val mat = Mat()
        val bmp32 = bitmapBuffer.rotate().copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)

        shapeDetected = detectShape(mat.nativeObjAddr)
        Utils.matToBitmap(mat, bmp32)
        _bitmapStreamLiveData.postValue(bmp32)
    }

    private fun Bitmap.rotate() = Bitmap.createBitmap(
        this, 0, 0, this.width, this.height, rotationMatrix, true
    )

    fun cropImageFromBitmap(listenerCropped: (Bitmap) -> Unit = { _ -> }) {
        if (shapeDetected) {
            val mat = Mat()
            val matResult = Mat()
            val bmp32 = bitmapBuffer.rotate().copy(Bitmap.Config.ARGB_8888, true)
            Utils.bitmapToMat(bmp32, mat)

            detectShapeAndCropImage(mat.nativeObjAddr, matResult.nativeObjAddr)
            val bmp =
                Bitmap.createBitmap(matResult.width(), matResult.height(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(matResult, bmp)

            listenerCropped(bmp)
        }
    }

    external fun detectShape(matAddress: Long): Boolean

    external fun detectShapeAndCropImage(matAddress: Long, matResult: Long): Boolean
}
