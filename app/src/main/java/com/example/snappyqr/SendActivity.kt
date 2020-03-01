package com.example.snappyqr

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class SendActivity : AppCompatActivity() {

    val fakeData : ByteArray = ByteArray(1000)

    override fun onCreate(savedInstanceState: Bundle?) {

        for (x in 0..fakeData.size-1) {
            fakeData[x] = x.toByte()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val qr_string = intent.getStringExtra("qr_string")
        val uri = intent.getStringExtra("uri")

        /*
        val myBitmap: Bitmap = QRCode.from(qr_string).bitmap()
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView
        myImage.setImageBitmap(myBitmap)

         */
        var counter = 0
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView
        val flashqrs : () -> Unit = {
            counter+=1
            counter = counter.rem(99)
            myImage.setImageBitmap(Routines.getNthQRCode(counter,fakeData,5))
            //Log.w("SnappyQR","Thread running.")
        }
        val exec = Executors.newSingleThreadScheduledExecutor()
        exec.scheduleAtFixedRate(flashqrs,100,100, TimeUnit.MILLISECONDS)
        //exec.

    }
}
