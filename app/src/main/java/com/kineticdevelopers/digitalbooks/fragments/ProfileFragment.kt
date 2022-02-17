package com.kineticdevelopers.digitalbooks.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.helper.Constants
import com.kineticdevelopers.digitalbooks.helper.Utils
import com.kineticdevelopers.digitalbooks.model.UsersModel
import com.kineticdevelopers.digitalbooks.ui.AddBookActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.lang.NullPointerException

class ProfileFragment :Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.profile_addBtn.setOnClickListener {
            startActivity(Intent(requireContext(),AddBookActivity::class.java))
        }

        loadProfile()
        return view
    }

    private fun loadProfile() {
        Utils.showLoader(requireContext(),"Loading profile...")
        Utils.database().collection(Constants.users)
            .document(Utils.currentUserID())
            .get()
            .addOnSuccessListener {
                if (it.exists()){
                    val user = it.toObject(UsersModel::class.java)
                    try {
                        profile_accountType.text = user!!.role
                        profile_email.text = user.email
                        profile_name.text = user.fullname
                        profile_number.text = user.number

                        if (user.role == Constants.author){
                            profile_addBtn.visibility = View.VISIBLE
                        }
                        Utils.dismissLoader()
                    }catch (e:NullPointerException){}
                }else{
                    Utils.dismissLoader()
                    Toasty.error(requireContext(),"User not found",Toasty.LENGTH_LONG).show()
                }
            }
    }


}
