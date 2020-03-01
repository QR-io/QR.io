package com.example.snappyqr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.util.valueIterator
import com.example.snappyqr.Routines.Companion.toBitmap
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class RecvActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bcd = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.QR_CODE).build()

        setContentView(R.layout.activity_recv)

        //Routines.setupCamViewer(this, findViewById(R.id.textureView))

        val qr_image = findViewById<ImageView>(R.id.qr_image)

        val imgResId = R.drawable.ic_launcher_background
        var resId = imgResId
        qr_image.setImageResource(imgResId)

        /*val button = findViewById<Button>(R.id.switch_image)
        button?.setOnClickListener {
            resId = if (resId == R.drawable.ic_launcher_background) R.mipmap.ic_launcher else R.drawable.ic_launcher_background
            qr_image.setImageResource(resId)
        }*/

        if (!bcd.isOperational) {
            Toast.makeText(applicationContext,"QR code reader could not be instantiated.",Toast.LENGTH_LONG)
        }

        var dataMap = TreeMap<Int,ByteArray>()

        val analyzer = ImageAnalysis.Analyzer { imageProxy: ImageProxy, i: Int ->
            val bitmap = imageProxy.toBitmap()
            //TODO(There may end up being more than 1 QR spotted. Write logic to grab the correct one.)

            val barcodes = bcd.detect(Frame.Builder().setBitmap(bitmap).build())

            if (barcodes.size() > 0) {
                for (barcode in barcodes.valueIterator()){

                    var frame = barcode.rawValue
                    var data = frame.split(",")
                    var frameIndex = data[0].trim()
                    var dataLength = data[1].trim()
                    var byteData = data[2].trim()

//                    Log.d("INDEX", frameIndex)
//                    Log.d("LENGTH", dataLength)
//                    Log.d("DATA", byteData)
                    dataMap[frameIndex.toInt()] = byteData.toByteArray()

                    if (dataMap.lastKey() + 1 == dataLength.toInt() - 1){
                        makeFileFromByteArrays(dataMap)
                        break
                    }

//                    if (barcode != null) {
//                        Log.d("RAWVALUE", barcode.rawValue)
//                    } else {
//                        Log.d("ELSE", "NULL")
//                    }
                }
//                Log.d("RAWVALUE", barcodes[0].toString())
            }
            //Log.v("SnappyQR",""+barcodes.size())
        }
        Routines.setupCamAnalysis(this,analyzer)
    }

    private fun makeFileFromByteArrays(data: TreeMap<Int, ByteArray>) {
        //TODO Send 100 in the intent. This number is BytesPerQR in SendActivity.
        var fos: FileOutputStream? = null
        val theFile = File(getExternalFilesDir(null),"THEFILE.txt")
        theFile.createNewFile()
        try {
            fos = FileOutputStream(File(getExternalFilesDir(null),"THEFILE.txt"))
            val baos = ByteArrayOutputStream()
            // Put data in your baos
            for (dataPiece in data.values) {
                print(dataPiece)
                baos.writeTo(fos)
            }
        } catch (ioe: IOException) { // Handle exception here
            ioe.printStackTrace()
        } finally {
            Log.d("FINALLY", "Finally closing.")
            fos?.flush()
            fos?.close()
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
//        if(!isExternalStorageWritable()){
//            Log.d("PERMS", "NOT writable!!!!!")
//        }else {
//            val outputStream = ByteArrayOutputStream(data.lastKey() * 100)
//
//            val location = "THEFILE.txt"
//
//            val outfile = openFileOutput(location, Context.MODE_APPEND)
//            Log.d("FILECREATION", "FILE IS BEING CREATED!!!!!!!!!!!!!!!!!!")
//
//            for (dataPiece in data.values) {
//                outfile.write(dataPiece)
//                outputStream.write(dataPiece)
//            }
//            outfile.flush()
//            outputStream.flush()
//            outfile.close()
//            outputStream.close()
//        }
    }
    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

}

/*
 {
                val f = Frame.Builder().setBitmap(it.bitmap).build()
                val qr = bcd.detect(f)
                Log.v("SnappyQR","found " + qr.size() + " codes")
 }
 */

