package com.app.lms

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.EWPMS.utilities.AppConstants
import com.app.lms.databinding.ActivityMainBinding
import com.app.lms.fragment.AddEmployeeFragment
import com.app.lms.fragment.AddUtilizationFragment
import com.app.lms.fragment.AddVillageFragment
import com.app.lms.fragment.EmployeeMasterFragment
import com.app.lms.fragment.HomeFragment
import com.app.lms.fragment.ProjectListFragment
import com.app.lms.fragment.ResetPasswordFragment
import com.app.lms.fragment.VillageFragment
import com.app.lms.utilities.AppSharedPreferences
import com.app.lms.utilities.Common

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityMainBinding.inflate(layoutInflater)
        Common.fullScreen(window)
        setContentView(binding.root)

        call_Drawer()
        call_bottom_navigation()
    }

    private fun call_bottom_navigation() {
        replaceFragment(HomeFragment())
        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
            binding.userNameTv.text =
                AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                    .toString()
        }

        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
            binding.userIdTv.text =
                AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                    .toString()
        }

        binding.homeMenu.setOnClickListener{
            replaceFragment(HomeFragment())
            binding.titleBarHomeLayout.visibility=View.VISIBLE
            binding.titleBar.visibility=View.GONE
            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                binding.userNameTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                        .toString()
            }

            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                binding.userIdTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                        .toString()
            }
        }

        binding.addNewMenu.setOnClickListener{
            replaceFragment(AddUtilizationFragment())
            binding.titleBarHomeLayout.visibility=View.INVISIBLE
            binding.titleBar.visibility=View.VISIBLE
            binding.headTitle.text=getString(R.string.add_uti_head)
        }

        binding.searchMenu.setOnClickListener{
            replaceFragment(ProjectListFragment())
            binding.titleBarHomeLayout.visibility=View.VISIBLE
            binding.titleBar.visibility=View.GONE
            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                binding.userNameTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                        .toString()
            }

            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                binding.userIdTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                        .toString()
            }
        }

        binding.logoutMenu.setOnClickListener{
            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.popup_layout)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            // Set the width of the dialog
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            dialog.window?.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

            var yes_btn = dialog.findViewById<Button>(R.id.yesButton)
            var no_btn = dialog.findViewById<Button>(R.id.noButton)

            yes_btn.setOnClickListener {
                AppSharedPreferences.setStringPreference(
                    this, AppConstants.REMEMBER_ME, null
                )
                AppSharedPreferences.setStringPreference(
                    this,
                    AppConstants.USERTYPE,
                    null
                )
                AppSharedPreferences.setStringPreference(
                    this,
                    AppConstants.USERID,
                    null
                )
                AppSharedPreferences.setStringPreference(
                    this,
                    AppConstants.USERNAME,
                    null
                )
                AppSharedPreferences.setStringPreference(
                    this,
                    AppConstants.USERMOBILE,
                    null
                )
                AppSharedPreferences.setStringPreference(
                    this,
                    AppConstants.PASSWORD,
                    null
                )

                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
            no_btn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }

        binding.backImg.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if(currentFragment is AddUtilizationFragment) {
                replaceFragment(HomeFragment())
                binding.titleBarHomeLayout.visibility=View.VISIBLE
                binding.titleBar.visibility=View.GONE
                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                    binding.userNameTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                            .toString()
                }

                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                    binding.userIdTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                            .toString()
                }
            }else  if(currentFragment is AddEmployeeFragment) {
                replaceFragment(EmployeeMasterFragment())
                binding.titleBarHomeLayout.visibility=View.INVISIBLE
                binding.titleBar.visibility=View.VISIBLE
                binding.headTitle.text=getString(R.string.employe_menu)
            }else  if(currentFragment is EmployeeMasterFragment) {
                replaceFragment(HomeFragment())
                binding.titleBarHomeLayout.visibility=View.VISIBLE
                binding.titleBar.visibility=View.GONE
                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                    binding.userNameTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                            .toString()
                }

                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                    binding.userIdTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                            .toString()
                }
            }else  if(currentFragment is AddVillageFragment) {
                replaceFragment(VillageFragment())
                binding.titleBarHomeLayout.visibility=View.INVISIBLE
                binding.titleBar.visibility=View.VISIBLE
                binding.headTitle.text=getString(R.string.villege_menu)
            }else  if(currentFragment is VillageFragment) {
                replaceFragment(HomeFragment())
                binding.titleBarHomeLayout.visibility=View.VISIBLE
                binding.titleBar.visibility=View.GONE
                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                    binding.userNameTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                            .toString()
                }

                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                    binding.userIdTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                            .toString()
                }
            }else  if(currentFragment is ResetPasswordFragment) {
                replaceFragment(HomeFragment())
                binding.titleBarHomeLayout.visibility=View.VISIBLE
                binding.titleBar.visibility=View.GONE

                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                    binding.userNameTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                            .toString()
                }

                if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                    binding.userIdTv.text =
                        AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                            .toString()
                }
            }else {
                val dialog = Dialog(this@MainActivity)
                dialog.setContentView(R.layout.back_layout_popup)
                dialog.setCancelable(false)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

                // Set the width of the dialog
                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val width = displayMetrics.widthPixels
                dialog.window?.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

                var yes_btn = dialog.findViewById<Button>(R.id.yesButton)
                var no_btn = dialog.findViewById<Button>(R.id.noButton)

                yes_btn.setOnClickListener {
                    finishAffinity()
                }
                no_btn.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }

    private fun call_Drawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.menuButton2.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.closeDrawer)
            .setOnClickListener {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            }

        //set data to navigation
        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
            binding.navView.getHeaderView(0)
                .findViewById<TextView>(R.id.drawer_profile_name).text = AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                .toString()
        }

        if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
            binding.navView.getHeaderView(0)
                .findViewById<TextView>(R.id.user_id_tv).text = AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                .toString()
        }

        // Lock the drawer and handle touch events
        binding.drawerLayout.setDrawerLockMode(
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
            GravityCompat.START
        )

        binding.drawerLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                true
            } else {
                false
            }
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dashboard_menu -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(HomeFragment())
                    binding.titleBarHomeLayout.visibility=View.VISIBLE
                    binding.titleBar.visibility=View.GONE

                    if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                        binding.userNameTv.text =
                            AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                                .toString()
                    }

                    if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                        binding.userIdTv.text =
                            AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                                .toString()
                    }
                }
                R.id.employee_menu -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(EmployeeMasterFragment())
                    binding.titleBarHomeLayout.visibility=View.INVISIBLE
                    binding.titleBar.visibility=View.VISIBLE
                    binding.headTitle.text=getString(R.string.employe_menu)
                }
                R.id.village_menu -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(VillageFragment())
                    binding.titleBarHomeLayout.visibility=View.INVISIBLE
                    binding.titleBar.visibility=View.VISIBLE
                    binding.headTitle.text=getString(R.string.villege_menu)
                }
                R.id.reset_menu -> {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    replaceFragment(ResetPasswordFragment())
                    binding.titleBarHomeLayout.visibility=View.INVISIBLE
                    binding.titleBar.visibility=View.VISIBLE
                    binding.headTitle.text=getString(R.string.reset_password)
                }
            }
            true
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        //super.onBackPressed()

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if(currentFragment is AddUtilizationFragment) {
            replaceFragment(HomeFragment())
            binding.titleBarHomeLayout.visibility=View.VISIBLE
            binding.titleBar.visibility=View.GONE
            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                binding.userNameTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                        .toString()
            }

            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                binding.userIdTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                        .toString()
            }
        }else  if(currentFragment is AddEmployeeFragment) {
            replaceFragment(EmployeeMasterFragment())
            binding.titleBarHomeLayout.visibility=View.INVISIBLE
            binding.titleBar.visibility=View.VISIBLE
            binding.headTitle.text=getString(R.string.employe_menu)
        }else  if(currentFragment is EmployeeMasterFragment) {
            replaceFragment(HomeFragment())
            binding.titleBarHomeLayout.visibility=View.VISIBLE
            binding.titleBar.visibility=View.GONE
            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                binding.userNameTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                        .toString()
            }

            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                binding.userIdTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                        .toString()
            }
        }else  if(currentFragment is AddVillageFragment) {
            replaceFragment(VillageFragment())
            binding.titleBarHomeLayout.visibility=View.INVISIBLE
            binding.titleBar.visibility=View.VISIBLE
            binding.headTitle.text=getString(R.string.villege_menu)
        }else  if(currentFragment is VillageFragment) {
            replaceFragment(HomeFragment())
            binding.titleBarHomeLayout.visibility=View.VISIBLE
            binding.titleBar.visibility=View.GONE
            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                binding.userNameTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                        .toString()
            }

            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                binding.userIdTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                        .toString()
            }
        }else  if(currentFragment is ResetPasswordFragment) {
            replaceFragment(HomeFragment())
            binding.titleBarHomeLayout.visibility=View.VISIBLE
            binding.titleBar.visibility=View.GONE
            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME).isNotEmpty()) {
                binding.userNameTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERNAME)
                        .toString()
            }

            if(AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)!=null && AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE).isNotEmpty()) {
                binding.userIdTv.text =
                    AppSharedPreferences.getStringSharedPreference(baseContext, AppConstants.USERTYPE)
                        .toString()
            }
        }else {
            val dialog = Dialog(this@MainActivity)
            dialog.setContentView(R.layout.back_layout_popup)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            // Set the width of the dialog
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            dialog.window?.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

            var yes_btn = dialog.findViewById<Button>(R.id.yesButton)
            var no_btn = dialog.findViewById<Button>(R.id.noButton)

            yes_btn.setOnClickListener {
                finishAffinity()
            }
            no_btn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    //change text
    fun updateTextView(newText: String) {
        binding.titleBarHomeLayout.visibility=View.INVISIBLE
        binding.titleBar.visibility=View.VISIBLE
        binding.headTitle.text = newText
    }
}