package com.aldajo92.scancv

import android.Manifest
import android.os.Build

const val REQUEST_CODE_PERMISSIONS = 10

val REQUIRED_PERMISSIONS =
    mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ).apply {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }.toTypedArray()