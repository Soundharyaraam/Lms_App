package com.app.lms.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.MainActivity
import com.app.lms.R
import com.app.lms.dataResponse.DashBoardSpinnerResponse
import com.app.lms.databinding.FragmentAddEmployeeBinding
import com.app.lms.databinding.FragmentAddVillageBinding
import com.app.lms.utilities.Common
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AddEmployeeFragment : Fragment() {

    private lateinit var binding: FragmentAddEmployeeBinding
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

        binding = FragmentAddEmployeeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callCommonClass()

        onClickListener()
    }

    private fun onClickListener() {
        binding.clearBtn.setOnClickListener {
            binding.empNameEt.text.clear()
            binding.empNameEt.hint=getString(R.string.enter_emp_name)

            binding.logNameEt.text.clear()
            binding.logNameEt.hint=getString(R.string.enter_log_name)

            binding.mobileEt.text.clear()
            binding.mobileEt.hint=getString(R.string.enter_mob)

            binding.emailEt.text.clear()
            binding.emailEt.hint=getString(R.string.enter_Email)
        }

        binding.saveBtn.setOnClickListener {
            if(binding.empNameEt.text.toString().isNotEmpty()){
                if(binding.logNameEt.text.toString().isNotEmpty()){
                    if(binding.mobileEt.text.toString().isNotEmpty()){
                        if(binding.emailEt.text.toString().isNotEmpty()){
                           call_Add_emp()
                        }else{
                            Toast.makeText(requireContext(),getString(R.string.enter_Email),Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(requireContext(),getString(R.string.enter_mob),Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),getString(R.string.enter_log_name),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),getString(R.string.enter_emp_name),Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun call_Add_emp() {
        if (Common.isInternetAvailable(requireContext())) {
            try {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_Ins_EmployeeMaster2"

                // Create the JSON payload
                val jsonBody = JSONObject()
                jsonBody.put("EmployeeID","0")
                jsonBody.put("EmployeeName",binding.empNameEt.text.toString().trim())
                jsonBody.put("LoginName",binding.logNameEt.text.toString().trim())
                jsonBody.put("MobileNo",binding.mobileEt.text.toString().trim())
                jsonBody.put("EmailID",binding.emailEt.text.toString().trim())

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
                                        getString(R.string.add_employee_success),
                                        Toast.LENGTH_SHORT).show()
                                    val transaction = parentFragmentManager.beginTransaction()
                                    transaction.replace(R.id.fragment_container, EmployeeMasterFragment())
                                    transaction.commit()
                                    (activity as? MainActivity)?.updateTextView(getString(R.string.employe_menu))
                                } else {
                                    progressDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        getString(R.string.upload_failed),
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
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        val value = arguments?.getString("emp_id")
        if (value != null) {
            if (value.isNotEmpty()) {
                call_emp_details_api(value)
            }
        }
    }

        private fun call_emp_details_api(id:String) {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_Get_Employees_dt/?id="+id
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            val jsonArray = JSONArray(response)
                            val obj = jsonArray.getJSONObject(0)
                            Log.d("Response", response)

                            // Assuming `Password` or `UserType` exists
                            val Sno = obj.getString("Sno")
                            val EmployeeID = obj.getString("EmployeeID")
                            val EmployeeName = obj.getString("EmployeeName")
                            val LoginName = obj.getString("LoginName")
                            val MobileNo = obj.getString("MobileNo")
                            val EmailID = obj.getString("EmailID")

                            if(Sno.toString().isNotEmpty()){
                                progressDialog.dismiss()
                                binding.empNameEt.setText(EmployeeName)
                                binding.logNameEt.setText(LoginName)
                                binding.mobileEt.setText(MobileNo)
                                binding.emailEt.setText(EmailID)

                            }else{
                                progressDialog.dismiss()
                                Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
                    }
                )
                queue.add(stringRequest)
            }else{
                Toast.makeText(requireContext(), getString(R.string.please_check_with_the_internet_connection), Toast.LENGTH_SHORT).show()
            }
        }

}