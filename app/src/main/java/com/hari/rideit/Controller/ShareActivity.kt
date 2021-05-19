package com.hari.rideit.Controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception

class ShareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        if( intent.getStringExtra("JWT")!=null){
            DataService.jwttoken=intent.getStringExtra("JWT")

        }
        else{
            try {


                val READ_BLOCK_SIZE = 100
                val fileIn: FileInputStream = openFileInput("mytextfile.txt")
                val InputRead = InputStreamReader(fileIn)
                val inputBuffer = CharArray(READ_BLOCK_SIZE)
                var s: String? = ""
                var charRead: Int = 1
                while (InputRead.read(inputBuffer).also({ charRead = it }) > 0) {
                    // char to string conversion
                    val readstring = String(inputBuffer, 0, charRead)
                    s += readstring
                }
                InputRead.close()
                DataService.jwttoken=s.toString()


            }catch (e: Exception){

                DataService.jwttoken= ""
            }
        }


    }
     fun ShareYourBtnClicked(view: View){
        if(DataService.jwttoken==""){
            loginAlert()
            return
        }
         val intent= Intent(this,ShareRideAddActivity::class.java)
         intent.putExtra("JWT",DataService.jwttoken)
         startActivity(intent)

    }

    fun loginAlert(){
        val alertdialog: AlertDialog.Builder=AlertDialog.Builder(this)
        alertdialog.setTitle("Pls Login to create a Ad!")//for set Title
        alertdialog.setMessage("You need to login in order to use this feature")// for Message
        alertdialog.setIcon(R.drawable.ic_baseline_new_releases_24) // for alert icon
        alertdialog.setPositiveButton("Yes") { dialog, id ->
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        alertdialog.setNegativeButton("Cancel") { dialog, id ->
            // set your desired action here.
            val intent= Intent(this,ShareActivity::class.java)
            startActivity(intent)
        }
        val alert = alertdialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }



    fun FindBtnClicked(view: View){
        if(DataService.jwttoken==""){
            val intent= Intent(this,ShareRideActivity::class.java)
            startActivity(intent)
        }
        else{
            val intent= Intent(this,ShareRideActivity::class.java)
            intent.putExtra("JWT",DataService.jwttoken)
            startActivity(intent)
        }
    }




}