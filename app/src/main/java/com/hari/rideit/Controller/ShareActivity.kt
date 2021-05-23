package com.hari.rideit.Controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import org.json.JSONObject
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.net.ConnectException

class ShareActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"
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

    fun ViewOwnAd(view: View) {
        if(DataService.jwttoken==""){
            loginAlert()
            return
        }
        var email:String=""

        var mdialog: SimpleArcDialog = SimpleArcDialog(this)
        mdialog.setConfiguration(ArcConfiguration(this))
        mdialog.show()
        val RequestQueue: RequestQueue = Volley.newRequestQueue(this)
        try {
            val READ_BLOCK_SIZE = 100
            val fileIn: FileInputStream = openFileInput("email.txt")
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
            email=s.toString()

        }catch (err:Exception){
            Toast.makeText(this,err.toString(),Toast.LENGTH_SHORT).show()
        }
        val url2= "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/account/email/${email}"
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)

        try {

            val request1= object:
                JsonArrayRequest(Request.Method.GET, url2, null, Response.Listener { res ->

                    val temp: JSONObject =res.getJSONObject(0)
                    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putString("id",temp.getString("_id"))
                    editor.apply()
                    editor.commit()



                }, Response.ErrorListener { err: VolleyError ->
                    if (err is NetworkError || err.cause is ConnectException) {
                        mdialog.hide()
                        Toast.makeText(
                            this,
                            "Pls Check Your Connection And Try Again",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (err is com.android.volley.TimeoutError) {
                        mdialog.hide()
                        Toast.makeText(this, "Net Connection Problem1", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        mdialog.hide()
                        Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
                    }
                }){}
            RequestQueue.add(request1)

        }catch (err:Exception){
            Toast.makeText(this,err.toString(),Toast.LENGTH_SHORT).show()
        }
        val myid = sharedPreferences.getString("id", "0")

        val url3="http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/share/"
        val request2= object :JsonArrayRequest(Request.Method.GET,url3,null,Response.Listener { res->

            for(i in 0 until res.length()){
                val shareride: JSONObject = res.getJSONObject(i)
                if(myid==shareride.getString("accountid")){

                    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putInt("ispresent",1)
                    editor.apply()
                    editor.commit()

                    break
                }

            }

        },Response.ErrorListener { err:VolleyError->
            if (err is NetworkError || err.cause is ConnectException) {
                mdialog.hide()
                Toast.makeText(
                    this,
                    "Pls Check Your Connection And Try Again",
                    Toast.LENGTH_LONG
                ).show()
            } else if (err is com.android.volley.TimeoutError) {
                mdialog.hide()
                Toast.makeText(this, "Check Network and try again", Toast.LENGTH_LONG)
                    .show()
            } else {
                mdialog.hide()
                Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
            }
        }){}


        RequestQueue.add(request2)
        val ispresent = sharedPreferences.getInt("ispresent", 0)
        if(ispresent==1){
            val intent= Intent(this,AdDetailsActivity::class.java)
            intent.putExtra("JWT",DataService.jwttoken)
            startActivity(intent)
        }
        else{
            noAdAlert()
        }

    }
    fun noAdAlert(){
        val alertdialog: AlertDialog.Builder=AlertDialog.Builder(this)
        alertdialog.setTitle("You Don't have a Advertisement pls create one to see")//for set Title
        alertdialog.setMessage("Create one advertisement by clicking share own ride button")// for Message
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


}