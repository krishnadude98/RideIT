package com.hari.rideit.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.hari.rideit.R
import com.hari.rideit.model.BidderModel
import kotlinx.android.synthetic.main.viewnum_layout.view.*
import java.math.BigDecimal

class BidderAdd(context: Context,bidders:List<BidderModel>):BaseAdapter() {
    val context= context
    val bidders=bidders
    override fun getCount(): Int {
        return bidders.count()
    }

    override fun getItem(position: Int): Any {
        return bidders[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val BidderView= LayoutInflater.from(context).inflate(R.layout.bidders_layout,null)
        val bidder= bidders[position]
        val name= BidderView.findViewById<TextView>(R.id.BidderName)
        val offer= BidderView.findViewById<TextView>(R.id.BidderOffer)
        name.text= bidder.name
        offer.text= bidder.bid
        val bidacceptBtn= BidderView.findViewById<Button>(R.id.bidAcceptBtn)
        bidacceptBtn.setOnClickListener(){
            noAdAlert(bidder.phoneno,bidder.name)
        }


        return BidderView

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