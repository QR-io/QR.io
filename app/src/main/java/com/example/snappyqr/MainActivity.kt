package com.example.snappyqr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val send_button = findViewById<Button>(R.id.send_button)
        send_button.setOnClickListener{
            val intent = Intent(this, PickerActivity::class.java)
            startActivity(intent)
        }
    }
}
