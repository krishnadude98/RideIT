package com.hari.rideit.Controller

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
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
    val context= this
    lateinit var jwt: String
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
        if( intent.getStringExtra("JWT")==null){
            try{
                val READ_BLOCK_SIZE=100
                val fileIn: FileInputStream = openFileInput("mytextfile.txt")
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
                DataService.jwttoken= s.toString()
                val navView:NavigationView= findViewById(R.id.nav_view)
                navView.menu.clear()
                navView.inflateMenu(R.menu.nav_menu_login)
            }catch (err:Exception){
                val navView:NavigationView= findViewById(R.id.nav_view)
                navView.menu.clear()
                navView.inflateMenu(R.menu.nav_menu)
            }


        }


        else{
            try {



                DataService.jwttoken=intent.getStringExtra("JWT")
                val navView:NavigationView= findViewById(R.id.nav_view)
                navView.menu.clear()
                navView.inflateMenu(R.menu.nav_menu_login)

            }catch (e:Exception){

                val navView:NavigationView= findViewById(R.id.nav_view)
                navView.menu.clear()
                navView.inflateMenu(R.menu.nav_menu)
            }


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
            Toast.makeText(this,"Postion 1 clicked ",Toast.LENGTH_SHORT).show()
        }
        else if(position==2){
            Toast.makeText(this,"Postion 2 clicked ",Toast.LENGTH_SHORT).show()
        }
        else if(position==3){
            Toast.makeText(this,"Postion 3 clicked ",Toast.LENGTH_SHORT).show()
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
