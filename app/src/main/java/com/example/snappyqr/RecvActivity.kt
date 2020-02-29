package com.example.snappyqr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.TextureView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class RecvActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bcd = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.QR_CODE).build()

        setContentView(R.layout.activity_recv)

        Routines.setupCamViewer(this,findViewById(R.id.textureView))
    }

/*
 {
                val f = Frame.Builder().setBitmap(it.bitmap).build()
                val qr = bcd.detect(f)
                Log.v("SnappyQR","found " + qr.size() + " codes")
 }
 */

}
