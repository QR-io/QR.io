package com.example.snappyqr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class PickerActivity : AppCompatActivity() {

    private val PICKER_REQUEST_CODE = 6006
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*";
        startActivityForResult(intent, PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("DATA", data.toString())
        if (requestCode != PICKER_REQUEST_CODE) { return }

        if(data == null){
            Log.d("DATA", "NO FILE CHOSEN")
            super.finish()
            return
        }

        val intent = Intent(this, SendActivity::class.java)
        intent.putExtra("uri", data.data.toString())
        startActivity(intent)
        finish()
    }
}
