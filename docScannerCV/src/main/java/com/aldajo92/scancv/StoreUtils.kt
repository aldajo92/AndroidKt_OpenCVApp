package com.aldajo92.scancv

import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

// based from https://www.simplifiedcoding.net/android-save-bitmap-to-gallery/
fun saveMediaToStorage(bitmap: Bitmap): Uri? {
    val filename = "${System.currentTimeMillis()}.jpg"
    val file = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        filename
    )
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
    return Uri.fromFile(file)
}
