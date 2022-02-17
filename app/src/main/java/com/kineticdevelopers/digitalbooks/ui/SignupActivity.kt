package com.kineticdevelopers.digitalbooks.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.helper.Utils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*
import kotlin.collections.HashMap

class SignupActivity : AppCompatActivity() {
    var role = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signup_activity_selectRole.setItems("Reader","Author")
        signup_activity_selectRole.setOnItemSelectedListener { view, position, id, item ->
            role = item.toString().lowercase(Locale.getDefault())
        }

        signup_activity_alreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        signup_activity_createAccountBtn.setOnClickListener {
            val fullname = signup_activity_fullname.text.toString()
            val email = signup_activity_email.text.toString()
            val number = signup_activity_number.text.toString()
            val password = signup_activity_password.text.toString()

            when{
                TextUtils.isEmpty(fullname) -> Toasty.info(this,"Fullname is required",Toasty.LENGTH_LONG).show()
                TextUtils.isEmpty(email) -> Toasty.info(this,"email is required",Toasty.LENGTH_LONG).show()
                TextUtils.isEmpty(number) -> Toasty.info(this,"number is required",Toasty.LENGTH_LONG).show()
                TextUtils.isEmpty(password) -> Toasty.info(this,"password is required",Toasty.LENGTH_LONG).show()
                password.length < 6 -> Toasty.info(this,"Password must be more than 6 characters",Toasty.LENGTH_LONG).show()
                role == "" -> Toasty.info(this,"Please select role",Toasty.LENGTH_LONG).show()
                else ->{
                    Utils.showLoader(this,"Please wait...")
                    createAccount(fullname,email,number,password)
                }
            }
        }
    }

    private fun createAccount(fullname: String, email: String, number: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                saveUserInfoToDatabase(fullname, number, email, it.user!!.uid)
            }.addOnFailureListener {
                Utils.dismissLoader()
                Toasty.error(this, "error: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun saveUserInfoToDatabase(fullname: String, number: String, email: String, userID: String) {

        val userMap = HashMap<String,Any>()

        userMap["fullname"] = fullname
        userMap["number"] = number
        userMap["email"] = email
        userMap["user_id"] = userID
        userMap["role"] = role

        Utils.database()
            .collection("users")
            .document(Utils.currentUserID())
            .set(userMap)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Utils.dismissLoader()
                    Toasty.success(this,"Registration Successful",Toasty.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }else{
                    Utils.dismissLoader()
                    Toasty.error(this,"Error occur creating account",Toasty.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().currentUser!!.delete()
                }
        }

    }
}