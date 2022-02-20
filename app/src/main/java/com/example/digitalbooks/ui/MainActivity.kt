package com.example.digitalbooks.ui

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission

import com.example.digitalbooks.admin.AdminActivity
import com.example.digitalbooks.fragments.BooksFragment
import com.example.digitalbooks.fragments.HomeFragment
import com.example.digitalbooks.fragments.ProfileFragment
import com.example.digitalbooks.helper.Utils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.os.AsyncTask
import android.provider.MediaStore
import android.view.View
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.example.digitalbooks.helper.Constants
import com.theartofdev.edmodo.cropper.CropImage
import example.digitalbooks.R
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.lang.Exception


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, PermissionListener {
    private var selectedFragment: Fragment? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigatioView: NavigationView? = null
    val  PERMISSION_REQUEST_CODE = 0

    private var profilePicRef: StorageReference? = null
    private var profilePicBitmap: Bitmap? = null
    private var mediaDownloadUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profilePicRef = FirebaseStorage.getInstance().reference.child("avatars")

        supportFragmentManager.beginTransaction().replace(R.id.main_frame, HomeFragment()).commit()
        bottomNavSetUp()
        setUpNavigation()

        main_admin.setOnClickListener {
            startActivity(Intent(this,AdminActivity::class.java))
            Animatoo.animateSwipeLeft(this)
        }

        checkIsAdminView()

    }

    private fun checkIsAdminView() {
        Utils.database().collection("admin").document(Utils.currentUserID())
            .get()
            .addOnSuccessListener {
                if (it.exists()){
                    main_admin.visibility = View.VISIBLE
                }else{
                    Utils.dismissLoader()
                    Toasty.error(this,"User not found",Toasty.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            val cropedImage = CropImage.getActivityResult(data)
            val categoryImageUri = cropedImage.uri
            profilePicBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, categoryImageUri)
            uploadProfilePic().execute()
        }
    }

    inner class uploadProfilePic : AsyncTask<Void, Void, Void>() {
        override fun onPreExecute() {
            Toasty.info(this@MainActivity, "Uploading Profile Picture...", Toasty.LENGTH_LONG).show()
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            try {
                val baos = ByteArrayOutputStream()
                profilePicBitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, baos)
                val data = baos.toByteArray()
                val fileRef = profilePicRef!!.child("${Utils.currentUserID()}.jpg")
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
                        mediaDownloadUrl = task.result.toString()
                        uploadAvatar()
                    }
                }
            } catch (e: Exception) {
            }
            return null
        }
    }

    private fun uploadAvatar() {
        val avatarMap = HashMap<String, Any>()
        avatarMap["profile_image"] = mediaDownloadUrl
        Utils.database().collection(Constants.users).document(Utils.currentUserID()).update(avatarMap)
            .addOnSuccessListener {
                Toasty.success(this,"Upload Successful",Toasty.LENGTH_LONG).show()
            }
    }

//    private fun requestPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.addCategory("android.intent.category.DEFAULT")
//                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
//                startActivityForResult(intent, 2296)
//            } catch (e: Exception) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
//                startActivityForResult(intent, 2296)
//            }
//        } else {
//            //below android 11
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(WRITE_EXTERNAL_STORAGE),
//                PERMISSION_REQUEST_CODE
//            )
//        }
//    }

    override fun onStart() {
        super.onStart()
        checkStoragePermission()
//        requestPermission()
    }

    private fun checkStoragePermission() {
        TedPermission.with(this)
            .setPermissionListener(this)
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE)
            .check()
    }

    private fun bottomNavSetUp() {
        main_bottomBar.onItemSelected = {
            when (it) {
                0 -> selectedFragment = HomeFragment()
                1 -> selectedFragment = BooksFragment()
                2 -> selectedFragment = ProfileFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, selectedFragment!!).commit()
            }
        }
    }

    private fun setUpNavigation() {
        drawerLayout = findViewById(R.id.main_drawer_layout)
        navigatioView = findViewById(R.id.main_navView)
        navigatioView!!.setNavigationItemSelectedListener(this)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, main_toolbar, R.string.drawer_open, R.string.drawer_close)
        drawerLayout!!.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.home -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, HomeFragment()).commit()
            }
            R.id.book -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, BooksFragment()).commit()
            }
            R.id.profile -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, ProfileFragment()).commit()
            }
            R.id.logout -> {
                Utils.showLoader(this,"Loging out...")
                FirebaseAuth.getInstance().signOut()
                Utils.dismissLoader()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                Animatoo.animateSlideDown(this)
            }

        }
        closeDrawer()
        return false
    }

    private fun closeDrawer() {
        drawerLayout!!.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            closeDrawer()
        }
    }

    override fun onPermissionGranted() {
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

    }

}