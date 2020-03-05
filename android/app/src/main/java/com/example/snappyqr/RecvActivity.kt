package com.example.snappyqr

import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.util.valueIterator
import com.example.snappyqr.Routines.Companion.toBitmap
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class RecvActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

        // flag to indicate that analyzer shouldn't run
        // since the executor doesn't actually stop running until the UI
        // updates and the current activity is stopped.
        var done = false

        val analyzer = ImageAnalysis.Analyzer { imageProxy: ImageProxy, i: Int ->

            if (done) {
                return@Analyzer
            }

            val bitmap = imageProxy.toBitmap()
            //TODO(There may end up being more than 1 QR spotted. Write logic to grab the correct one.)

            val barcodes = bcd.detect(Frame.Builder().setBitmap(bitmap).build())

            if (barcodes.size() > 0) {
                for (barcode in barcodes.valueIterator()){
                    var frame = barcode.rawValue
                    Log.d("SnappyQR",frame)
                    var data = frame.split(",")

                    /*if (data.size<3) {
                        // this frame is corrupt. skipping.
                        continue
                    }*/

                    var frameIndex = data[0].trim()
                    var dataLength = data[1].trim()
                    var byteData = data[2]
                    for (x in 3 until data.size) {
                        byteData += data[x]
                    }

                   /*if (byteData.length < 100 && frameIndex.toInt()!=dataLength.toInt()-1) {
                        // QR code is truncated.
                        // This seems to be a regular failure mode, where it doesn't read the
                        // entire QR code.
                        continue
                    }*/

                    Log.d("INDEX", frameIndex)
                    Log.d("LENGTH", dataLength)
                    Log.d("DATA", byteData)
//                    Log.d("4TH_THING", data[3])
                    var size = dataMap.size
                    Log.d("FRAMES_I_HAVE", "$size")
                    dataMap[frameIndex.toInt()] = byteData.toByteArray()

                    if (dataMap.size == dataLength.toInt()){
                        done = true
                        makeFileFromByteArrays(dataMap)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeFileFromByteArrays(data: TreeMap<Int, ByteArray>) {
        //TODO Send 100 in the headers. This number is BytesPerQR in SendActivity.
        var fos: FileOutputStream? = null

        //TODO Don't force null. Will also fix the let used later.
        var nullTermLocation: Pair<Int, Int> = Pair(0,0)

        for ((frame, info) in data) {
            if (info.contains(('\u0000').toByte())) {
                nullTermLocation = Pair(frame, info.indexOf(('\u0000').toByte()))
            }
        }

        val location1: Pair<Int, Int> = Pair(0,0)
        val location2: Pair<Int, Int> = Pair(nullTermLocation.first, nullTermLocation.second)

        //Filename size will always be less than 100 * the amount of frames that contain filename info.
        //TODO Change this to be exact
        var filename = ByteArray(100 * location2.first)

        for ((frameiter, info) in data) {
            if (frameiter < location2.first) {
                filename += info
            } else if (frameiter == location2.first){
                filename += info.copyOfRange(0,location2.second)
            }
        }

        val theFile = File(getExternalFilesDir(null),filename.toString())
        Log.d("FILENAME", filename.toString())
        theFile.createNewFile()
        try {
            fos = FileOutputStream(theFile)
            //val baos = ByteArrayOutputStream()
            // Put data in your baos
            for (dataPiece in data.values) {
                //print(dataPiece)
                fos.write(dataPiece)
            }
        } catch (ioe: IOException) { // Handle exception here
            ioe.printStackTrace()
        } finally {
            Log.d("FINALLY", "Finally closing.")
            fos?.flush()
            fos?.close()
            vibrateWhenDone()
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrateWhenDone(){
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else { //deprecated in API 26
            v.vibrate(VibrationEffect.createOneShot(500, 10))
        }
    }

}

/*
 {
                val f = Frame.Builder().setBitmap(it.bitmap).build()
                val qr = bcd.detect(f)
                Log.v("SnappyQR","found " + qr.size() + " codes")
 }
 */

