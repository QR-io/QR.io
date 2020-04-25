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
import java.nio.ByteBuffer
import java.util.concurrent.Executor


class Routines {

    companion object {
        fun setupCamAnalysis(a: LifecycleOwner, hook: ImageAnalysis.Analyzer): Executor {
            val imageAnalysisConfig = ImageAnalysisConfig.Builder().apply {
                setLensFacing(CameraX.LensFacing.FRONT)
                setTargetResolution(Size(640, 480))
                setImageQueueDepth(5)

            }.build()

            var imageAnalysis = ImageAnalysis(imageAnalysisConfig)

            val executor = Executors.newSingleThreadExecutor()

            imageAnalysis.setAnalyzer(executor, hook)

            CameraX.bindToLifecycle(a, imageAnalysis)

            return executor
        }

        // fun fact, "min" requires api level 24.
        // so we wrote our own.
        fun min(a: Int, b: Int): Int {
            return if (a < b) a else b
        }

        @ExperimentalStdlibApi
        fun getNthQRCode(n: Int, file: ByteArray, databytes: Int, totalFrames: Int, fileName: String): Bitmap {
            var dataString: ByteArray =
                file.sliceArray(
                    (n * databytes)..min(
                        (n + 1) * databytes - 1,
                        file.size - 1
                    )
                )

            val frameNumArray: ByteArray =  ByteBuffer.allocate(4).putInt(n).array() + '\u0000'.toByte() // Current Frame Index, null terminated
            val totalFramesArray: ByteArray = ByteBuffer.allocate(4).putInt(totalFrames).array() + '\u0000'.toByte() // Number of Total Frames, null terminated

            // Header Logging
            Log.d("frameNumArrayLength", frameNumArray.size.toString())
            Log.d("frameNumArray", ByteBuffer.wrap(frameNumArray).getInt().toString())
            Log.d("totalFramesArrayLength", totalFramesArray.size.toString())
            Log.d("totalFramesArray", ByteBuffer.wrap(totalFramesArray).getInt().toString())

            // Create the Header
            var dataStringHeader = frameNumArray + totalFramesArray

            if(n == 0){
                val fileNameArray: ByteArray = fileName.toByteArray() + '\u0000'.toByte()
                Log.d("fileNameArrayLength", fileNameArray.size.toString())
                Log.d("fileNameArray", String(fileNameArray))
                dataStringHeader += fileNameArray
            }

            // Data Logging
            Log.d("dataStringRaw", String(dataString))
            Log.d("dataStringRawSize", dataString.size.toString())
            dataString = dataStringHeader + dataString
            Log.d("dataStringHeader", String(dataStringHeader))
            Log.d("dataStringWithHeader", String(dataString))
            Log.d("dataStringWithHeader", dataString.size.toString())

            return QRCode.from(String(dataString)).bitmap()
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