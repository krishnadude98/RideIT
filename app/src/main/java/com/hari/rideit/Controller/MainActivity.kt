package com.hari.rideit.Controller

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationView
import com.hari.rideit.Adapters.CategoryAdapter
import com.hari.rideit.Adapters.ViewPageAdapter
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.Exception

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener, CategoryAdapter.CustomClickListener{


    lateinit var adapter:CategoryAdapter
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var gridview:GridView
    internal lateinit var viewPager:ViewPager
    private lateinit var locationManager: LocationManager
    val context= this
    lateinit var jwt: String
    private val locationPermissionCode = 2
    private var PERMISSION_ID=1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest:LocationRequest
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //animation loading
        val ttb= AnimationUtils.loadAnimation(this,R.anim.ttb)

        val geadert= findViewById(R.id.home_conentmain) as TextView
        val cattext= findViewById(R.id.contentGridView) as GridView



        //start animation
        geadert.startAnimation(ttb)
        cattext.startAnimation(ttb)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        gridview= findViewById(R.id.contentGridView)


        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        adapter=  CategoryAdapter(this,DataService.categories,this)
        gridview.adapter= adapter


        //added view pager
        viewPager=findViewById<View>(R.id.viewPager) as ViewPager
        val adapter= ViewPageAdapter(this)
        viewPager.adapter=adapter
        if( intent.getStringExtra("JWT")!=null){
            try{

                DataService.jwttoken= intent.getStringExtra("JWT")

            }catch (err:Exception){

            }

            val navView:NavigationView= findViewById(R.id.nav_view)
            navView.menu.clear()
            navView.inflateMenu(R.menu.nav_menu_login)
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
                val navView:NavigationView= findViewById(R.id.nav_view)
                navView.menu.clear()
                navView.inflateMenu(R.menu.nav_menu_login)

            }catch (e:Exception){

                val navView:NavigationView= findViewById(R.id.nav_view)
                navView.menu.clear()
                navView.inflateMenu(R.menu.nav_menu)
            }


        }
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)




    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation(){
        if(CheckPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task->
                    var location:Location?=task.result
                    if(location==null){
                        getNewLocation()

                    }
                    else{
                        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
                            Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
                        editor.putString("lat",location.latitude.toString())
                        editor.putString("long",location.longitude.toString())
                        editor.apply()
                        editor.commit()

                    }
                }
            }
            else{
                Toast.makeText(this,"Pls turn on GPS",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            RequestPermission()
        }
    }
    @SuppressLint("MissingPermission")
    private fun getNewLocation(){
        locationRequest= LocationRequest()
        locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=0
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=2
        fusedLocationProviderClient!!.requestLocationUpdates(locationRequest,locationCallback,
            Looper.myLooper())
    }
    private val locationCallback= object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastlocation=p0.lastLocation
            val sharedPreferences: SharedPreferences = this@MainActivity.getSharedPreferences(sharedPrefFile,
                Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor =  sharedPreferences.edit()
            editor.putString("lat",lastlocation.latitude.toString())
            editor.putString("long",lastlocation.longitude.toString())
            editor.apply()
            editor.commit()

        }
    }

    override fun onClick(position:Int) {

        if(position==0){
            val intent= Intent(this,ShareActivity::class.java)
            if(DataService.jwttoken!=""){
                intent.putExtra("JWT",DataService.jwttoken)

            }

            startActivity(intent)
        }
        else if(position==1){
            Toast.makeText(this,"Akhil part not implemented ",Toast.LENGTH_SHORT).show()
        }
        else if(position==2){
            getLastLocation()
            val intent= Intent(this,RentRiderActivity::class.java)
            val sharedPreferences: SharedPreferences = this@MainActivity.getSharedPreferences(sharedPrefFile,
                Context.MODE_PRIVATE)
            val lat=sharedPreferences.getString("lat","0")
            val long= sharedPreferences.getString("long","0")

            if(lat!="0"||long!="0") {
                if(isLocationEnabled()){
                    intent.putExtra("lat", lat)
                    intent.putExtra("long", long)
                    val editor:SharedPreferences.Editor= sharedPreferences.edit()
                    editor.remove("lat")
                    editor.remove("lat")
                    editor.apply()
                    editor.commit()
                    if(DataService.jwttoken!=""){
                        intent.putExtra("JWT",DataService.jwttoken)

                    }
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Pls Turn On Gps",Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(this,"Pls Allow location permission in order to use this feature of Rideit",Toast.LENGTH_SHORT).show()
            }


        }
        else if(position==3){
            Toast.makeText(this,"Postion 3 clicked ",Toast.LENGTH_SHORT).show()
        }
    }
    private fun CheckPermission():Boolean{
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
        ){
            return true

        }

        return false
    }
    private fun  RequestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }

    private fun isLocationEnabled():Boolean{

        var locationManager= getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode== PERMISSION_ID){
            if(grantResults.isNotEmpty()&&grantResults[0]==  PackageManager.PERMISSION_GRANTED){
                Log.d("Debug","You Have Permission")
            }
            else{
                Toast.makeText(this,"Pls Allow location permission in setting for rideit to use this",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent= Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_Signup -> {
                val intent= Intent(this,SignupActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_Share -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_Rate -> {
                Toast.makeText(this, "Rate clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                this.deleteFile("mytextfile.txt")
                DataService.jwttoken=""
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
            }

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
