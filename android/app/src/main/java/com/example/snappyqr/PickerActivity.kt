package com.example.snappyqr

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream


class PickerActivity : AppCompatActivity() {

    private val PICKER_REQUEST_CODE = 6006
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)
        val chooseFileButton = findViewById<Button>(R.id.choose_file_button)
        chooseFileButton.setOnClickListener{
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*";
            startActivityForResult(intent, PICKER_REQUEST_CODE)
            Log.d("SEND", "Sending intent for Picker.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != requestCode || requestCode != PICKER_REQUEST_CODE) {
            return
        }
        val intent = Intent(Intent(this, SendActivity::class.java))
        intent.putExtra("uri", data?.data.toString())
        intent.putExtra("qr_string", "google.com")
        startActivity(intent)
    }
}
