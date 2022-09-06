package com.aldajo92.scancv

import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.nio.ByteBuffer

class OpenCVAnalyzer(private val listener: (Bitmap) -> Unit) : ImageAnalysis.Analyzer {

    private lateinit var bitmapBuffer: Bitmap
    private var imageRotationDegrees: Int = 0

    private var pauseAnalysis = false
    private var rotationMatrix = android.graphics.Matrix().apply {
        postRotate(90f)
    }

   private fun ByteBuffer.toByteArray(): ByteArray {
       rewind()    // Rewind the buffer to zero
       val data = ByteArray(remaining())
       get(data)   // Copy the buffer into a byte array
       return data // Return the byte array
   }

   override fun analyze(image: ImageProxy) {
       if (!::bitmapBuffer.isInitialized) {
           // The image rotation and RGB image buffer are initialized only once
           // the analyzer has started running
           imageRotationDegrees = image.imageInfo.rotationDegrees
           bitmapBuffer = Bitmap.createBitmap(
               image.width, image.height, Bitmap.Config.ARGB_8888)

           bitmapBuffer
       }

       if (pauseAnalysis) {
           image.close()
           return
       }

       // Copy out RGB bits to our shared buffer
       image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer)  }

//       CoroutineScope(Dispatchers.IO).launch{

           // Process the image
           val bitmap = bitmapBuffer.rotate()

           val mat = Mat()

           val bmp32: Bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
           Utils.bitmapToMat(bmp32, mat)
           processFrame(mat.nativeObjAddr)
           Utils.matToBitmap(mat, bmp32)
           listener(bmp32)
//       }
   }

    private fun Bitmap.rotate() = Bitmap.createBitmap(this, 0, 0, this.width, this.height, rotationMatrix, true)

    external fun processFrame(matAddr: Long)
}
