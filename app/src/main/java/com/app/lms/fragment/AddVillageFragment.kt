package com.app.lms.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.MainActivity
import com.app.lms.R
import com.app.lms.dataResponse.DashBoardSpinnerResponse
import com.app.lms.dataResponse.VillageDataResponse
import com.app.lms.databinding.FragmentAddVillageBinding
import com.app.lms.utilities.Common
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AddVillageFragment : Fragment() {

    private lateinit var binding: FragmentAddVillageBinding

    //api call
    lateinit var progressDialog: Dialog

    private lateinit var mandals_list: ArrayList<DashBoardSpinnerResponse>
    private lateinit var village_list: ArrayList<VillageDataResponse>
    private lateinit var district_list: ArrayList<DashBoardSpinnerResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAddVillageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callCommonClass()
        onClickListener()
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        call_district_api()
    }

    private fun onClickListener() {
        binding.clearBtn.setOnClickListener {
            binding.spinnerdistrict.setSelection(0)
            binding.spinnerMandal.setSelection(0)

            binding.villageEt.text.clear()
            binding.villageEt.hint=getString(R.string.enter_village)
        }

        binding.spinnerdistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    var district_id=district_list[position-1].id.toString()
                    call_mandal_api(district_id)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected, if needed
            }
        }


        binding.saveBtn.setOnClickListener {
            if(binding.spinnerdistrict.selectedItemPosition!=0){
                if(binding.spinnerMandal.selectedItemPosition!=0){
                    if(binding.villageEt.text.toString().isNotEmpty()){
                        call_add_vilage()
                    }else{
                        Toast.makeText(requireContext(),getString(R.string.enter_Email), Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(),getString(R.string.enter_mob), Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(),getString(R.string.enter_log_name), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun call_add_vilage() {
        if (Common.isInternetAvailable(requireContext())) {
            try {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_Ins_VillageMaster"

                // Create the JSON payload
                val jsonBody = JSONObject()
                jsonBody.put("VillageMasterID","0")
                jsonBody.put("MandalId",mandals_list[binding.spinnerMandal.selectedItemPosition-1].id.toString())
                jsonBody.put("VillageName",binding.villageEt.text.toString().trim())

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
                                        getString(R.string.add_village_success),
                                        Toast.LENGTH_SHORT).show()
                                    val transaction = parentFragmentManager.beginTransaction()
                                    transaction.replace(R.id.fragment_container, VillageFragment())
                                    transaction.commit()
                                    (activity as? MainActivity)?.updateTextView(getString(R.string.villege_menu))
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
                        progressDialog.dismiss()
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

    private fun call_district_api() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_get_Districts_AllLMS/"
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            district_list = ArrayList<DashBoardSpinnerResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val id = jsonObject.optString("DistrictMasterID")
                                val name = jsonObject.optString("DistrictName")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = DashBoardSpinnerResponse(
                                    id.toInt(),
                                    name)

                                district_list.add(workItem)
                            }

                            if (district_list.size > 0) {
                                var district_name_list=ArrayList<String>()
                                district_name_list.add("Choose..")
                                for(i in district_list.indices){
                                    district_name_list.add(district_list[i].name.toString())
                                }
                                lateinit var district_adapter: ArrayAdapter<String>
                                district_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, district_name_list)
                                binding.spinnerdistrict.adapter = district_adapter
                                progressDialog.dismiss()

                                val value = arguments?.getString("village_id")
                                if (value != null) {
                                    if (value.isNotEmpty()) {
                                        call_mandal_api(district_list[1].id.toString())
                                    }
                                }

                            } else {
                                progressDialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.response_failure_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
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

    private fun call_mandal_api(district_id: String) {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_get_Mandals_Dist/?id="+district_id
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            mandals_list = ArrayList<DashBoardSpinnerResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val id = jsonObject.optString("MandalMasterID")
                                val name = jsonObject.optString("MandalName")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = DashBoardSpinnerResponse(
                                    id.toInt(),
                                    name)

                                mandals_list.add(workItem)
                            }

                            if (mandals_list.size > 0) {
                                var mandal_name_list=ArrayList<String>()
                                mandal_name_list.add("Choose..")
                                for(i in mandals_list.indices){
                                    mandal_name_list.add(mandals_list[i].name.toString())
                                }
                                lateinit var mandals_adapter: ArrayAdapter<String>
                                mandals_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, mandal_name_list)
                                binding.spinnerMandal.adapter = mandals_adapter
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.response_failure_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            val value = arguments?.getString("village_id")
                            if (value != null) {
                                if (value.isNotEmpty()) {
                                    call_village_details_api(value)
                                }
                            }
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
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

    private fun call_village_details_api(value:String) {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url ="http://vmrda.gov.in/ewpms_api/api/Usp_getVillagesList/"
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            village_list = ArrayList<VillageDataResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                var DistrictMasterID = jsonObject.optString("DistrictMasterID")
                                var DistrictName = jsonObject.optString("DistrictName")
                                var MandalMasterID = jsonObject.optString("MandalMasterID")
                                var MandalName = jsonObject.optString("MandalName")
                                var Sno = jsonObject.optString("Sno")
                                var VillageMasterID = jsonObject.optString("VillageMasterID")
                                var VillageName = jsonObject.optString("VillageName")

                                // Create a new MyWorksResponse object and add it to the list
                                val village_Item = VillageDataResponse(
                                    DistrictMasterID,DistrictName,MandalMasterID,
                                    MandalName,Sno,VillageMasterID,VillageName
                                )

                                village_list.add(village_Item)
                            }

                            if (village_list.size > 0) {
                                progressDialog.dismiss()
                                for(i in village_list.indices){
                                    if(village_list[i].VillageMasterID==value){
                                        binding.villageEt.setText(village_list[i].VillageName.toString())
                                        binding.spinnerdistrict.setSelection(i+1)
                                        binding.spinnerMandal.setSelection(i+1)
                                    }
                                }
                            } else {
                                progressDialog.dismiss()
                            }
                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //call_district_api()
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
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

}