package com.example.snappyqr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class PickerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)
    }
    fun onClick(view: View){
        //file picker code

    }
    fun showFileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

    }
}
