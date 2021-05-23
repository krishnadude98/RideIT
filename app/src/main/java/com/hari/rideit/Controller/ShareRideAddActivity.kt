package com.hari.rideit.Controller

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.ConnectException
import java.util.*

class ShareRideAddActivity : AppCompatActivity() {
    private val sharedPrefFile = "kotlinsharedpreference"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_ride_add)
        DataService.jwttoken= intent.getStringExtra("JWT")
        MyEditTextDatePicker(this,R.id.registerShareDate)

    }


    class MyEditTextDatePicker(context: Context, editTextViewID: Int) :
        View.OnClickListener, DatePickerDialog.OnDateSetListener {
        var _editText: EditText
        private var _day = 0
        private var _month = 0
        private var _birthYear = 0
        private val _context: Context
        override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            _birthYear = year
            _month = monthOfYear
            _day = dayOfMonth
            updateDisplay()
        }

        override fun onClick(v: View?) {
            val calendar = Calendar.getInstance(TimeZone.getDefault())
            val dialog = DatePickerDialog(
                _context, this,
                calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            )
            dialog.show()
        }

        // updates the date in the birth date EditText
        private fun updateDisplay() {
            _editText.setText(
                StringBuilder() // Month is 0 based so add 1
                    .append(_birthYear).append("/").append(_month + 1).append("/").append(_day)
                    .append(" ")
            )
        }

        init {
            val act = context as Activity
            _editText = act.findViewById<View>(editTextViewID) as EditText
            _editText.setOnClickListener(this)
            _context = context
        }
    }


    fun registerBtnClicked(view: View) {

        val from:EditText = findViewById(R.id.registerShareFrom)
        val to:EditText = findViewById(R.id.rehisterShareTo)
        val date = findViewById<EditText>(R.id.registerShareDate)
        val time:EditText = findViewById(R.id.registerShareTime)
        val vehicleno:EditText = findViewById(R.id.registerShareVehicle)
        val vehiclemodel:EditText = findViewById(R.id.registerShareVehicleModel)
        val eamount:EditText = findViewById(R.id.registerShareMinBid)

        if (TextUtils.isEmpty(from.text.toString()) || TextUtils.isEmpty(to.text.toString()) || TextUtils.isEmpty(
                date.text.toString()
            ) || TextUtils.isEmpty(date.text.toString()) || TextUtils.isEmpty(time.text.toString()) || TextUtils.isEmpty(
                vehicleno.text.toString()
            ) || TextUtils.isEmpty(vehicleno.text.toString()) || TextUtils.isEmpty(vehiclemodel.text.toString()) || TextUtils.isEmpty(
                eamount.text.toString()
            )
        ) {
            Toast.makeText(this, "Pls Fill Complete Details", Toast.LENGTH_SHORT).show()

        }
        else {
            val url = "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/share/add"

            var mdialog: SimpleArcDialog = SimpleArcDialog(this)
            mdialog.setConfiguration(ArcConfiguration(this))
            mdialog.show()
            var email:String=""
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
            val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

            try {

                val request1= object:
                    JsonArrayRequest(Request.Method.GET, url2, null, Response.Listener { res ->

                        val temp:JSONObject=res.getJSONObject(0)
                        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
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











            try {
                val jsonObject = JSONObject()
                jsonObject.put("from", from.text.toString())
                jsonObject.put("to", to.text.toString())

                jsonObject.put("date", date.text.toString())
                jsonObject.put("time", time.text.toString())
                jsonObject.put("vehicleno", vehicleno.text.toString())
                jsonObject.put("vehiclemodel", vehiclemodel.text.toString())
                jsonObject.put("eamount", eamount.text.toString())
                jsonObject.put("accountid", sharedPreferences.getString("id","0"))


                val request =
                    object :
                        JsonObjectRequest(
                            Request.Method.POST,
                            url,
                            jsonObject,
                            Response.Listener { res ->
                                mdialog.hide()
                                val op = res.getString("message")

                                loginAlert(op.toString())

                            },
                            Response.ErrorListener { err: VolleyError ->
                                if (err is NetworkError || err.cause is ConnectException) {
                                    mdialog.hide()
                                    Toast.makeText(
                                        this,
                                        "Pls Check Your Connection And Try Again",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                else if(err is com.android.volley.TimeoutError ){
                                    mdialog.hide()
                                    Toast.makeText(this,"Net Connection Problem2",Toast.LENGTH_LONG).show()
                                }
                                else {
                                    mdialog.hide()
                                    Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }) {

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["auth-token"] = DataService.jwttoken
                            return headers
                        }
                    }
                RequestQueue.add(request)




            } catch (err: Exception) {
                Toast.makeText(this,err.toString(),Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun loginAlert(msg:String) {
        val alertdialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertdialog.setTitle(msg+"y")//for set Title
        alertdialog.setMessage("pls go back to previos window")// for Message
        alertdialog.setIcon(R.drawable.ic_baseline_check_24) // for alert icon
        alertdialog.setPositiveButton("OK!") { dialog, id ->
            val intent = Intent(this, ShareActivity::class.java)
            startActivity(intent)
        }

        val alert = alertdialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}