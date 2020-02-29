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


        askForPermissions(Manifest.permission.CAMERA)
        askForPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)

    }


    // permission handling code taken with credit to https://handyopinion.com/ask-runtime-permission-in-kotlin-android/
    fun isPermissionsAllowed(perm:String): Boolean {
        return ContextCompat.checkSelfPermission(this,perm) == PackageManager.PERMISSION_GRANTED
    }

    private val REQUEST_CODE = 8

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
        //PackageManager.permission_granted
        for (x in grantResults) {
            if (x!=PackageManager.PERMISSION_GRANTED) {
                // UH-OH. At least one permission we requested was not granted. We'll disable the buttons.
                disableButtons()


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

        // We didn't get the perms, so we have to disable the button until the user comes back.
        disableButtons()
    }

    fun disableButtons() {
        val toastRestart = {v: View ->
            Toast.makeText(applicationContext, "Please grant the app permissions in settings and restart the app.",
                Toast.LENGTH_LONG).show()
        }
        findViewById<Button>(R.id.send_button).setOnClickListener(toastRestart)
        findViewById<Button>(R.id.receive_button).setOnClickListener(toastRestart)
    }
}
