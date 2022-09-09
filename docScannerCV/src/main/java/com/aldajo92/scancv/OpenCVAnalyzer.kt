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

    private val _bitmapStreamShapeDetectionLiveData = MutableLiveData<Bitmap>()
    val bitmapStreamShapeDetectionLiveData: LiveData<Bitmap> = _bitmapStreamShapeDetectionLiveData

    private val _shapeDetectedLiveData = MutableLiveData(false)
    val shapeDetectedLiveData: LiveData<Boolean> = _shapeDetectedLiveData

    private lateinit var bitmapBuffer: Bitmap
    private var imageRotationDegrees: Int = 0

    private var pauseAnalysis = false

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
        val bmp32 = bitmapBuffer.rotate90Degrees().copy(Bitmap.Config.ARGB_8888, true)
        _bitmapStreamLiveData.postValue(bmp32)

        //////////////////// image processing ////////////////////
        val mat = Mat()
        Utils.bitmapToMat(bmp32, mat)

        val shapeDetectedFlag = detectShape(mat.nativeObjAddr)
        _shapeDetectedLiveData.postValue(shapeDetectedFlag)

        Utils.matToBitmap(mat, bmp32)
        _bitmapStreamShapeDetectionLiveData.postValue(bmp32)
    }

    fun cropBitmapFromShapeDetected(bitmap: Bitmap): Bitmap {
        val mat = Mat()
        val matResult = Mat()
        val bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)

        val imageCropped = detectShapeAndCropImage(mat.nativeObjAddr, matResult.nativeObjAddr)
        return if (imageCropped)
            Bitmap.createBitmap(matResult.width(), matResult.height(), Bitmap.Config.ARGB_8888)
                .apply {
                    Utils.matToBitmap(matResult, this)
                }
        else bitmap
    }

    external fun detectShape(matAddress: Long): Boolean

    external fun detectShapeAndCropImage(matAddress: Long, matResult: Long): Boolean
}
