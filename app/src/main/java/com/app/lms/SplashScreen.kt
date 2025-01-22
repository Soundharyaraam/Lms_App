package com.app.lms

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.EWPMS.utilities.AppConstants
import com.app.lms.databinding.ActivitySplashBinding
import com.app.lms.utilities.AppSharedPreferences
import com.app.lms.utilities.Common

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Common.fullScreen(window)
        binding = ActivitySplashBinding.inflate(layoutInflater);
        setContentView(binding.root)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

        try {
            if (AppSharedPreferences.getStringSharedPreference(
                    baseContext,
                    AppConstants.REMEMBER_ME
                ) != null && (!AppSharedPreferences.getStringSharedPreference(
                    baseContext, AppConstants.REMEMBER_ME
                ).equals(""))
            ) {
                if (AppSharedPreferences.getStringSharedPreference(
                        baseContext,
                        AppConstants.REMEMBER_ME
                    ).equals("true")
                ) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                        finish()
                    }, 1800)
                }else{
                    startActivity(Intent(this@SplashScreen, SignInActivity::class.java))
                    finish()
                }
            }else{
                startActivity(Intent(this@SplashScreen, SignInActivity::class.java))
                finish()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}