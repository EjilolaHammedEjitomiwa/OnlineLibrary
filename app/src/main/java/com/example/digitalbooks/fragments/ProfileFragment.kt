package com.example.digitalbooks.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.example.digitalbooks.helper.Constants
import com.example.digitalbooks.helper.Utils
import com.example.digitalbooks.model.UsersModel
import com.example.digitalbooks.ui.AddBookActivity
import com.example.digitalbooks.ui.CartActivity
import com.example.digitalbooks.ui.LoginActivity
import com.theartofdev.edmodo.cropper.CropImage
import es.dmoral.toasty.Toasty
import example.digitalbooks.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.lang.NullPointerException

class ProfileFragment :Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        view.profile_addBtn.setOnClickListener {
            startActivity(Intent(requireContext(),AddBookActivity::class.java))
        }

        view.profile_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            Animatoo.animateSlideDown(requireContext())
        }

        view.profile_changeProfileImage.setOnClickListener {
            cropImage()
        }

        view.profile_myCart.setOnClickListener {
            startActivity(Intent(requireContext(),CartActivity::class.java))
            Animatoo.animateSwipeLeft(requireContext())
        }

        loadProfile()
        return view
    }

    private fun cropImage() {
        CropImage.activity().start(requireActivity())
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
                        if (user.profile_image != null && user.profile_image != ""){
                            Utils.loadImage(requireContext(),user.profile_image.toString(),profile_image)
                        }

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
