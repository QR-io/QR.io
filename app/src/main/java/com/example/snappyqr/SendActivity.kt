package com.example.snappyqr

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import net.glxn.qrgen.android.QRCode


class SendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val qr_string = intent.getStringExtra("qr_string")
        val uri = intent.getStringExtra("uri")

        val myBitmap: Bitmap = QRCode.from(qr_string).bitmap()
        val myImage: ImageView = findViewById<View>(R.id.imageView) as ImageView
        myImage.setImageBitmap(myBitmap)
    }
}
