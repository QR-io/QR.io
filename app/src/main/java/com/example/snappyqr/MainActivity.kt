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
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
//import com.google.android.gms.vision.barcode.Barcode
//import com.google.android.gms.vision.barcode.BarcodeDetector

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askForPermissions(Manifest.permission.CAMERA)
        askForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

    }

    val REQUEST_CODE = 77

    // permission handling code taken with credit to https://handyopinion.com/ask-runtime-permission-in-kotlin-android/
    fun isPermissionsAllowed(perm:String): Boolean {
        return ContextCompat.checkSelfPermission(this,perm) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(perm:String): Boolean {
        if (!isPermissionsAllowed(perm)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity,perm)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(this as Activity,arrayOf(perm),REQUEST_CODE)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                    findViewById<Button>(R.id.send_button).setOnClickListener{
                        startActivity(Intent(this, PickerActivity::class.java))
                    }
                    findViewById<Button>(R.id.receive_button).setOnClickListener{
                        startActivity(Intent(this, RecvActivity::class.java))
                    }
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    for(perm in permissions){
                        if (!isPermissionsAllowed(perm)){
                            askForPermissions(perm)
                        }
                    }
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel",null)
            .show()
    }

}
