package com.example.snappyqr

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.provider.OpenableColumns
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File
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
            intent.type = "file/*";
            startActivityForResult(intent, PICKER_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != requestCode || resultCode != PICKER_REQUEST_CODE) {
            return
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun importURI(uri: Uri){
        val fileName: String = getFileName(uri)
        var tempFile: OutputStream? = null
        val fileCopy: OutputStream? = tempFile?.let { copyToTempFile(uri, it) }
    }
    fun getFileName(uri: Uri): String{
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

        if (cursor != null) {
            if (cursor.getCount() <= 0) {
                cursor.close()
                throw IllegalArgumentException("Can't obtain file name, cursor is empty")
            }
        }

        if (cursor != null) {
            cursor.moveToFirst()
        }

        val fileName: String =
            cursor?.getString(cursor?.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)) ?: cursor?.close().toString()

        return fileName
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
