package com.hari.rideit.Controller

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hari.rideit.R
import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import kotlinx.android.synthetic.main.activity_signup2.view.*
import org.json.JSONObject
import java.net.ConnectException
import java.util.*


class SignupActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)
        val roll= AnimationUtils.loadAnimation(this, R.anim.bounce)
        val logos= findViewById(R.id.logo_signup) as ImageView
        logos.startAnimation(roll)

        MyEditTextDatePicker(this, R.id.Birthday)



        }



    class MyEditTextDatePicker(context: Context, editTextViewID: Int) :
        View.OnClickListener, OnDateSetListener {
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

    fun signupClicked(view: View) {
        val email:EditText= findViewById(R.id.email)
        val name= findViewById<EditText>(R.id.name)
        val pass1:EditText= findViewById(R.id.pass1)
        val pass2:EditText= findViewById(R.id.pass2)
        val date=findViewById<EditText>(R.id.Birthday)
        if(pass1.text.toString()!=pass2.text.toString()){
            Toast.makeText(this, "Password do not match try again", Toast.LENGTH_SHORT).show()
            return
        }
        if(TextUtils.isEmpty(email.text.toString())||TextUtils.isEmpty(pass1.text.toString())||TextUtils.isEmpty(
                name.text.toString()
            )){
            Toast.makeText(this, "Pls Fill Complete Details", Toast.LENGTH_SHORT).show()
            return
        }
        val url= "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/account/register"
        var mdialog: SimpleArcDialog = SimpleArcDialog(this)
        mdialog.setConfiguration(ArcConfiguration(this))
        mdialog.show()
        try {
            val jsonObject = JSONObject()
            jsonObject.put("email",email.text.toString())
            jsonObject.put("password",pass1.text.toString())
            jsonObject.put("name",name.text.toString())
            jsonObject.put("date",date.text.toString())

            val request =
                object :JsonObjectRequest(Request.Method.POST,url,jsonObject,Response.Listener {res->
                    mdialog.hide()
                    Toast.makeText(this,res.getJSONObject("message").toString()+ " Pls Login",Toast.LENGTH_SHORT).show()
                    email.text.clear()
                    name.text.clear()
                    pass1.text.clear()
                    pass2.text.clear()
                    date.text.clear()
                }, Response.ErrorListener { err:VolleyError->
                    if(err is NetworkError || err.cause is ConnectException){
                        mdialog.hide()
                        Toast.makeText(this,"Pls Check Your Connection And Try Again",Toast.LENGTH_LONG).show()
                    }
                    else if(err is com.android.volley.ClientError ){
                        mdialog.hide()
                        Toast.makeText(this,"Email Already Exist pls login or try other email",Toast.LENGTH_LONG).show()
                    }
                    else{
                        mdialog.hide()
                        Toast.makeText(this,err.toString(),Toast.LENGTH_SHORT).show()
                    }
                }) {}



            val RequestQueue: RequestQueue = Volley.newRequestQueue(this)
            RequestQueue.add(request)
        }catch (err: Exception){
            Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
        }


    }

}


