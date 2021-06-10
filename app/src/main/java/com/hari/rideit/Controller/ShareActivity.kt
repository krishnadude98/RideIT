package com.hari.rideit.Controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
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
        var na=""
        try{
            val READ_BLOCK_SIZE=100
            val fileIn: FileInputStream = openFileInput("name.txt")
            val InputRead = InputStreamReader(fileIn)
            val inputBuffer = CharArray(READ_BLOCK_SIZE)
            var s: String? = ""
            var charRead: Int=1
            while (InputRead.read(inputBuffer).also({ charRead = it }) > 0) {
                // char to string conversion
                val readstring = String(inputBuffer, 0, charRead)
                s += readstring
            }

            InputRead.close()
            na= s.toString()

        }catch (err:Exception){

            var myview= LayoutInflater.from(this).inflate(R.layout.name_layout,null)
            var mbuilder= android.app.AlertDialog.Builder(this).setView(myview).setTitle("Provide Name")
            var maalertDialog= mbuilder.show()
            myview.findViewById<Button>(R.id.BidBtn).setOnClickListener {
                val name:EditText= myview.findViewById(R.id.bidName)
                if(!TextUtils.isEmpty(name.text.toString())){
                    na= name.text.toString()
                    val fileout: FileOutputStream = openFileOutput("name.txt", MODE_PRIVATE)
                    val outputWriter = OutputStreamWriter(fileout)
                    outputWriter.write(name.text.toString())
                    outputWriter.close()
                    maalertDialog.hide()
                    Toast.makeText(this,"Click Find Btn Again",Toast.LENGTH_SHORT).show()
                }
                else{
                    maalertDialog.show()
                }
            }

        }
        if(DataService.jwttoken==""&&na!=""){
            val intent= Intent(this,ShareRideActivity::class.java)
            startActivity(intent)
        }
        else if (DataService.jwttoken!=""&&na!=""){
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
        Log.d("MYEMAIL",email)
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
        var jsoobj:JSONObject
        val request2= object :JsonArrayRequest(Request.Method.GET,url3,null,Response.Listener { res->
            var ispresent=0
            for(i in 0 until res.length()){
                val shareride: JSONObject = res.getJSONObject(i)
                if(myid==shareride.getString("accountid")){
                    mdialog.hide()
                    val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                    ispresent=1
                    editor.putInt("ispresent",1)
                    editor.apply()
                    editor.commit()

                    jsoobj= shareride

                    val ispresent = sharedPreferences.getInt("ispresent", 0)
                    if(ispresent==1){
                        val intent= Intent(this,AdDetailsActivity::class.java)

                        intent.putExtra("JWT",DataService.jwttoken)
                        intent.putExtra("from",jsoobj.getString("from"))
                        intent.putExtra("to",jsoobj.getString("to"))
                        val date:String= jsoobj.getString("date")
                        var tem=""
                        for(i in 0..9){
                            tem= tem+date[i]
                        }
                        intent.putExtra("date",tem)
                        intent.putExtra("vehicleno",jsoobj.getString("vehicleno"))
                        intent.putExtra("vehiclemodel",jsoobj.getString("vehiclemodel"))
                        intent.putExtra("eamount",jsoobj.getString("eamount"))
                        intent.putExtra("id",jsoobj.getString("_id"))
                        intent.putExtra("time",jsoobj.getString("time"))
                        startActivity(intent)
                        break
                    }

                }

            }
                if(ispresent==0){
                    mdialog.hide()
                    noAdAlert()
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


    }
    fun noAdAlert(){
        val alertdialog: AlertDialog.Builder=AlertDialog.Builder(this)
        alertdialog.setTitle("You Don't have a Advertisement pls create one to see")//for set Title
        alertdialog.setMessage("Create one advertisement by clicking share own ride button")// for Message
        alertdialog.setIcon(R.drawable.ic_baseline_new_releases_24) // for alert icon
        alertdialog.setPositiveButton("OK!") { dialog, id ->

        }

        val alert = alertdialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


}