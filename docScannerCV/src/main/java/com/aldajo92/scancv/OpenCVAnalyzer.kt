package com.aldajo92.scancv

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.opencv.android.Utils
import org.opencv.core.Mat

class OpenCVAnalyzer() : ImageAnalysis.Analyzer {

    private val _bitmapStreamLiveData = MutableLiveData<Bitmap>()
    val bitmapStreamLiveData: LiveData<Bitmap> = _bitmapStreamLiveData

    private val _bitmapStreamShapeDetectionLiveData = MutableLiveData<Bitmap>()
    val bitmapStreamShapeDetectionLiveData: LiveData<Bitmap> = _bitmapStreamShapeDetectionLiveData

    private val _shapeDetectedLiveData = MutableLiveData(false)
    val shapeDetectedLiveData: LiveData<Boolean> = _shapeDetectedLiveData

    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationDegreesMatrix: Matrix

    override fun analyze(image: ImageProxy) {
        if (!::bitmapBuffer.isInitialized) {
            rotationDegreesMatrix = image.imageInfo.rotationDegrees.toFloat().getRotationMatrix()
            bitmapBuffer = Bitmap.createBitmap(
                image.width,
                image.height,
                Bitmap.Config.ARGB_8888
            )
        }

        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }
        val bmp32 = bitmapBuffer.rotateWithMatrix(rotationDegreesMatrix).copy(Bitmap.Config.ARGB_8888, true)
        _bitmapStreamLiveData.postValue(bmp32.copy(Bitmap.Config.ARGB_8888, false))

        val bitmapWithShapes = bmp32.detectAndDrawShapes()
        _shapeDetectedLiveData.postValue(bitmapWithShapes)
        _bitmapStreamShapeDetectionLiveData.postValue(bmp32)
    }

}
