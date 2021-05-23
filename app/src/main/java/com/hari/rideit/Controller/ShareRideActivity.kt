package com.hari.rideit.Controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ListView
import android.widget.ScrollView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.hari.rideit.Adapters.ShareAdapter
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import com.hari.rideit.model.ShareRideModel
import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import org.json.JSONObject
import java.net.ConnectException

class ShareRideActivity : AppCompatActivity() {
    lateinit var adapter: ShareAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_ride)
        try {
            DataService.jwttoken = intent.getStringExtra("JWT")


        }catch (err:Exception){
            DataService.jwttoken=""

        }


        getData()
    }
    private fun getData(){
        val shareview:ListView= findViewById(R.id.shareRides)
        lateinit var ShareModelList:ArrayList<ShareRideModel>
        ShareModelList= ArrayList<ShareRideModel>()

        val url="http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/share/"

        var mdialog: SimpleArcDialog = SimpleArcDialog(this)
        mdialog.setConfiguration(ArcConfiguration(this))
        mdialog.show()

            val request =
                object : JsonArrayRequest(Request.Method.GET, url, null, Response.Listener { res ->

                    for (i in 0 until res.length()) {
                        val shareride: JSONObject = res.getJSONObject(i)
                        val from = shareride.getString("from")
                        val to = shareride.getString("to")
                        val date = shareride.getString("date")
                        val time = shareride.getString("time")
                        val eamount = shareride.getString("eamount")
                        val vehicleno = shareride.getString("vehicleno")
                        val vehiclemodel = shareride.getString("vehiclemodel")
                        val id = shareride.getString("_id")

                        val shareRide: ShareRideModel = ShareRideModel(
                            from,
                            to,
                            date,
                            time,
                            vehicleno,
                            vehiclemodel,
                            eamount,
                            id
                        )

                        ShareModelList.add(shareRide)

                    }

                    adapter= ShareAdapter(this,ShareModelList,DataService.jwttoken)
                    shareview.adapter= adapter


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
                        Toast.makeText(this, "Check Network and try again", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        mdialog.hide()
                        Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
                    }
                }) {}
        mdialog.hide()
        val RequestQueue: RequestQueue = Volley.newRequestQueue(this)
        RequestQueue.add(request)

        }


}