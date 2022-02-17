package com.kineticdevelopers.digitalbooks.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.kineticdevelopers.digitalbooks.R
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        admin_uploadSliderImages.setOnClickListener {
            startActivity(Intent(this,UploadSliderImages::class.java))
            Animatoo.animateSwipeLeft(this)
        }

        admin_addCategory.setOnClickListener {
            startActivity(Intent(this,AddCategory::class.java))
            Animatoo.animateSwipeLeft(this)
        }
    }
}