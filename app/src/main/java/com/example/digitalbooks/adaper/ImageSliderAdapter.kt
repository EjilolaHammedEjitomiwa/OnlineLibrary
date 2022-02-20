package com.example.digitalbooks.adaper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.digitalbooks.model.ImageListModel
import com.smarteist.autoimageslider.SliderViewAdapter
import example.digitalbooks.R

class ImageSliderAdapter (val context: Context, var itemList: ArrayList<ImageListModel>) : SliderViewAdapter<ImageSliderAdapter.SliderAdapterVH>() {
    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent.context).inflate(R.layout.d_home_slider_design, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem = itemList[position]
        viewHolder.image.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(context).load(sliderItem.image_url).into(viewHolder.image)
    }

    override fun getCount(): Int {
        return itemList.size
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.home_slider_design_image)

    }

}