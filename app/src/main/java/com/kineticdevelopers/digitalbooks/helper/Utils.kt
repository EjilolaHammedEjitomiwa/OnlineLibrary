package com.kineticdevelopers.digitalbooks.helper

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dmax.dialog.SpotsDialog
import es.dmoral.toasty.Toasty

object Utils {
    var loader: AlertDialog? = null

    fun currentUserID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun currentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun database(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun formatTime(timeInMilliseconds: Long): String {
        val formatedDate = TimeAgo.using(timeInMilliseconds)
        return formatedDate
    }

    fun copyValue(context: Activity, value: String) {
        val clipboard: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("", value)
        clipboard.setPrimaryClip(clip)
        Toasty.info(context, "copied", Toast.LENGTH_LONG, true).show()
    }

    fun loadImage(context: Context, src: Any, view: ImageView) {
        try {
            Glide.with(context).load(src).into(view)
        } catch (e: IllegalArgumentException) {
        }

    }


    fun showLoader(context: Context, title: String) {
        loader = SpotsDialog.Builder()
            .setContext(context)
            .setMessage(title)
            .setCancelable(false)
            .build()
            .apply {
            }
        if (loader!!.isShowing) {
            try {
                loader!!.dismiss()
            }catch (e:Exception){}

        }else{
            try {
                loader!!.show()
            }catch (e:Exception){}
        }

    }

    fun dismissLoader() {
        if (loader!!.isShowing) {
            Handler().postDelayed(object : Runnable {
                override fun run() {
                    try {
                        loader!!.cancel()
                    } catch (e: java.lang.Exception) {
                    }
                }
            }, 3000)
        }
    }
}