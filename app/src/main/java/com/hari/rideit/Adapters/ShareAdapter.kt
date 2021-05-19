package com.hari.rideit.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.hari.rideit.R
import com.hari.rideit.model.ShareRideModel

class ShareAdapter(context:Context,shareride:List<ShareRideModel>): BaseAdapter() {
    val context=context
    val shareride= shareride
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

        }

        return shareRideView
    }
}