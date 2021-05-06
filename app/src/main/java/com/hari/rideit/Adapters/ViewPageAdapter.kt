package com.hari.rideit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hari.rideit.Controller.MainActivity
import com.hari.rideit.R

class ViewPageAdapter(private val context: Context):PagerAdapter() {

    private var layoutInflator:LayoutInflater?=null
    private val images= arrayOf(R.drawable.cat1,R.drawable.cat2,R.drawable.cat3)
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view=== `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflator= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v= layoutInflator!!.inflate(R.layout.custom_layout,null)
        val image=v.findViewById<View>(R.id.image_view) as ImageView
        image.setImageResource(images[position])
        image.setOnClickListener {

                Toast.makeText(context,"At Postion $position",Toast.LENGTH_LONG).show()

        }
        val vp= container as ViewPager
        vp.addView(v,0)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp= container as ViewPager
        val v= `object` as View
        vp.removeView(v)

    }

}