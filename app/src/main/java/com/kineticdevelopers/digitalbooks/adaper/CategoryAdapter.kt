package com.kineticdevelopers.digitalbooks.adaper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.material.button.MaterialButton
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.helper.Constants
import com.kineticdevelopers.digitalbooks.helper.Utils
import com.kineticdevelopers.digitalbooks.model.CategoryModel
import com.kineticdevelopers.digitalbooks.ui.BuyBookActivity
import com.kineticdevelopers.digitalbooks.ui.CategoryProduct

class CategoryAdapter(val context: Context, val itemList: ArrayList<CategoryModel>, val type : String) : RecyclerView.Adapter<CategoryAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.categories_design, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        when(type){
            "categories" ->{
                holder.title.text = item.name
                Utils.loadImage(context,item.icon_url.toString(),holder.image)
            }
            "recommended" -> {
                holder.title.text = item.title
                Utils.loadImage(context,item.book_image.toString(),holder.image)
            }
        }

        holder.itemView.setOnClickListener {
            when(type){
                "categories" ->{
                    val intent =  Intent(context,CategoryProduct::class.java)
                    intent.putExtra(Constants.category,item.name)
                    context.startActivity(intent)
                }
                "recommended" -> {
                    val intent = Intent(context, BuyBookActivity::class.java)
                    intent.putExtra(Constants.image,item.book_image)
                    intent.putExtra(Constants.title,item.title)
                    intent.putExtra(Constants.description,item.description)
                    intent.putExtra(Constants.price,item.price)
                    intent.putExtra(Constants.link,item.link)
                    intent.putExtra(Constants.bookID,item.bookID)
                    context.startActivity(intent)
                    Animatoo.animateSwipeLeft(context)
                }
            }

        }


    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.category_design_title)
        val image: ImageView = itemView.findViewById(R.id.category_design_image)
    }
}