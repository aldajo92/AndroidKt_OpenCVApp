package com.aldajo92.scancv

import android.graphics.Bitmap
import android.graphics.Matrix
import org.opencv.android.Utils
import org.opencv.core.Mat

private var rotationMatrix = Matrix().apply {
    postRotate(90f)
}

fun Float.getRotationMatrix() = Matrix().apply {
    postRotate(this@getRotationMatrix)
}

fun Bitmap.rotateWithMatrix(matrix: Matrix) : Bitmap = Bitmap.createBitmap(
    this, 0, 0, this.width, this.height, matrix, true
)

fun Bitmap.rotate90Degrees(): Bitmap = Bitmap.createBitmap(
    this, 0, 0, this.width, this.height, rotationMatrix, true
)

fun Bitmap.cropBitmapFromShapeDetected(): Bitmap {
    val mat = Mat()
    val matResult = Mat()
    val bmp32 = this.copy(Bitmap.Config.ARGB_8888, true)
    Utils.bitmapToMat(bmp32, mat)

    val imageCropped = detectShapeAndCropImage(mat.nativeObjAddr, matResult.nativeObjAddr)
    return if (imageCropped)
        Bitmap.createBitmap(matResult.width(), matResult.height(), Bitmap.Config.ARGB_8888)
            .apply {
                Utils.matToBitmap(matResult, this)
            }
    else this
}

fun Bitmap.detectAndDrawShapes() : Boolean {
    val mat = Mat()
    Utils.bitmapToMat(this, mat)
    return detectShape(mat.nativeObjAddr).apply {
        Utils.matToBitmap(mat, this@detectAndDrawShapes)
    }
}

external fun detectShape(matAddress: Long): Boolean

external fun detectShapeAndCropImage(matAddress: Long, matResult: Long): Boolean

