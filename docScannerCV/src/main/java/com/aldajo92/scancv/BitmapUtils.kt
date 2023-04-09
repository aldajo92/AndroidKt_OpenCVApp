package com.aldajo92.scancv

import android.graphics.Bitmap

private var rotationMatrix = android.graphics.Matrix().apply {
    postRotate(90f)
}

fun Bitmap.rotate90Degrees(): Bitmap = Bitmap.createBitmap(
    this, 0, 0, this.width, this.height, rotationMatrix, true
)