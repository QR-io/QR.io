package com.example.snappyqr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.DocumentsContract
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.util.valueIterator
import androidx.documentfile.provider.DocumentFile
import com.example.snappyqr.Routines.Companion.toBitmap
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf


class RecvActivity : AppCompatActivity() {

    private val PICKER_REQUEST_CODE = 6006
    private lateinit var DOCUMENT_TREE : Uri // User-chosen directory to place the file

    @RequiresApi(Build.VERSION_CODES.O) // For Vibes
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recv)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, PICKER_REQUEST_CODE)

        val bcd = BarcodeDetector.Builder(applicationContext).setBarcodeFormats(Barcode.QR_CODE).build()
        if (!bcd.isOperational) {
            Toast.makeText(applicationContext,"QR code reader could not be instantiated.",Toast.LENGTH_LONG).show()
        }

        val qrImage = findViewById<ImageView>(R.id.qr_image)
        val imgResId = R.drawable.ic_launcher_background
        qrImage.setImageResource(imgResId)


        val dataMap = TreeMap<Int,ByteArray>()
        var fileName: String = ""

        val analyzer = ImageAnalysis.Analyzer { imageProxy: ImageProxy, rotationDegrees: Int ->
            val bitmap = imageProxy.toBitmap()
            //TODO(There may end up being more than 1 QR spotted. Write logic to grab the correct one.)

            val barcodes = bcd.detect(Frame.Builder().setBitmap(bitmap).build())

            if (barcodes.size() > 0) {
                for (barcode in barcodes.valueIterator()){
                    val frame = barcode.rawValue
                    val frameByteBuffer: ByteBuffer = ByteBuffer.wrap(frame.toByteArray())

                    val frameIndex: Int = frameByteBuffer.getInt()
                    frameByteBuffer.get()  // Skip the Null Terminator
                    val totalFrames: Int = frameByteBuffer.getInt()
                    frameByteBuffer.get()

                    if (frameIndex == 0) {
                        var fileNameByteArray: ByteArray = byteArrayOf()

                        while (true) {
                            val b: Byte = frameByteBuffer.get()
                            if (b == '\u0000'.toByte()){ break }
                            else
                                fileNameByteArray += b
                        }
                        fileName = StandardCharsets.UTF_8.decode(ByteBuffer.wrap(fileNameByteArray)).toString()
                        Log.d("OVERWRITE", fileName)
                    }
                    var data: ByteArray = frameByteBuffer.array()
                    data = data.copyOfRange(frameByteBuffer.arrayOffset() + frameByteBuffer.position(), frameByteBuffer.arrayOffset() + frameByteBuffer.limit())

                    Log.d("frameIndex", frameIndex.toString())
                    Log.d("totalFrames", totalFrames.toString())
                    Log.d("data", String(data))

                    val quarter: Int = kotlin.math.ceil(totalFrames.toDouble() / 4).toInt()

                    val dataMapSize = dataMap.size
                    Log.d("dataMapSize", dataMapSize.toString())
                    dataMap[frameIndex] = data

                    if (dataMap.size == totalFrames){
                        makeFileFromByteArrays(dataMap, fileName)
                        return@Analyzer
                    }
//
                    if (dataMap.size % quarter == 0 && dataMap.size != totalFrames){
                        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        val numVibes = dataMap.size / quarter
                        val timings: ArrayList<Long>? = arrayListOf()
                        for (i in 1..numVibes){
                            timings?.add(150) // Off
                            timings?.add(150) // On
                        }
                        v.vibrate(VibrationEffect.createWaveform(timings?.toLongArray(), -1))
                    }

                }
            }
        }
        Routines.setupCamAnalysis(this,analyzer)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeFileFromByteArrays(data: TreeMap<Int, ByteArray>, fileName: String) {
        //TODO Send 100 in the intent. This number is BytesPerQR in SendActivity.

        val pickedDirectory: DocumentFile? = DocumentFile.fromTreeUri(this, DOCUMENT_TREE)
        var file: DocumentFile? = pickedDirectory?.findFile(fileName)
        if (file == null) {
            file = pickedDirectory?.createFile(MimeTypeMap.getFileExtensionFromUrl(fileName), fileName)
        }
        Log.d("fileName", fileName)
        val fos = contentResolver.openOutputStream(file?.uri!!)

        for (dataPiece in data.values) {
            fos?.write(dataPiece)
        }
        fos?.flush()
        fos?.close()
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        super.finish()
        return
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // Catch the DirectoryFile the user picks
        super.onActivityResult(requestCode, resultCode, data)
        if(data == null){
            Log.d("DOCUMENT_TREE", "NO FOLDER CHOSEN")
            super.finish()
            return
        }
        DOCUMENT_TREE = data.data!!
    }
}