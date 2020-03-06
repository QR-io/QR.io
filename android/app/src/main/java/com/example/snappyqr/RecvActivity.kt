package com.example.snappyqr

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.net.toUri
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

    private val PICKER_REQUEST_CODE_RECV = 6008

    private var saveLocation = ""


    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, PICKER_REQUEST_CODE_RECV)

        val bcd = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.QR_CODE).build()

        setContentView(R.layout.activity_recv)

        val qr_image = findViewById<ImageView>(R.id.qr_image)

        val imgResId = R.drawable.ic_launcher_background
        var resId = imgResId
        qr_image.setImageResource(imgResId)

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
                    val frame = barcode.rawValue
                    Log.d("SnappyQR",frame)
                    val data = frame.split(",")

                    val frameIndex = data[0].trim()
                    val dataLength = data[1].trim()
                    var byteData = data[2]
                    for (x in 3 until data.size) {
                        byteData += data[x]
                    }

                    Log.d("INDEX", frameIndex)
                    Log.d("LENGTH", dataLength)
                    Log.d("DATA", byteData)

                    val size = dataMap.size
                    Log.d("FRAMES_I_HAVE", "$size")
                    dataMap[frameIndex.toInt()] = byteData.toByteArray()

                    if (dataMap.size == dataLength.toInt()){
                        done = true
                        makeFileFromByteArrays(dataMap)
                    }
                }
            }
        }
        Routines.setupCamAnalysis(this,analyzer)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeFileFromByteArrays(data: TreeMap<Int, ByteArray>) {
        //TODO Send 100 in the headers. This number is BytesPerQR in SendActivity.
        var fos: FileOutputStream? = null

        //TODO Don't force null.
        var nullTermLocation: Pair<Int, Int> = Pair(0,0)

        for ((frame, info) in data) {
            if (info.contains(('\u0000').toByte())) {
                nullTermLocation = Pair(frame, info.indexOf(('\u0000').toByte()))
            }
        }

        //Filename size will always be less than 100 * the amount of frames that contain filename info.
        //TODO Change this to be exact
        var filename = ByteArray(100 * nullTermLocation.first)

        for ((frameiter, info) in data) {
            if (frameiter < nullTermLocation.first) {
                filename += info
            } else if (frameiter == nullTermLocation.first){
                filename += info.copyOfRange(0,nullTermLocation.second)
            }
        }


        Log.d("SAVELOCATION", saveLocation)
        Log.d("FILENAME", String(filename))

        val theFile = File(saveLocation + String(filename))

        Log.d("SAVEDFILE", theFile.absolutePath)

        try {
            theFile.createNewFile()
        } catch (ioe: IOException){
            val toastRestart = {v: View ->
                Toast.makeText(applicationContext, "Could not create new file. Please try again.",
                    Toast.LENGTH_LONG).show()
            }
            ioe.printStackTrace()
        } finally {
            try {
                fos = FileOutputStream(theFile)

                // Write every frame of data to file
                for (dataPiece in data.values) {
                    fos.write(dataPiece)
                }
            } catch (ioe: IOException) { // Handle exception here
                ioe.printStackTrace()
            } finally {
                Log.d("FINALLY", "Finally closing.")
                fos?.flush()
                fos?.close()
                //TODO Find new vibe function that does not require an API elevation.
                vibrateWhenDone()
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != requestCode || requestCode != PICKER_REQUEST_CODE_RECV) {
            return
        }
        saveLocation = data?.data.toString()
        Log.d("SAVELOCATION", saveLocation)
    }

}