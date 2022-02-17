package com.kineticdevelopers.digitalbooks.ui

import android.Manifest
import android.Manifest.permission
import android.content.Intent
import android.net.Uri
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
import com.google.firebase.firestore.Source
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.kineticdevelopers.digitalbooks.R
import com.kineticdevelopers.digitalbooks.admin.AdminActivity
import com.kineticdevelopers.digitalbooks.fragments.BooksFragment
import com.kineticdevelopers.digitalbooks.fragments.HomeFragment
import com.kineticdevelopers.digitalbooks.fragments.ProfileFragment
import com.kineticdevelopers.digitalbooks.helper.Utils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE

import androidx.core.app.ActivityCompat

import android.os.Build
import android.os.Build.VERSION

import android.os.Build.VERSION.SDK_INT
import android.provider.Settings
import java.lang.Exception


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, PermissionListener {
    private var selectedFragment: Fragment? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigatioView: NavigationView? = null
    val  PERMISSION_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.main_frame, HomeFragment()).commit()
        bottomNavSetUp()
        setUpNavigation()

        main_admin.setOnClickListener {
            startActivity(Intent(this,AdminActivity::class.java))
            Animatoo.animateSwipeLeft(this)
        }

    }

    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this,
                arrayOf(WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onStart() {
        super.onStart()
        checkStoragePermission()
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