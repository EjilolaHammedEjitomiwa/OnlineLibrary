package com.example.digitalbooks.adaper

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

import com.example.digitalbooks.model.DownloadedBookModel
import com.rajat.pdfviewer.PdfViewerActivity
import example.digitalbooks.R

class DownloadedBookAdapter (val context: Context, val itemList: ArrayList<DownloadedBookModel>) : RecyclerView.Adapter<DownloadedBookAdapter.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.downloaded_book_design, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        holder.title.text = item.title

        holder.viewButton.setOnClickListener {
            val title = "${item.title}${DateFormat.format("EE, dd MMM yyyy hh-mm-ss a", System.currentTimeMillis())})"
            context.startActivity(PdfViewerActivity.buildIntent(context, item.link, false, title, "DigitalBooks/BooksPurchased", enableDownload = true))
        }

    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.downloaded_book_design_title)
        val viewButton: Button = itemView.findViewById(R.id.downloaded_book_design_viewBtn)
    }
}