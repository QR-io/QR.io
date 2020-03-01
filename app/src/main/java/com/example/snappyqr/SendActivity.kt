package com.example.snappyqr

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SendActivity : AppCompatActivity() {

    private val fakeData : ByteArray = ByteArray(1000)

    var handler:Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {

        for (x in 0..fakeData.size-1) {
            fakeData[x] = x.toByte()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val uri = intent.getStringExtra("uri")

        val data : ByteArray = readBytes(applicationContext, Uri.parse(uri))

        /*
        val myBitmap: Bitmap = QRCode.from(qr_string).bitmap()
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView
        myImage.setImageBitmap(myBitmap)

         */
        var counter = 0
        val bytesPerQR = 100
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView
        val flashqrs : () -> Unit = {
            counter+=1
            counter = counter.rem(data.size/bytesPerQR)
            val qr = Routines.getNthQRCode(counter,data,bytesPerQR,
                kotlin.math.ceil(data.size / bytesPerQR.toDouble()).toInt())
            handler.post {
                myImage.setImageBitmap(qr)
            }
        }
        Log.d("DATASIZE", data.size.toString())
        val exec = Executors.newSingleThreadScheduledExecutor()
        exec.scheduleAtFixedRate(flashqrs,100,500, TimeUnit.MILLISECONDS)
        //exec.

    }
    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray =
        context.contentResolver.openInputStream(uri)!!.buffered().use { it.readBytes() }
}
