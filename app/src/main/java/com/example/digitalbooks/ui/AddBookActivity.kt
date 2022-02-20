package com.example.digitalbooks.ui

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask

import com.example.digitalbooks.helper.Constants
import com.example.digitalbooks.helper.Utils
import com.example.digitalbooks.model.CategoryModel
import com.theartofdev.edmodo.cropper.CropImage
import es.dmoral.toasty.Toasty
import example.digitalbooks.R
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.activity_upload_slider_images.*
import java.io.ByteArrayOutputStream

class AddBookActivity : AppCompatActivity() {
    private var imageRef: StorageReference? = null
    private var imageBitmap: Bitmap? = null
    private var imageUrl: String = ""
    var recommended = false
    var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        imageRef = FirebaseStorage.getInstance().reference.child("product images")

        add_book_selectImage.setOnClickListener {
            CropImage.activity().start(this)
        }

        add_book_type.setItems("Recommended","Not Recommended")

        add_book_type.setOnItemSelectedListener { view, position, id, item ->
            if (item.toString() == "Recommended"){
                recommended =  true
            }
        }

        add_book_category.setOnItemSelectedListener { view, position, id, item ->
            category =  item.toString()
        }

        add_book_publishBtn.setOnClickListener {
            val title = add_book_title.text.toString()
            val description = add_book_description.text.toString()
            val price = add_book_price.text.toString()
            val link = add_book_link.text.toString()

            when{
                TextUtils.isEmpty(title) -> Toasty.info(this,"title is required",Toasty.LENGTH_LONG).show()
                TextUtils.isEmpty(description) -> Toasty.info(this,"description is required",Toasty.LENGTH_LONG).show()
                TextUtils.isEmpty(price) -> Toasty.info(this,"price is required",Toasty.LENGTH_LONG).show()
                TextUtils.isEmpty(link) -> Toasty.info(this,"link is required",Toasty.LENGTH_LONG).show()
                imageBitmap == null -> Toasty.info(this,"select product image",Toasty.LENGTH_LONG).show()
                category == "" -> Toasty.info(this,"Category is required",Toasty.LENGTH_LONG).show()
                else -> {
                    Utils.showLoader(this,"Publishing...")
                    uploadProductImage(title,description,price,link)
                }
            }

        }
    }

    private fun uploadProductImage(title: String, description: String, price: String, link: String) {
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
                saveBookInfo(title,description,price,link)
            }
        }
    }

    private fun saveBookInfo(title: String, description: String, price: String, link: String) {
        val bookMap = HashMap<String,Any>()
        bookMap["book_image"] = imageUrl
        bookMap["title"] = title
        bookMap["description"] = description
        bookMap["price"] = price
        bookMap["recommended"] = recommended
        bookMap["link"] = link
        bookMap["category"] = category
        bookMap["views"] = 0
        bookMap["seller"] = Utils.currentUserID()

        Utils.database()
            .collection("books")
            .document(System.currentTimeMillis().toString())
            .set(bookMap)
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
            add_book_image.setImageBitmap(imageBitmap)
        }
    }

    override fun onStart() {
        super.onStart()
        loadCategory()
    }

    private fun loadCategory() {
        Utils.showLoader(this,"Loading Categories...")
        Utils.database()
            .collection(Constants.categories)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty){
                    val categories = mutableListOf<String>()
                    for (data in it.documents){
                        val item = data.toObject(CategoryModel::class.java)
                        categories.add(item!!.name.toString())
                    }
                    add_book_category.setItems(categories)
                    Utils.dismissLoader()

                }else{
                   Utils.dismissLoader()
                }

            }
    }
}