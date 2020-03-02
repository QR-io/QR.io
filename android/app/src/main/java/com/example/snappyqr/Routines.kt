package com.example.snappyqr

import android.accessibilityservice.GestureDescription
import android.graphics.*
import android.graphics.Bitmap
import android.util.Log
import android.util.Size
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executors
import net.glxn.qrgen.android.QRCode
import java.io.ByteArrayOutputStream


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
                setImageQueueDepth(5)

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

        // snippet provided by
        // https://heartbeat.fritz.ai/image-classification-on-android-with-tensorflow-lite-and-camerax-4f72e8fdca79
        fun ImageProxy.toBitmap(): Bitmap {
            val yBuffer = planes[0].buffer // Y
            val uBuffer = planes[1].buffer // U
            val vBuffer = planes[2].buffer // V

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)

            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
            val imageBytes = out.toByteArray()
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
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
