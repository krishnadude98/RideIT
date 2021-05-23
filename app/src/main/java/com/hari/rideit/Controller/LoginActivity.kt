package com.hari.rideit.Controller

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import com.leo.simplearcloader.ArcConfiguration
import com.leo.simplearcloader.SimpleArcDialog
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.net.ConnectException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val email:EditText=findViewById(R.id.userNameTxt)
        val password:EditText= findViewById(R.id.passwordTxt)


    }

    fun LoginBtnClicked(view: View) {
        val email:EditText=findViewById(R.id.userNameTxt)
        val password:EditText= findViewById(R.id.passwordTxt)
        if(TextUtils.isEmpty(email.text.toString())){
            Toast.makeText(this, "Pls Enter Email", Toast.LENGTH_SHORT).show()
            return
        }
        else if(TextUtils.isEmpty(password.text.toString())) {
            Toast.makeText(this, "Pls Enter Password", Toast.LENGTH_SHORT).show()
            return
        }
        val url= "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/account/login"

        var mdialog: SimpleArcDialog = SimpleArcDialog(this)
        mdialog.setConfiguration(ArcConfiguration(this))
        mdialog.show()
        try {
            val jsonObject = JSONObject()
            jsonObject.put("email", email.text.toString())
            jsonObject.put("password", password.text.toString())
            val request= object :JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                Response.Listener { res ->



                    try {



                        val flout:FileOutputStream= openFileOutput("email.txt", MODE_PRIVATE)
                        val owriter= OutputStreamWriter(flout)
                        owriter.write(email.text.toString())
                        owriter.close()
                        email.text.clear()
                        password.text.clear()
                        mdialog.hide()
                        //display file saved message
                        Toast.makeText(
                            baseContext, "Logged in Sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent= Intent(this,MainActivity::class.java)
                        intent.putExtra("JWT",res.getString("message"))
                        startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }




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
                        Toast.makeText(this,"Check Network and try again",Toast.LENGTH_LONG).show()
                    }
                    else if(err is com.android.volley.ClientError ){
                        mdialog.hide()
                        Toast.makeText(this,"You don't have a account with us pls sign up",Toast.LENGTH_LONG).show()
                    }
                    else {
                        mdialog.hide()
                        Toast.makeText(this, err.toString(), Toast.LENGTH_SHORT).show()
                    }
                }){}
            val RequestQueue: RequestQueue = Volley.newRequestQueue(this)
            RequestQueue.add(request)

        }catch (err: Exception){
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show()
        }



    }



    // Read text from file
//    fun ReadBtn(v: View?) {
//        //reading text from file
//        try {
//            val fileIn: FileInputStream = openFileInput("mytextfile.txt")
//            val InputRead = InputStreamReader(fileIn)
//            val inputBuffer = CharArray(READ_BLOCK_SIZE)
//            var s: String? = ""
//            var charRead: Int
//            while (InputRead.read(inputBuffer).also({ charRead = it }) > 0) {
//                // char to string conversion
//                val readstring = String(inputBuffer, 0, charRead)
//                s += readstring
//            }
//            InputRead.close()
//            textmsg.setText(s)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
}