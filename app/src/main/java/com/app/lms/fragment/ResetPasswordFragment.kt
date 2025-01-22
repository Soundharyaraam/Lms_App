package com.app.lms.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.EWPMS.utilities.AppConstants
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.app.lms.MainActivity
import com.app.lms.R
import com.app.lms.SignInActivity
import com.app.lms.databinding.FragmentAddEmployeeBinding
import com.app.lms.databinding.FragmentResetPasswordBinding
import com.app.lms.utilities.AppSharedPreferences
import com.app.lms.utilities.Common
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordFragment : Fragment() {
    private lateinit var binding: FragmentResetPasswordBinding

    var check_old: Boolean = false
    var check_new: Boolean = false
    var check_confirm: Boolean = false

    //api call
    lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentResetPasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Common.progressDialog(requireContext())

        show_password()

        binding.saveBtn.setOnClickListener {
            if(binding.etOldPass.text.toString().isNotEmpty()){
                if(binding.etNewPass.text.toString().isNotEmpty()){
                    if(binding.etConfirmPass.text.toString().isNotEmpty()){
                       if(AppSharedPreferences.getStringSharedPreference(
                               requireContext(), AppConstants.PASSWORD)==binding.etOldPass.text.toString()) {
                           if(binding.etNewPass.text.toString() == binding.etConfirmPass.text.toString()) {
                               call_change_password()
                           }else{
                               Toast.makeText(requireContext(),getString(R.string.pass_not_match),Toast.LENGTH_SHORT).show()
                           }
                       }else{
                           Toast.makeText(requireContext(),getString(R.string.old_pass_error),Toast.LENGTH_SHORT).show()
                       }
                    }else{
                        Toast.makeText(requireContext(),getString(R.string.enter_confirm_pass),Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),getString(R.string.enter_new_pass),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),getString(R.string.enter_old_pass),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun call_change_password() {
        try{
            if (Common.isInternetAvailable(requireContext())) {
                try {
                    progressDialog.show()
                    val url = "http://vmrda.gov.in/ewpms_api/api/Usp_Change_Password2"

                    // Create the JSON payload
                    val jsonBody = JSONObject()
                    jsonBody.put("LoginName", AppSharedPreferences.getStringSharedPreference(
                        requireContext(),
                        AppConstants.USERTYPE))
                    jsonBody.put("Password",binding.etConfirmPass.text.toString().trim())

                    val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())

                    val jsonArrayRequest = object : JsonArrayRequest(
                        Request.Method.POST,
                        url,
                        null, // Passing null here as the body will be sent in getBody
                        { response ->
                            try {
                                // Handle the JSONArray response
                                for (i in 0 until response.length()) {
                                    val jsonObject = response.getJSONObject(i)
                                    val retVal = jsonObject.getString("RetVal")
                                    Log.d("Response Value", "RetVal: $retVal")
                                    if (retVal == "Success") {
                                        progressDialog.dismiss()
                                        // Handle the successful response
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.password_changes_sucess),
                                            Toast.LENGTH_SHORT).show()
                                        AppSharedPreferences.setStringPreference(
                                            requireContext(), AppConstants.REMEMBER_ME, null
                                        )
                                        AppSharedPreferences.setStringPreference(
                                            requireContext(),
                                            AppConstants.USERTYPE,
                                            null
                                        )
                                        AppSharedPreferences.setStringPreference(
                                            requireContext(),
                                            AppConstants.USERID,
                                            null
                                        )
                                        AppSharedPreferences.setStringPreference(
                                            requireContext(),
                                            AppConstants.USERNAME,
                                            null
                                        )
                                        AppSharedPreferences.setStringPreference(
                                            requireContext(),
                                            AppConstants.USERMOBILE,
                                            null
                                        )
                                        AppSharedPreferences.setStringPreference(
                                            requireContext(),
                                            AppConstants.PASSWORD,
                                            null
                                        )

                                        startActivity(Intent(requireContext(), SignInActivity::class.java))
                                        requireActivity().finish()
                                    } else {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.response_failure_please_try_again),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Log.e("API Error", "JSON Parsing error: ${e.message}")
                            }
                        },
                        { error ->
                            // Handle errors
                            Log.e("API Error", "Volley Error: ${error.message}")
                            Toast.makeText(requireContext(), "API Request Failed: ${error.message}", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        // Override getHeaders to set Content-Type
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-Type"] = "application/json"
                            return headers
                        }

                        // Override getBody to send the JSON payload
                        override fun getBody(): ByteArray {
                            return jsonBody.toString().toByteArray(Charsets.UTF_8)
                        }
                    }

                    // Add the request to the Volley queue
                    requestQueue.add(jsonArrayRequest)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.upload_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_check_with_the_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun show_password() {
        binding.showOldTv.setOnClickListener {
            if (check_old) {
                check_old = false
                binding.etOldPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.etOldPass.setSelection(binding.etOldPass.text.length)
                binding.showOldTv.text=getString(R.string.show_txt)
            } else {
                check_old = true
                binding.etOldPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                binding.etOldPass.setSelection(binding.etOldPass.text.length)
                binding.showOldTv.text=getString(R.string.hide_txt)
            }
        }

        binding.showNewTv.setOnClickListener {
            if (check_new) {
                check_new = false
                binding.etNewPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.etNewPass.setSelection(binding.etNewPass.text.length)
                binding.showNewTv.text=getString(R.string.show_txt)
            } else {
                check_new = true
                binding.etNewPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                binding.etNewPass.setSelection(binding.etNewPass.text.length)
                binding.showNewTv.text=getString(R.string.hide_txt)
            }
        }

        binding.showConfirmTv.setOnClickListener {
            if (check_confirm) {
                check_confirm = false
                binding.etConfirmPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.etConfirmPass.setSelection(binding.etConfirmPass.text.length)
                binding.showConfirmTv.text=getString(R.string.show_txt)
            } else {
                check_confirm = true
                binding.etConfirmPass.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                binding.etConfirmPass.setSelection(binding.etConfirmPass.text.length)
                binding.showConfirmTv.text=getString(R.string.hide_txt)
            }
        }

    }

}