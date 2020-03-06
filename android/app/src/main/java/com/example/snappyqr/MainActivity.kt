package com.example.snappyqr

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import com.google.android.gms.vision.barcode.Barcode
//import com.google.android.gms.vision.barcode.BarcodeDetector

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.send_button).setOnClickListener{
            startActivity(Intent(this, PickerActivity::class.java))
        }
        findViewById<Button>(R.id.receive_button).setOnClickListener{
            startActivity(Intent(this, RecvActivity::class.java))
        }
        //Array string allows asking for multiple permissions at the same time
        var str: Array<String> = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, str, REQUEST_CODE)
    }
    
    // permission handling code taken with credit to https://handyopinion.com/ask-runtime-permission-in-kotlin-android/
    private fun isPermissionsAllowed(perm:String): Boolean {
        return ContextCompat.checkSelfPermission(this,perm) == PackageManager.PERMISSION_GRANTED
    }

    private val REQUEST_CODE = 8

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        //PackageManager.permission_granted
        for (x in grantResults) {
            if (x!=PackageManager.PERMISSION_GRANTED) {
                // UH-OH. At least one permission we requested was not granted. We'll disable the buttons.
                Routines.disableButtons(this)
            }
        }
    }

}
