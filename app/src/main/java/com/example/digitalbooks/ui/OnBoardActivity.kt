package com.example.digitalbooks.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.digitalbooks.helper.Utils
import example.digitalbooks.R
import kotlinx.android.synthetic.main.activity_on_board.*

class OnBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_board)

        onboard_activity_createAccountBtn.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
            Animatoo.animateSwipeLeft(this)
        }
        onboard_activity_loginBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            Animatoo.animateSwipeLeft(this)
        }

    }

    override fun onStart() {
        super.onStart()
        checkIsLoggedIn()
    }

    private fun checkIsLoggedIn() {
        if (Utils.currentUser() != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            Animatoo.animateFade(this)
        }
    }
}