package com.app.lms.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.Adapter.EmployeeAdapter
import com.app.lms.Adapter.ProjectsAdapter
import com.app.lms.R
import com.app.lms.activity.ProjectDetailsActivity
import com.app.lms.dataResponse.DashBoardSpinnerResponse
import com.app.lms.dataResponse.EmployeeData
import com.app.lms.dataResponse.ProjectsListResponse
import com.app.lms.databinding.ProjectListFragmentBinding
import com.app.lms.utilities.Common
import com.finowizx.CallBackInterface.CallBackData
import org.json.JSONArray
import org.json.JSONException

class ProjectListFragment : Fragment(), CallBackData {
    //api call
    lateinit var progressDialog: Dialog
    private lateinit var binding: ProjectListFragmentBinding
    private lateinit var projects_list: ArrayList<ProjectsListResponse>
    private lateinit var projects_name_list: ArrayList<ProjectsListResponse>

    private lateinit var project_adapter: ProjectsAdapter

    private lateinit var district_list: ArrayList<DashBoardSpinnerResponse>
    private lateinit var mandals_list: ArrayList<DashBoardSpinnerResponse>
    private lateinit var villege_list: ArrayList<DashBoardSpinnerResponse>

    var district_id="All"
    var mandal_id="All"
    var village_id="All"
    var api_status="All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = ProjectListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callCommonClass()

