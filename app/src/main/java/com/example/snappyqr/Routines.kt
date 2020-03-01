package com.example.snappyqr

import android.graphics.Bitmap
import android.util.Log
import android.util.Size
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
import net.glxn.qrgen.android.QRCode


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
        fun setupCamAnalysis(a : LifecycleOwner, hook:ImageAnalysis.Analyzer) {
            val imageAnalysisConfig = ImageAnalysisConfig.Builder().apply {
                setLensFacing(CameraX.LensFacing.FRONT)
                setTargetResolution(Size(640, 480))
            }.build()

            var imageAnalysis = ImageAnalysis(imageAnalysisConfig)


            imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(),hook)

            CameraX.bindToLifecycle(a,imageAnalysis)
        }

        // fun fact, "min" requires api level 24.
        // so we wrote our own.
        fun min(a:Int,b:Int) : Int {
            return if (a<b) a else b
        }

        fun getNthQRCode(n:Int, file:ByteArray, databytes:Int, totalFrames: Int) : Bitmap {
            //val databytes = 10
            var dataString:String = String(file.sliceArray((n*databytes)..min((n+1)*databytes-1,file.size-1)))
            val dataStringHeader = String(longToUInt32ByteArray(n)) + String(longToUInt32ByteArray(totalFrames))
            dataString = dataStringHeader + dataString

            Log.d("nBYTES", dataStringHeader)
            return QRCode.from(dataString).bitmap()

        }
        fun longToUInt32ByteArray(value: Int): ByteArray {
            val bytes = ByteArray(4)
            bytes[3] = (value and 0xFFFF).toByte()
            bytes[2] = ((value ushr 8) and 0xFFFF).toByte()
            bytes[1] = ((value ushr 16) and 0xFFFF).toByte()
            bytes[0] = ((value ushr 24) and 0xFFFF).toByte()
            return bytes
        }
    }

}