package com.example.snappyqr

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Size
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.activity_recv.*
import java.security.AccessController.getContext


class Routines {

    companion object {

        fun setupCamViewer(a: LifecycleOwner, j: TextureView) {
            // modified example code from https://codelabs.developers.google.com/codelabs/camerax-getting-started/

            // Create configuration object for the viewfinder use case
            val previewConfig = PreviewConfig.Builder().apply {
                setLensFacing(CameraX.LensFacing.FRONT)
                setTargetResolution(Size(640, 480))
            }.build()


            // Build the viewfinder use case
            val preview = Preview(previewConfig)

            // Every time the viewfinder is updated, recompute layout
            preview.setOnPreviewOutputUpdateListener {

                // To update the SurfaceTexture, we have to remove it and re-add it
                val parent = j.parent as ViewGroup
                parent.removeView(j)
                parent.addView(j, 0)

                j.surfaceTexture = it.surfaceTexture

            }
            // Bind use cases to lifecycle
            // If Android Studio complains about "this" being not a LifecycleOwner
            // try rebuilding the project or updating the appcompat dependency to
            // version 1.1.0 or higher.
            CameraX.bindToLifecycle(a, preview)
        }
        fun setupCamAnalysis(a : LifecycleOwner, hook:(j:Bitmap) -> Unit) {
            val imageAnalysisConfig = ImageAnalysisConfig.Builder().apply {
                setLensFacing(CameraX.LensFacing.FRONT)
                setTargetResolution(Size(640, 480))
            }.build()

            var imageAnalysis = ImageAnalysis(imageAnalysisConfig)

            // todo add hook
            //imageAnalysis.setAnalyzer(hook)
        }
    }
}