package com.example.snappyqr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bcd = BarcodeDetector.Builder(applicationContext).setBarcodeFormats(Barcode.QR_CODE)

    }
}
