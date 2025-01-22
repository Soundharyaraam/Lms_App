package com.app.lms

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.EWPMS.utilities.AppConstants
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.databinding.ActivitySignInBinding
import com.app.lms.utilities.AppSharedPreferences
import com.app.lms.utilities.Common
import org.json.JSONArray
import org.json.JSONException

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    var check: Boolean = false
    //api call
    lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivitySignInBinding.inflate(layoutInflater);
        setContentView(binding.root)
        try{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }catch (e:Exception){
            e.printStackTrace()
        }

        callCommonClass()

        setOnclickListener()
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(this)
    }

    private fun setOnclickListener() {
        binding.showTv.setOnClickListener {
            if (check) {
                check = false
                binding.passwordEt.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.passwordEt.setSelection(binding.passwordEt.text.length)
                binding.showTv.text=getString(R.string.show_txt)
            } else {
                check = true
                binding.passwordEt.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                binding.passwordEt.setSelection(binding.passwordEt.text.length)
                binding.showTv.text=getString(R.string.hide_txt)
            }
        }

        binding.loginBtn.setOnClickListener {
            try {
                if (binding.userIdEt.text.toString().isNotEmpty()) {
                    if (binding.passwordEt.text.toString().isNotEmpty()) {
                        call_login_api(binding.userIdEt.text.toString())
                    } else {
                        Toast.makeText(this@SignInActivity, getString(R.string.valid_password_error), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignInActivity, getString(R.string.valid_username_error), Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }

    private fun call_login_api(userId: String) {
        if (Common.isInternetAvailable(this@SignInActivity)) {
            progressDialog.show()
            val url = "http://vmrda.gov.in/ewpms_api/api//Usp_Check_LoginLMS/?id="+userId
            Log.d("API_URL", url)

            val queue = Volley.newRequestQueue(this)
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { response ->
                    try {
                        val jsonArray = JSONArray(response)
                        val obj = jsonArray.getJSONObject(0)
                        Log.d("Response", response)

                        // Assuming `Password` or `UserType` exists
                        val user_name = obj.getString("EmployeeName")
                        val user_password = obj.getString("Password")
                        val loginId = obj.getString("EmployeeID")
                        val userType = obj.getString("LoginName")
                        val userMobile = obj.getString("MobileNo")

                        if(user_password.equals(binding.passwordEt.text.toString().trim())) {
                            Toast.makeText(this, "Welcome $user_name!", Toast.LENGTH_LONG).show()

                            if (binding.rememberMeCheckBox.isChecked) {
                                AppSharedPreferences.setStringPreference(
                                    this, AppConstants.REMEMBER_ME, "true"
                                )
                            } else {
                                AppSharedPreferences.setStringPreference(
                                    this, AppConstants.REMEMBER_ME, "false"
                                )
                            }

                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERTYPE,
                                userType
                            )
                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERID,
                                loginId
                            )
                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERNAME,
                                user_name
                            )
                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.USERMOBILE,
                                userMobile
                            )
                            AppSharedPreferences.setStringPreference(
                                this,
                                AppConstants.PASSWORD,
                                user_password
                            )

                            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                            finish()
                        }else{
                            progressDialog.dismiss()
                            Toast.makeText(
                                this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("JSONError", "Parsing error", e)
                        progressDialog.dismiss()
                        Toast.makeText(
                            this, getString(R.string.user_details_not_found), Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    progressDialog.dismiss()
                    Log.e("VolleyError", "Request failed", error)
                    Toast.makeText(this, getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                }
            )
            queue.add(stringRequest)
        }else{
            Toast.makeText(
                this, getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
        }
    }

}