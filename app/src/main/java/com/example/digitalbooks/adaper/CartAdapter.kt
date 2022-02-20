package com.example.digitalbooks.adaper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.digitalbooks.helper.Constants
import com.example.digitalbooks.helper.Utils
import com.example.digitalbooks.model.CartModel
import com.example.digitalbooks.ui.CartActivity
import example.digitalbooks.R
import kotlinx.android.synthetic.main.fragment_profile.*


class CartAdapter (val context: Context, val itemList: ArrayList<CartModel>) : RecyclerView.Adapter<CartAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_design, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.title.text = item.name
        holder.price.text = item.price
        holder.price.text = "N ${item.price}"

        Utils.loadImage(context,item.image.toString(),holder.image)
        //holder.incrementer.number = item.quantity.toString()
        holder.incrementer.setNumber(item.quantity.toString(),true)
        holder.incrementer.setOnValueChangeListener { view, oldValue, newValue ->
            val map = HashMap<String,Any>()
            map["quantity"] = newValue

            Utils.database()
                .collection(Constants.carts)
                .document(Utils.currentUserID())
                .collection(Utils.currentUserID())
                .document(item.id.toString())
                .update(map)
            item.quantity = newValue
            if (context is CartActivity){
                context.calculatePrice()
            }
        }

        holder.remove.setOnClickListener {
            if (context is CartActivity){
                context.removeFromCart(item.id.toString())
            }
        }


    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.cart_design_title)
        val price: TextView = itemView.findViewById(R.id.cart_design_price)
        val remove: TextView = itemView.findViewById(R.id.cart_design_remove)
        val incrementer: ElegantNumberButton = itemView.findViewById(R.id.cart_design_incrementer)
        val image: ImageView = itemView.findViewById(R.id.cart_design_image)
    }
}