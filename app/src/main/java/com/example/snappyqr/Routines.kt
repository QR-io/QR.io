package com.example.snappyqr

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.security.AccessController.getContext


class Routines {

    companion object {
        fun checkCameraPerms(c: Context,a: Activity) {
            if (ContextCompat.checkSelfPermission(
                    c,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Let's grab permission.
                ActivityCompat.requestPermissions(a, arrayOf(Manifest.permission.CAMERA), 1234)
            }
        }
    }
}