package com.kineticdevelopers.digitalbooks.admin

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.helper.Utils
import com.theartofdev.edmodo.cropper.CropImage
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.activity_upload_slider_images.*
import java.io.ByteArrayOutputStream

class AddCategory : AppCompatActivity() {
    private var imageRef: StorageReference? = null
    private var imageBitmap: Bitmap? = null
    private var imageUrl: String = ""
    var categoryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        imageRef = FirebaseStorage.getInstance().reference.child("category images")

        add_category_selectImage.setOnClickListener {
            CropImage.activity().setAspectRatio(1, 1).start(this)
        }

        add_category_addBtn.setOnClickListener {
             categoryName = add_category_name.text.toString()
            when{
                categoryName == "" -> Toasty.info(this,"Please enter category name",Toasty.LENGTH_LONG).show()
                imageBitmap == null -> Toasty.info(this,"Please select the product image",Toasty.LENGTH_LONG).show()
                else ->{
                    Utils.showLoader(this,"Please wait...")
                    uploadCategoryImage()
                }
            }

        }

    }

    private fun uploadCategoryImage() {
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
                saveCategoryInfo()
            }
        }
    }

    private fun saveCategoryInfo() {
        val categoryMap = HashMap<String,Any>()
        categoryMap["name"] = categoryName
        categoryMap["icon_url"] = imageUrl
        Utils.database()
            .collection("categories")
            .document(System.currentTimeMillis().toString())
            .set(categoryMap)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Utils.dismissLoader()
                    Toasty.success(this,"Saved Successfully",Toasty.LENGTH_LONG).show()
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
            add_category_image.setImageBitmap(imageBitmap)
        }
    }
}