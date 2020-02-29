package com.example.snappyqr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class RecvActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bcd = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.QR_CODE).build()

        setContentView(R.layout.activity_recv)

        Routines.setupCamViewer(this, findViewById(R.id.textureView))

        val qr_image = findViewById<ImageView>(R.id.qr_image)

        val imgResId = R.drawable.ic_launcher_background
        var resId = imgResId
        qr_image.setImageResource(imgResId)

        val button = findViewById<Button>(R.id.switch_image)
        button?.setOnClickListener {
            resId = if (resId == R.drawable.ic_launcher_background) R.mipmap.ic_launcher else R.drawable.ic_launcher_background
            qr_image.setImageResource(resId)
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

