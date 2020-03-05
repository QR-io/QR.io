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


    var handler:Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val uri = intent.getStringExtra("uri")

        val filename = Uri.parse(uri).lastPathSegment.toString()
        var data : ByteArray = readBytes(applicationContext, Uri.parse(uri))

        data += filename.toByteArray()
        data += "/u0000".toByteArray()

        /*
        val myBitmap: Bitmap = QRCode.from(qr_string).bitmap()
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView
        myImage.setImageBitmap(myBitmap)

         */
        var counter = 0
        val bytesPerQR = 100
        val frames = kotlin.math.ceil(data.size/bytesPerQR.toDouble()).toInt()
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView
        val flashqrs : () -> Unit = {
            counter+=1
            counter = counter.rem(frames)
            val qr = Routines.getNthQRCode(counter,data,bytesPerQR,frames)
            handler.post {
                myImage.setImageBitmap(qr)
            }
        }
        Log.d("DATASIZE", data.size.toString())
        val exec = Executors.newSingleThreadScheduledExecutor()
        exec.scheduleAtFixedRate(flashqrs,100,500, TimeUnit.MILLISECONDS)

    }
    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray =
        context.contentResolver.openInputStream(uri)!!.buffered().use { it.readBytes() }
}
