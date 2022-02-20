package com.example.digitalbooks.adaper

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.digitalbooks.helper.Constants
import com.example.digitalbooks.helper.Utils
import com.example.digitalbooks.model.CategoryModel
import com.example.digitalbooks.ui.BuyBookActivity
import example.digitalbooks.R

class AllProductAdapter(val context: Context, val itemList: ArrayList<CategoryModel>) : RecyclerView.Adapter<AllProductAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.all_books_design, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.title.text = item.title
        holder.category.text = item.category
        // change currency
        holder.price.text = "N ${item.price}"
        Utils.loadImage(context,item.book_image.toString(),holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,BuyBookActivity::class.java)
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

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.books_design_title)
        val price: TextView = itemView.findViewById(R.id.books_design_price)
        val category: TextView = itemView.findViewById(R.id.books_design_category)
        val buyNow: TextView = itemView.findViewById(R.id.books_design_buyNowBtn)
        val image: ImageView = itemView.findViewById(R.id.books_design_image)
    }
}