        binding.spinnerDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    district_id=district_list[position-1].id.toString()
                    mandal_id="All"
                    village_id="All"
                    api_status="district"
                    call_projects_api()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected, if needed
            }
        }

        binding.spinnerMandal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    mandal_id=mandals_list[position-1].id.toString()
                    district_id="All"
                    village_id="All"
                    api_status="mandal"
                    call_projects_api()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected, if needed
            }
        }

        binding.spinnerVillege.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != 0) {
                    village_id=villege_list[position-1].id.toString()
                    district_id="All"
                    mandal_id="All"
                    api_status="village"
                    call_projects_api()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case when no item is selected, if needed
            }
        }

        binding.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                projects_name_list = ArrayList<ProjectsListResponse>()
                projects_name_list.clear()

                if (binding.searchEdt.text.toString().isNotEmpty()) {
                    if (binding.searchEdt.text.toString().length >= 2) {
                        for (i in projects_list.indices) {
                            if (projects_list[i].P_Classification!!.length>=2) {
                                if (projects_list[i].P_Classification!!.toLowerCase().substring(0, 2) == binding.searchEdt.text.toString().lowercase().substring(0, 2)) {
                                    projects_name_list.add(projects_list[i])
                                }
                            }
                        }

                        if(projects_name_list.size>0) {
                            binding.noDataLayout.visibility=View.GONE
                            binding.utilizationRv.visibility=View.VISIBLE

                            binding.utilizationRv.adapter =
                                ProjectsAdapter(requireContext(), projects_name_list, this@ProjectListFragment)
                        }else{
                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.utilizationRv.visibility=View.GONE
                        }
                    }else{
                        binding.noDataLayout.visibility=View.GONE
                        binding.utilizationRv.visibility=View.VISIBLE

                        binding.utilizationRv.adapter =
                            ProjectsAdapter(requireContext(), projects_list, this@ProjectListFragment)                    }
                }
            }
        })

        onclickListeners()
    }

    private fun onclickListeners() {
        binding.previousBtn.setOnClickListener {
            if(binding.pageNoTv.text.toString()!="1") {
                var new_list = ArrayList<ProjectsListResponse>()
                binding.pageNoTv.text = (binding.pageNoTv.text.toString().toInt()-1).toString()

                for (i in projects_list.indices) {
                    if (i.toString().length >= 2) {
                        if ((binding.pageNoTv.text.toString()
                                .toInt() - 1).toString() == i.toString().substring(0, 1)
                        ) {
                            new_list.add(projects_list[i])
                        }
                    }
                }

                if(new_list.size <= 0){
                    for(j in projects_list.indices){
                        if(j<10){
                            new_list.add(projects_list[j])
                        }
                    }
                }

                project_adapter = ProjectsAdapter(
                    requireContext(),
                    new_list,
                    this@ProjectListFragment
                )
                binding.utilizationRv.adapter = project_adapter
            }
        }

        binding.nextBtn.setOnClickListener {
            var new_list = ArrayList<ProjectsListResponse>()
            for (i in projects_list.indices) {
                if (i.toString().length >= 2) {
                    if(binding.pageNoTv.text.toString() == "1") {
                        if ((binding.pageNoTv.text.toString() == i.toString().substring(0, 1))) {
                            new_list.add(projects_list[i])
                        }
                    }else if (binding.pageNoTv.text.toString() == i.toString().substring(0, 1)) {
                        new_list.add(projects_list[i])
                    }
                }
            }

            if(new_list.size>0) {
                binding.pageNoTv.text = (binding.pageNoTv.text.toString().toInt() +1).toString()
                project_adapter = ProjectsAdapter(
                    requireContext(),
                    new_list,
                    this@ProjectListFragment
                )
                binding.utilizationRv.adapter = project_adapter
            }
        }

    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        call_projects_api()
    }

    private fun call_projects_api() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url ="http://vmrda.gov.in/ewpms_api/api/Usp_get_ProjectsList2LMS/?dis="+district_id+"&Mandal="+mandal_id+"&Village="+village_id
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            projects_list = ArrayList<ProjectsListResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                var Address1 = jsonObject.optString("Address1")
                                var Ali_Extent_Ac = jsonObject.optString("Ali_Extent_Ac")
                                var Ali_NameoftheFirm = jsonObject.optString("Ali_NameoftheFirm")
                                var Clear_vacant_land = jsonObject.optString("Clear_vacant_land")
                                var CourtCase_WP_No = jsonObject.optString("CourtCase_WP_No")
                                var Covered_by = jsonObject.optString("Covered_by")
                                var CreatedBy = jsonObject.optString("CreatedBy")
                                var   CreatedOn = jsonObject.optString("CreatedOn")
                                var District = jsonObject.optString("District")
                                var DistrictName = jsonObject.optString("DistrictName")
                                var Ext_Ac = jsonObject.optString("Ext_Ac")
                                var Formed_Extent_Ac = jsonObject.optString("Formed_Extent_Ac")
                                var Formed_NameoftheLayout = jsonObject.optString("Formed_NameoftheLayout")
                                var Gist_of_the_case = jsonObject.optString("Gist_of_the_case")
                                var Latitide2 = jsonObject.optString("Latitide2")
                                var Latitide3 = jsonObject.optString("Latitide3")
                                var Latitide4 = jsonObject.optString("Latitide4")
                                var Latitude = jsonObject.optString("Latitude")
                                var Location = jsonObject.optString("Location")
                                var Longitude = jsonObject.optString("Longitude")
                                var Longitude2 = jsonObject.optString("Longitude2")
                                var Longitude3 = jsonObject.optString("Longitude3")
                                var Longitude4 = jsonObject.optString("Longitude4")
                                var  Mandal = jsonObject.optString("Mandal")
                                var MandalName = jsonObject.optString("MandalName")
                                var  P_Classification = jsonObject.optString("P_Classification")
                                var  PresentStatus = jsonObject.optString("PresentStatus")
                                var  ProjectId = jsonObject.optString("ProjectId")
                                var  Public_Extent_Ac = jsonObject.optString("Public_Extent_Ac")
                                var  Public_purpose = jsonObject.optString("Public_purpose")
                                var Remarks = jsonObject.optString("Remarks")
                                var  Sno = jsonObject.optString("Sno")
                                var Sold_Document_fn = jsonObject.optString("Sold_Document_fn")
                                var Sold_Document_fn1 = jsonObject.optString("Sold_Document_fn1")
                                var SyNo = jsonObject.optString("SyNo")
                                var Under_possession_of_others = jsonObject.optString("Under_possession_of_others")
                                var Unfit_Extent_Ac = jsonObject.optString("Unfit_Extent_Ac")
                                var Unfit_Reason = jsonObject.optString("Unfit_Reason")
                                var UpdatedBy = jsonObject.optString("UpdatedBy")
                                var UpdatedOn = jsonObject.optString("UpdatedOn")
                                var  Upload_fn = jsonObject.optString("Upload_fn")
                                var Util_Extent_Ac = jsonObject.optString("Util_Extent_Ac")
                                var Util_NameofthePurpose = jsonObject.optString("Util_NameofthePurpose")
                                var Village = jsonObject.optString("Village")
                                var VillageName = jsonObject.optString("VillageName")
                                var fn1 = jsonObject.optString("fn1")
                                var zone = jsonObject.optString("zone")

                                // Create a new MyWorksResponse object and add it to the list
                                val projectItem = ProjectsListResponse(
                                    Address1,
                                    Ali_Extent_Ac,
                                    Ali_NameoftheFirm,
                                    Clear_vacant_land,
                                    CourtCase_WP_No,
                                    Covered_by,
                                    CreatedBy,
                                    CreatedOn, District, DistrictName, Ext_Ac, Formed_Extent_Ac, Formed_NameoftheLayout, Gist_of_the_case, Latitide2, Latitide3, Latitide4, Latitude, Location, Longitude, Longitude2, Longitude3, Longitude4, Mandal, MandalName, P_Classification, PresentStatus, ProjectId, Public_Extent_Ac, Public_purpose, Remarks, Sno, Sold_Document_fn, Sold_Document_fn1, SyNo, Under_possession_of_others, Unfit_Extent_Ac, Unfit_Reason, UpdatedBy, UpdatedOn, Upload_fn, Util_Extent_Ac, Util_NameofthePurpose, Village, VillageName, fn1, zone
                                )

                                projects_list.add(projectItem)
                            }

                            if (projects_list.size > 0) {
                                 progressDialog.dismiss()
                                if(projects_list.size>=10){
                                    var new_list=ArrayList<ProjectsListResponse>()
                                    binding.previousBtn.isEnabled = true
                                    binding.nextBtn.isEnabled = true
                                    binding.pageNoTv.text = "1"

                                    for(i in projects_list.indices){
                                        if(i<10){
                                            new_list.add(projects_list[i])
                                        }
                                    }
                                    project_adapter = ProjectsAdapter(
                                        requireContext(),
                                        new_list,
                                        this@ProjectListFragment
                                    )
                                    binding.utilizationRv.adapter = project_adapter
                                }else {
                                    binding.previousBtn.isEnabled = false
                                    binding.nextBtn.isEnabled = false
                                    binding.pageNoTv.text = "1"

                                    binding.noDataLayout.visibility = View.GONE
                                    binding.utilizationRv.visibility = View.VISIBLE
                                    binding.utilizationRv.adapter =
                                        ProjectsAdapter(requireActivity(), projects_list, this)
                                }
                            } else {
                                progressDialog.dismiss()
                                binding.noDataLayout.visibility = View.VISIBLE
                                binding.utilizationRv.visibility = View.GONE
                            }
                            if(api_status=="All") {
                                call_district_api()
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

    private fun call_district_api() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/usp_get_ProjectDistrictsLMS/"
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
                                binding.spinnerDistrict.adapter = district_adapter
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()

                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.response_failure_please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            call_mandal_api()
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

    private fun call_mandal_api() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/usp_get_ProjectMandalsLMS/"
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
                            call_villege_api()
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

    private fun call_villege_api() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url = "http://vmrda.gov.in/ewpms_api/api/usp_get_ProjectVillagesLMS/"
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            villege_list = ArrayList<DashBoardSpinnerResponse>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                val id = jsonObject.optString("VillageMasterID")
                                val name = jsonObject.optString("VillageName")

                                // Create a new MyWorksResponse object and add it to the list
                                val workItem = DashBoardSpinnerResponse(
                                    id.toInt(),
                                    name)

                                villege_list.add(workItem)
                            }

                            if (villege_list.size > 0) {
                                var villege_name_list=ArrayList<String>()
                                villege_name_list.add("Choose..")
                                for(i in villege_list.indices){
                                    villege_name_list.add(villege_list[i].name.toString())
                                }
                                lateinit var vilelge_adapter: ArrayAdapter<String>
                                vilelge_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, villege_name_list)
                                binding.spinnerVillege.adapter = vilelge_adapter
                                progressDialog.dismiss()
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

    override fun getTaskStatus(project_id: String, position: String) {
        startActivity(Intent(requireContext(), ProjectDetailsActivity::class.java).putExtra("id",project_id))
        requireActivity().finish()
    }
}