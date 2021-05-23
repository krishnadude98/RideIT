package com.hari.rideit.Adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hari.rideit.Controller.ShareActivity
import com.hari.rideit.R
import com.hari.rideit.Services.DataService
import com.hari.rideit.model.ShareRideModel
import kotlinx.android.synthetic.main.make_bid_layout.view.*
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.ConnectException
import java.util.HashMap

class ShareAdapter(context:Context,shareride:List<ShareRideModel>,jwttoken:String): BaseAdapter() {
    val context=context
    val shareride= shareride
    val jwttoken=jwttoken
    lateinit var name:String
    override fun getCount(): Int {
        return shareride.count()
    }

    override fun getItem(p0: Int): Any {
        return  shareride[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val shareRideView:View
        shareRideView= LayoutInflater.from(context).inflate(R.layout.share_layout,null)
        val from:TextView = shareRideView.findViewById(R.id.shareFrom)
        val to:TextView= shareRideView.findViewById(R.id.shareTo)
        val date:TextView= shareRideView.findViewById(R.id.shareDate)
        val minbid:TextView= shareRideView.findViewById(R.id.shareMinBid)
        val time:TextView= shareRideView.findViewById(R.id.shareTime)
        val vehicleno:TextView= shareRideView.findViewById(R.id.shareVehicleNo)
        val reviewBtn:Button= shareRideView.findViewById(R.id.shareReviewBtn)
        val bidsBtn:Button= shareRideView.findViewById(R.id.shareViewBidsBtn)
        val viewMoreBtn:Button= shareRideView.findViewById(R.id.shareViewMoreBtn)
        val model:TextView= shareRideView.findViewById(R.id.shareVehicleModel)
        val bidbtnadd:Button= shareRideView.findViewById(R.id.bidsBtnAdd)
        val ride=shareride[position]
        from.text=ride.from
        to.text= ride.to
        var tem=""
        val dae= ride.date
        for(i in 0..9){
            tem= tem+dae[i]
        }
        date.text=tem

        minbid.text= ride.emaount
        viewMoreBtn.setOnClickListener(){
            val viewmore:View= shareRideView.findViewById(R.id.view_more)
            viewmore.visibility= View.VISIBLE
            time.text=ride.time
            vehicleno.text= ride.vehicleno
            model.text=ride.vehiclemodel
            viewMoreBtn.text="View Less"
            viewMoreBtn.setOnClickListener {
                viewmore.visibility= View.GONE
                viewMoreBtn.text="View more"

                viewMoreBtn.setOnClickListener {
                    viewmore.visibility= View.VISIBLE
                    viewMoreBtn.text="View Less"
                    viewMoreBtn.setOnClickListener {
                        viewmore.visibility= View.GONE
                        viewMoreBtn.text="View More"

                    }
                }
            }
        }
        reviewBtn.setOnClickListener(){
            Toast.makeText(context,"Review Btn Clicked",Toast.LENGTH_SHORT).show()
        }
        bidsBtn.setOnClickListener(){
            Toast.makeText(context,"Bids Btn Clicked",Toast.LENGTH_SHORT).show()
        }

        bidbtnadd.setOnClickListener(){
            val myview= LayoutInflater.from(context).inflate(R.layout.make_bid_layout,null)
            val mbuilder= AlertDialog.Builder(context).setView(myview).setTitle("Make A Bid")
            val maalertDialog= mbuilder.show()


            myview.bidBtnCliked.setOnClickListener {

                val amount=myview.bidAmt.text.toString()
                val phone= myview.num.text.toString()
                if(TextUtils.isEmpty(amount)||TextUtils.isEmpty(phone)) {

                    Toast.makeText(context,"Pls Fill Both Fields!",Toast.LENGTH_LONG)
                }
                else{
                    maalertDialog.dismiss()
                    val url =
                        "http://ec2-3-19-240-6.us-east-2.compute.amazonaws.com:3005/v1/share/bidders/add/${ride.id}"

                    try {
                        val jsonObject = JSONObject()
                        jsonObject.put("bid", amount.toInt())
                        jsonObject.put("userid",phone)

                        try{
                            val READ_BLOCK_SIZE=100
                            val fileIn: FileInputStream =context.openFileInput("name.txt")
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
                            name= s.toString()

                        }catch (err:Exception){
                            Toast.makeText(context,err.toString(),Toast.LENGTH_SHORT).show()
                            name=""
                        }
                        if(name==""){
                            Toast.makeText(context,"Pls Login to make a Bid",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            if(jwttoken!="") {
                                jsonObject.put("name", name)
                                val req = object : JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    jsonObject,
                                    Response.Listener { res ->


                                    },
                                    Response.ErrorListener { err: VolleyError ->
                                        if (err is NetworkError || err.cause is ConnectException) {

                                            Toast.makeText(
                                                context,
                                                "Pls Check Your Connection And Try Again",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {

                                            Toast.makeText(
                                                context,
                                                err.toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }) {
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["auth-token"] = jwttoken
                                        return headers
                                    }
                                }
                                val RequestQueue: RequestQueue = Volley.newRequestQueue(context)
                                RequestQueue.add(req)
                            }
                            else {
                                Toast.makeText(context,"PLS Login To Make A bid ",Toast.LENGTH_SHORT).show()
                            }
                        }



                    } catch (err: Exception) {
                        Toast.makeText(context, err.toString(), Toast.LENGTH_SHORT).show()
                    }

                }



            }
        }


        return shareRideView
    }
}