package com.hari.rideit.Controller

import android.content.Intent
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.hari.rideit.Adapters.DriverAdapter
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import com.hari.rideit.model.DriverModel
import com.hari.rideit.model.ShareRideModel
import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import org.json.JSONObject
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Error
import java.lang.Exception
import java.net.ConnectException
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

class RentRiderActivity : AppCompatActivity() {
    var lat by Delegates.notNull<Double>()
    var long by Delegates.notNull<Double>()
    lateinit var adapter:DriverAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_rider)
        lat=intent.getStringExtra("lat").toDouble()
        long=intent.getStringExtra("long").toDouble()

        val city= getCityName(lat,long)

        val loc= findViewById<TextView>(R.id.myloc)
        loc.text=city
        var btn= findViewById<Button>(R.id.work_as_driver)
        var ten= findViewById<Button>(R.id.tenbtn)
        var hundred= findViewById<Button>(R.id.hundredbtn)
        lateinit var DriverModelList:ArrayList<DriverModel>
        DriverModelList= ArrayList<DriverModel>()
        lateinit var DriverModelList2:ArrayList<DriverModel>
        DriverModelList2= ArrayList<DriverModel>()

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
        val url="http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/rentrider/"
        val myview= findViewById<ListView>(R.id.myridersview)
        var mdialog: SimpleArcDialog = SimpleArcDialog(this)
        mdialog.setConfiguration(ArcConfiguration(this))
        mdialog.show()
        val request= object:JsonArrayRequest(Request.Method.GET,url,null,Response.Listener { res->
            mdialog.hide()
            for(i in 0 until res.length()){
                val retrider:JSONObject= res.getJSONObject(i)
                val name= retrider.getString("userid")
                val licenseno= retrider.getString("licenseno")
                val licensetype= retrider.getString("licensetype")
                val latitude:Double= retrider.getDouble("lat")
                val longitude= retrider.getDouble("long")
                val contact= retrider.getString("contact")



                var geoCoder=Geocoder(this, Locale.getDefault())

                var address = geoCoder.getFromLocation(latitude,longitude, 1)

                val cityname = address.get(0).locality!!


                val location=cityname

                var imageid=retrider.getString("imageid")
                var imgid= "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/"+imageid

                val rentride:DriverModel= DriverModel(name,location,imgid,licenseno,licensetype,contact)
                val km=getKilometeres(lat,long,latitude,longitude)
                Log.d("KM",km.toString())
                if(km>10){
                    DriverModelList2.add(rentride)
                    Log.d("HUNDRED","INSIDE 100")
                }
                else {
                    DriverModelList2.add(rentride)
                    DriverModelList.add(rentride)
                    Log.d("TEN","INSIDE 10")

                }
            }

            adapter= DriverAdapter(this,DriverModelList)
            myview.adapter=adapter

        },Response.ErrorListener { err:VolleyError ->
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

        val RequestQueue: RequestQueue = Volley.newRequestQueue(this)
        RequestQueue.add(request)

        ten.setOnClickListener{
            hundred.setBackgroundResource(R.color.colorAccent)
            ten.setBackgroundResource(R.color.colorPrimaryDark)
            try {
                if(DriverModelList.isEmpty()){
                    Toast.makeText(this,"No driver within 10 km try 100 km range",Toast.LENGTH_SHORT).show()
                }
                adapter = DriverAdapter(this, DriverModelList)
                myview.adapter = adapter
            }catch(err:Error){
                Toast.makeText(this,"Wait for data to load",Toast.LENGTH_SHORT).show()
            }
        }
        hundred.setOnClickListener{
            ten.setBackgroundResource(R.color.colorAccent)
            hundred.setBackgroundResource(R.color.colorPrimaryDark)
            try {
                if(DriverModelList2.isEmpty()){
                    Toast.makeText(this,"No driver within 100 km Also",Toast.LENGTH_SHORT).show()
                }
                adapter = DriverAdapter(this, DriverModelList2)
                myview.adapter = adapter
            }catch(err:Error){
                Toast.makeText(this,"Wait for data to load",Toast.LENGTH_SHORT).show()
            }
        }
        btn.setOnClickListener{
            if(DataService.jwttoken!="") {
                val intent = Intent(this, AddDriverActivity::class.java)
                intent.putExtra("lat", lat.toString())
                intent.putExtra("long", long.toString())
                intent.putExtra("JWT", DataService.jwttoken)
                startActivity(intent)
            }
            else{
                loginAlert()
            }
        }



    }
     fun getCityName(lat:Double,long:Double):String{
        var cityname=""
        var geoCoder=Geocoder(this, Locale.getDefault())
        var address= geoCoder.getFromLocation(lat,long,1)
        cityname= address.get(0).locality

        return cityname
    }

    private fun getKilometeres(lat:Double, long:Double, prevLat:Double, prevLong:Double):Double{
        if(prevLat==0.0&&prevLong==0.0){
            return 0.0
        }
        else{
            val PI_RAD:Double=Math.PI/180.0
            val ph1:Double= lat*PI_RAD
            val ph2:Double= prevLat*PI_RAD
            val lam1:Double=long*PI_RAD
            val lam2:Double=prevLong*PI_RAD
            return 6371.01* Math.acos(sin(ph1) * sin(ph2) + cos(ph1) * cos(ph2) * cos(lam2 - lam1))

        }

    }
    fun loginAlert(){
        val alertdialog: AlertDialog.Builder= AlertDialog.Builder(this)
        alertdialog.setTitle("Pls Login to create work as Driver")//for set Title
        alertdialog.setMessage("You need to login in order to use this feature")// for Message
        alertdialog.setIcon(R.drawable.ic_baseline_new_releases_24) // for alert icon
        alertdialog.setPositiveButton("Yes") { dialog, id ->
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        alertdialog.setNegativeButton("Cancel") { dialog, id ->
            // set your desired action here.

        }
        val alert = alertdialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

}