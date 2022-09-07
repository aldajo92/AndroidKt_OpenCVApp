package com.aldajo92.scancv

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Mat

class OpenCVAnalyzer(private val listener: (Bitmap) -> Unit) : ImageAnalysis.Analyzer {

    private lateinit var bitmapBuffer: Bitmap
    private var imageRotationDegrees: Int = 0

    private var width: Int = 0
    private var height: Int = 0

    private var pauseAnalysis = false
    private var rotationMatrix = android.graphics.Matrix().apply {
        postRotate(90f)
    }

    override fun analyze(image: ImageProxy) {
        if (!::bitmapBuffer.isInitialized) {
            imageRotationDegrees = image.imageInfo.rotationDegrees
            bitmapBuffer = Bitmap.createBitmap(
                image.width, image.height, Bitmap.Config.ARGB_8888
            )
            width = image.width
            height = image.height
            bitmapBuffer
        }

        if (pauseAnalysis) {
            image.close()
            return
        }

        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val mat = Mat()
        val bmp32 = bitmapBuffer.rotate().copy(Bitmap.Config.ARGB_8888, true)
        Utils.bitmapToMat(bmp32, mat)
        detectShape(mat.nativeObjAddr)
        Utils.matToBitmap(mat, bmp32)
        listener(bmp32)
    }

    private fun Bitmap.rotate() =
        Bitmap.createBitmap(this, 0, 0, this.width, this.height, rotationMatrix, true)

    fun cropImageFromBitmap(listenerCropped: (Bitmap) -> Unit = { _ -> }) {
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

    external fun detectShape(matAddress: Long)

    external fun detectShapeAndCropImage(matAddress: Long, matResult: Long)
}
