package com.kineticdevelopers.digitalbooks.admin

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.helper.Utils
import com.kineticdevelopers.digitalbooks.ui.MainActivity
import com.theartofdev.edmodo.cropper.CropImage
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_upload_slider_images.*
import java.io.ByteArrayOutputStream

class UploadSliderImages : AppCompatActivity() {
    private var imageRef: StorageReference? = null
    private var imageBitmap: Bitmap? = null
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_slider_images)

        imageRef = FirebaseStorage.getInstance().reference.child("slider images")

        upload_slider_image_uploadBtn.setOnClickListener {
            if (imageBitmap != null){
                Utils.showLoader(this,"Please wait..")
                uploadImage()
            }else{
                Toasty.info(this,"Please select Image",Toasty.LENGTH_LONG).show()
            }
        }

        upload_slider_image_selectImage.setOnClickListener {
            CropImage.activity().setAspectRatio(2, 1).start(this)
        }

    }

    private fun uploadImage() {
        val baos = ByteArrayOutputStream()
        imageBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val fileRef = imageRef!!.child("${System.currentTimeMillis()}.jpg")
        val uploadTask: StorageTask<*>
        uploadTask = fileRef.putBytes(data)
        uploadTask.continueWithTask(com.google.android.gms.tasks.Continuation<com.google.firebase.storage.UploadTask.TaskSnapshot, com.google.android.gms.tasks.Task<android.net.Uri>> {
            if (!it.isSuccessful) {
                it.exception?.let { error ->
                    throw error
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                imageUrl = task.result.toString()
                saveImageInfo()
            }
        }
    }

    private fun saveImageInfo() {
        val imageMap = HashMap<String,Any>()
        imageMap["image_url"] = imageUrl
        Utils.database()
            .collection("home_slider_images")
            .document(System.currentTimeMillis().toString())
            .set(imageMap)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Utils.dismissLoader()
                    Toasty.success(this,"Upload Successful",Toasty.LENGTH_LONG).show()
                    finish()
                }else{
                    Utils.dismissLoader()
                    Toasty.error(this,it.exception!!.message.toString(),Toasty.LENGTH_LONG).show()
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            val cropedImage = CropImage.getActivityResult(data)
            val imageUri = cropedImage.uri
            imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            upload_slider_image_image.setImageBitmap(imageBitmap)
        }
    }


}