package com.kineticdevelopers.digitalbooks.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.helper.Utils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_activity_dontHaveAnAccount.setOnClickListener {
            startActivity(Intent(this,SignupActivity::class.java))
            Animatoo.animateSwipeLeft(this)
        }

        login_activity_loginBtn.setOnClickListener {
            val email = login_activity_email.text.toString()
            val password = login_activity_password.text.toString()

            when{
                TextUtils.isEmpty(email) -> Toasty.info(this,"Enter your email",Toasty.LENGTH_LONG).show()
                TextUtils.isEmpty(password) -> Toasty.info(this,"Enter your password",Toasty.LENGTH_LONG).show()
                else -> {
                    Utils.showLoader(this,"Please wait...")
                    loginUser(email,password)
                }
            }
        }

    }

    private fun loginUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Utils.dismissLoader()
                    Toasty.success(this,"Login Successful",Toasty.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }else{
                    Utils.dismissLoader()
                    Toasty.error(this,it.exception!!.message.toString(),Toasty.LENGTH_LONG).show()
                }
            }

    }
}