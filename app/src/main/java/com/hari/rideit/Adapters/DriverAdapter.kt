package com.hari.rideit.Adapters

import android.content.Context
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.hari.rideit.R

import com.hari.rideit.model.DriverModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.viewnum_layout.view.*

class DriverAdapter(context:Context,rider:List<DriverModel>):BaseAdapter() {
    var context= context
    var rider=rider
    override fun getCount(): Int {
        return rider.count()
    }

    override fun getItem(p0: Int): Any {
       return rider[p0]

    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val myview:View
        myview= LayoutInflater.from(context).inflate(R.layout.driver_layout,null)
        val name= myview.findViewById<TextView>(R.id.driverName)
        val location= myview.findViewById<TextView>(R.id.driverLocation)
        val licenseno= myview.findViewById<TextView>(R.id.driverLicensenumber)
        val licensetype= myview.findViewById<TextView>(R.id.driverLicenseType)
        val image= myview.findViewById<ImageView>(R.id.driverImage)
        val reviewBtn=myview.findViewById<Button>(R.id.driverReviewBtn)
        val driverContactBtn= myview.findViewById<Button>(R.id.driverContactButton)
        val driver:DriverModel=rider[position]
        name.text= driver.name
        location.text=driver.location
        licenseno.text=driver.licenseno
        licensetype.text=driver.licensetype
        Picasso.get().load(driver.imageid).into(image)
        reviewBtn.setOnClickListener{
            Toast.makeText(context,"Review Btn Clicked",Toast.LENGTH_SHORT).show()
        }
        driverContactBtn.setOnClickListener{
            noAdAlert(driver.contact,driver.name)
        }

        return myview
    }
    fun noAdAlert(phno:String,na:String){
        val alertdialog: AlertDialog.Builder= AlertDialog.Builder(context)
        alertdialog.setTitle("You will be given the contact number of the bidder ")//for set Title
        alertdialog.setMessage("Any type of spam leads to account ban ")// for Message
        alertdialog.setIcon(R.drawable.ic_baseline_new_releases_24) // for alert icon
        alertdialog.setPositiveButton("OK!") { dialog, id ->
            val myview= LayoutInflater.from(context).inflate(R.layout.viewnum_layout,null)
            val mbuilder= android.app.AlertDialog.Builder(context).setView(myview).setTitle("Make A Bid")
            val maalertDialog= mbuilder.show()
            val name=myview.findViewById<TextView>(R.id.nameB)
            val bno=myview.findViewById<TextView>(R.id.bno)

            name.text= na
            bno.text=phno
            Linkify.addLinks(bno,Linkify.PHONE_NUMBERS)
            myview.okeyClicked.setOnClickListener {
                maalertDialog.dismiss()
            }

        }

        val alert = alertdialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}