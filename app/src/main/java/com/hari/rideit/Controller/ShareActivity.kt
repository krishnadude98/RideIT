package com.hari.rideit.Controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.hari.rideit.R

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)


    }
     fun ShareYourBtnClicked(view: View){
        Toast.makeText(this,"1 Clicked",Toast.LENGTH_SHORT).show()
    }



    fun FindBtnClicked(view: View){
        Toast.makeText(this,"2 clicked",Toast.LENGTH_SHORT).show()
    }




}