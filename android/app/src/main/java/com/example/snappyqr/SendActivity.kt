package com.example.snappyqr

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SendActivity : AppCompatActivity() {


    var handler:Handler = Handler()
    val exec = Executors.newSingleThreadScheduledExecutor()

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView

        val uri: String = intent?.getStringExtra("uri").toString()
        val fileName: String = File(Uri.parse(uri).path).name

        val data : ByteArray = readBytes(applicationContext, Uri.parse(uri))

        var counter = 0
        val bytesPerQR = 100
        val frames = kotlin.math.ceil(data.size/bytesPerQR.toDouble()).toInt()
        val flashqrs : () -> Unit = {
            counter += 1
            counter = counter.rem(frames)
            val qr = Routines.getNthQRCode(counter, data, bytesPerQR, frames, fileName)
            handler.post { myImage.setImageBitmap(qr) }
        }
        Log.d("dateSizeBytes", data.size.toString())
        exec.scheduleAtFixedRate(flashqrs,100,350, TimeUnit.MILLISECONDS)

    }
    @Throws(IOException::class)
    private fun readBytes(context: Context, uri: Uri): ByteArray =
        context.contentResolver.openInputStream(uri)!!.buffered().use { it.readBytes() }

    override fun onBackPressed() {
        exec.shutdown()
        super.onBackPressed()
    }
}
