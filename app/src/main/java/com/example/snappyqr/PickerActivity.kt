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
        //Bunch of logging stuff that help find issues.
//        Log.d("RESULT", resultCode.toString())
//        Log.d("REQUEST", requestCode.toString())
//        Log.d("DATA", data.toString())
        if (requestCode != requestCode || requestCode != PICKER_REQUEST_CODE) {
            return
        }
        val intent = Intent(Intent(this, SendActivity::class.java))
        intent.putExtra("uri", data.toString())
        intent.putExtra("qr_string", "google.com")
        //More logging to see what is actually in data.
//        Log.d("DATA", data.toString())
        startActivity(intent)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun importURI(uri: Uri){
        val fileName: String = getFileName(uri)
        val tempFile: OutputStream? = null
        val fileCopy: OutputStream? = tempFile?.let { copyToTempFile(uri, it) }
    }
    private fun getFileName(uri: Uri): String{
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

        if (cursor != null) {
            if (cursor.count <= 0) {
                cursor.close()
                throw IllegalArgumentException("Can't obtain file name, cursor is empty")
            }
        }

        cursor?.moveToFirst()

        return cursor?.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)) ?: cursor?.close().toString()
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun copyToTempFile(uri: Uri, tempFile: OutputStream): OutputStream {
        // Obtain an input stream from the uri
        // Obtain an input stream from the uri
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IOException("Unable to obtain input stream from URI")

        // Copy the stream to the temp file
        FileUtils.copy(inputStream, tempFile)

        return tempFile
    }
}
