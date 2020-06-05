package com.hari.rideit.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.hari.rideit.R
import com.hari.rideit.model.Category

class CategoryAdapter(context:Context,categories:List<Category>,val listener:CustomClickListener ):BaseAdapter(){
    interface CustomClickListener { fun onClick(position: Int) }
    val context= context
    val categories= categories
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val categoryView: View
        categoryView = LayoutInflater.from(context).inflate(R.layout.category_layout, null)
        val categoryImage: ImageView = categoryView.findViewById(R.id.categoryImage)
        val categoryname: TextView = categoryView.findViewById(R.id.categoryText)
        val category = categories[position]
        categoryname.text = category.title
        val resourceId =
            context.resources.getIdentifier(category.image, "drawable", context.packageName)
        categoryImage.setImageResource(resourceId)


        categoryView.setOnClickListener {
            listener.onClick(position)
        }




        return categoryView
    }



    override fun getItem(position: Int): Any {
        return categories[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
       return categories.count()
    }

}