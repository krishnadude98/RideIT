package com.hari.rideit.Controller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.hari.rideit.Adapters.BidderAdd
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import com.hari.rideit.model.BidderModel
import com.hari.rideit.model.ShareRideModel
import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import org.json.JSONObject
import java.net.ConnectException
import java.util.HashMap

class AdDetailsActivity : AppCompatActivity() {
    lateinit var id:String

    lateinit var adapter:BidderAdd
    lateinit var ShareBidders:java.util.ArrayList<BidderModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad_details)
        val from= findViewById<TextView>(R.id.myadfrom)
        val to = findViewById<TextView>(R.id.myadto)
        val date= findViewById<TextView>(R.id.myadDate)
        val time= findViewById<TextView>(R.id.myadtime)
        val vno=findViewById<TextView>(R.id.myadno)
        val vmodel= findViewById<TextView>(R.id.myadmodel)
        val emount= findViewById<TextView>(R.id.myadeamount)
        from.text= intent.getStringExtra("from")
        to.text= intent.getStringExtra("to")
        date.text= intent.getStringExtra("date")
        time.text= intent.getStringExtra("time")
        vno.text= intent.getStringExtra("vehicleno")
        vmodel.text=intent.getStringExtra("vehiclemodel")
        emount.text= intent.getStringExtra("eamount")

        id=intent.getStringExtra("id")

        getBidder(id)
    }
    private fun getBidder(id:String){
        val url= "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/share/bidders/${id}"
        val mylist:ListView=findViewById(R.id.bidderListView)

        ShareBidders= ArrayList<BidderModel>()
        var mdialog: SimpleArcDialog = SimpleArcDialog(this)
        mdialog.setConfiguration(ArcConfiguration(this))
        mdialog.show()
        val request= object:JsonArrayRequest(Request.Method.GET,url,null,Response.Listener { res->
            mdialog.hide()
            if(res.length()==0){
                val text=findViewById<TextView>(R.id.noBiddders)
                text.visibility=View.VISIBLE

            }
            else{
                for(i in 0 until res.length()){
                    val bidder:JSONObject= res.getJSONObject(i)
                    val biddername=bidder.getString("name")
                    val bidamount=bidder.getString("bid")
                    val number=bidder.getString("userid")
                    val bidde:BidderModel= BidderModel(biddername,bidamount,number)
                    ShareBidders.add(bidde)
                }
                adapter=BidderAdd(this,ShareBidders)
                mylist.adapter=adapter
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
        val RequestQueue: RequestQueue = Volley.newRequestQueue(this)
        RequestQueue.add(request)
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            loginAlert(id)
        }

    }

    fun loginAlert(adid:String) {
        val alertdialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertdialog.setTitle("Are You Sure? You want to delete your AD.")//for set Title
        alertdialog.setMessage("Once deleted the process cannot be undone")// for Message
        alertdialog.setIcon(R.drawable.ic_baseline_delete_forever_24) // for alert icon

        alertdialog.setPositiveButton("OK!") { dialog, id ->
            val url= "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/share/${adid}"
            val jwt=intent.getStringExtra("JWT")
            var mdialog: SimpleArcDialog = SimpleArcDialog(this)
            mdialog.setConfiguration(ArcConfiguration(this))
            mdialog.show()

            val req= object:JsonObjectRequest(Request.Method.DELETE,url,null,Response.Listener { res->
                mdialog.hide()
                Toast.makeText(this,res.getString("messgae"),Toast.LENGTH_LONG).show()
                val intent = Intent(this, ShareActivity::class.java)
                startActivity(intent)

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
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["auth-token"] = jwt
                    return headers
                }
            }
            val RequestQueue: RequestQueue = Volley.newRequestQueue(this)
            RequestQueue.add(req)

        }

        val alert = alertdialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

}