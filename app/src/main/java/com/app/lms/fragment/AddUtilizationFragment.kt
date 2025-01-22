package com.app.lms.fragment

import MultipartRequest
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.EWPMS.utilities.AppConstants
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.MainActivity
import com.app.lms.R
import com.app.lms.activity.ProjectDetailsActivity
import com.app.lms.dataResponse.DashBoardSpinnerResponse
import com.app.lms.databinding.FragmentAddUtilizationBinding
import com.app.lms.utilities.AppSharedPreferences
import com.app.lms.utilities.Common
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class AddUtilizationFragment : Fragment() {

    private lateinit var binding: FragmentAddUtilizationBinding
    var pdfUri: Uri? = null
    var imageUri: Uri? = null
    //api call
    lateinit var progressDialog: Dialog

    private lateinit var district_list: ArrayList<DashBoardSpinnerResponse>
    private lateinit var mandals_list: ArrayList<DashBoardSpinnerResponse>
    private lateinit var villege_list: ArrayList<DashBoardSpinnerResponse>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentAddUtilizationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = Common.progressDialog(requireContext())
        call_district_api()

        onClickListenrs()
    }

    private fun onClickListenrs() {
        binding.chooseFileLayout.setOnClickListener {
            val PICK_PDF_REQUEST = 1
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_REQUEST)
        }

        binding.chooseImageLayout.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)  {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 111)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        type = "image/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                    }
                    startActivityForResult(intent, 2)
                }else {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, 2)
                }
            }
        }

        binding.nextBtn.setOnClickListener {
            if(binding.syNoEt.text.toString().trim().isNotEmpty()){
                if(binding.exActEt.text.toString().trim().isNotEmpty()){
                    if(binding.classiEt.text.toString().trim().isNotEmpty()){
                        if(binding.coveredEt.text.toString().trim().isNotEmpty()){
                            binding.pageOneCard.visibility=View.GONE
                            binding.pageTwoCard.visibility=View.VISIBLE
                            /*if(binding.extentAcEt.text.toString().trim().isNotEmpty()){
                                if(binding.enterPurpose.text.toString().trim().isNotEmpty()){
                                    if(binding.extentAc2Et.text.toString().trim().isNotEmpty()){
                                        if(binding.firmNameEt.text.toString().trim().isNotEmpty()){
                                            if(binding.extentAc3Et.text.toString().trim().isNotEmpty()){
                                                if(binding.layoutEt.text.toString().trim().isNotEmpty()){
                                                    if(binding.extentAc4Et.text.toString().trim().isNotEmpty()){
                                                        if(binding.publicPurposeEt.text.toString().trim().isNotEmpty()){
                                                            if(binding.extentAc5Et.text.toString().trim().isNotEmpty()){
                                                                if(binding.reasonEt.text.toString().trim().isNotEmpty()){
                                                                    if(binding.clearLandEt.text.toString().trim().isNotEmpty()){
                                                                        if(binding.underPossesionEt.text.toString().trim().isNotEmpty()){
                                                                            binding.pageOneCard.visibility=View.GONE
                                                                            binding.pageTwoCard.visibility=View.VISIBLE
                                                                        }else{
                                                                            Toast.makeText(requireContext(), getString(R.string.please_enter_under_possie),Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }else{
                                                                        Toast.makeText(requireContext(), getString(R.string.please_enter_clear_land),Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }else{
                                                                    Toast.makeText(requireContext(), getString(R.string.please_enter_reason),Toast.LENGTH_SHORT).show()
                                                                }
                                                            }else{
                                                                Toast.makeText(requireContext(), getString(R.string.please_enter_extent),Toast.LENGTH_SHORT).show()
                                                            }
                                                        }else{
                                                            Toast.makeText(requireContext(), getString(R.string.please_enter_public_purpose),Toast.LENGTH_SHORT).show()
                                                        }
                                                    }else{
                                                        Toast.makeText(requireContext(), getString(R.string.please_enter_extent),Toast.LENGTH_SHORT).show()
                                                    }
                                                }else{
                                                    Toast.makeText(requireContext(), getString(R.string.please_enter_layout),Toast.LENGTH_SHORT).show()
                                                }
                                            }else{
                                                Toast.makeText(requireContext(), getString(R.string.please_enter_extent),Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            Toast.makeText(requireContext(), getString(R.string.please_enter_firm_name),Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(requireContext(), getString(R.string.please_enter_extent),Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(requireContext(), getString(R.string.please_enter_purpose),Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(requireContext(), getString(R.string.please_enter_extent),Toast.LENGTH_SHORT).show()
                            }*/
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.please_enter_covered),Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(requireContext(), getString(R.string.please_enter_classifi),Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), getString(R.string.please_enter_Ext),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), getString(R.string.please_enter_sy_no),Toast.LENGTH_SHORT).show()
            }
        }

        binding.submitBtn.setOnClickListener {
            if(binding.termsCheck.isChecked){
                call_Save_project_api()
            }else{
                Toast.makeText(requireContext(), getString(R.string.please_select_terms),Toast.LENGTH_SHORT).show()
            }
           /* if(binding.wpNoEt.text.toString().trim().isNotEmpty()){
                if(binding.gistEt.text.toString().trim().isNotEmpty()){
                    if(binding.presentEt.text.toString().trim().isNotEmpty()){
                        if(binding.spinnerDistrict.selectedItemPosition!=0){
                            if(binding.spinnerMandal.selectedItemPosition!=0){
                                if(binding.spinnerVillage.selectedItemPosition!=0){
                                    if(binding.locationEt.text.toString().trim().isNotEmpty()){
                                        if(binding.address1Et.text.toString().trim().isNotEmpty()){
                                            if(binding.latitude1Et.text.toString().trim().isNotEmpty()){
                                                if(binding.longidude1Et.text.toString().trim().isNotEmpty()){
                                                    if(binding.latitude2Et.text.toString().trim().isNotEmpty()){
                                                        if(binding.longitude2Et.text.toString().trim().isNotEmpty()){
                                                            if(binding.latitude3Et.text.toString().trim().isNotEmpty()){
                                                                if(binding.longitude3Et.text.toString().trim().isNotEmpty()){
                                                                    if(binding.latitude4Et.text.toString().trim().isNotEmpty()){
                                                                        if(binding.longitude4Et.text.toString().trim().isNotEmpty()){
                                                                            if(binding.remarksEt.text.toString().trim().isNotEmpty()){
                                                                                if(binding.termsCheck.isChecked){
                                                                                    call_Save_project_api()
                                                                                }else{
                                                                                    Toast.makeText(requireContext(), getString(R.string.please_select_terms),Toast.LENGTH_SHORT).show()
                                                                                }
                                                                            }else{
                                                                                Toast.makeText(requireContext(), getString(R.string.please_enter_remarks),Toast.LENGTH_SHORT).show()
                                                                            }
                                                                        }else{
                                                                            Toast.makeText(requireContext(), getString(R.string.please_enter_longitude4),Toast.LENGTH_SHORT).show()
                                                                        }
                                                                    }else{
                                                                        Toast.makeText(requireContext(), getString(R.string.please_enter_latitude4),Toast.LENGTH_SHORT).show()
                                                                    }
                                                                }else{
                                                                    Toast.makeText(requireContext(), getString(R.string.please_enter_longitude3),Toast.LENGTH_SHORT).show()
                                                                }
                                                            }else{
                                                                Toast.makeText(requireContext(), getString(R.string.please_enter_latitude3),Toast.LENGTH_SHORT).show()
                                                            }
                                                        }else{
                                                            Toast.makeText(requireContext(), getString(R.string.please_enter_longitude2),Toast.LENGTH_SHORT).show()
                                                        }
                                                    }else{
                                                        Toast.makeText(requireContext(), getString(R.string.please_enter_latitude2),Toast.LENGTH_SHORT).show()
                                                    }
                                                }else{
                                                    Toast.makeText(requireContext(), getString(R.string.please_enter_longitude1),Toast.LENGTH_SHORT).show()
                                                }
                                            }else{
                                                Toast.makeText(requireContext(), getString(R.string.please_enter_latitude1),Toast.LENGTH_SHORT).show()
                                            }
                                        }else{
                                            Toast.makeText(requireContext(), getString(R.string.please_enter_address),Toast.LENGTH_SHORT).show()
                                        }
                                    }else{
                                        Toast.makeText(requireContext(), getString(R.string.please_enter_location),Toast.LENGTH_SHORT).show()
                                    }
                                }else{
                                    Toast.makeText(requireContext(), getString(R.string.please_select_village),Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(requireContext(), getString(R.string.please_select_mandal),Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            Toast.makeText(requireContext(), getString(R.string.please_select_distrcit),Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(requireContext(), getString(R.string.please_enter_present),Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(requireContext(), getString(R.string.please_enter_gist),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(requireContext(), getString(R.string.please_enter_wp_no),Toast.LENGTH_SHORT).show()
            }*/
        }

    }

    private fun call_Save_project_api() {
        if (Common.isInternetAvailable(requireContext())) {
            try {
                progressDialog.show()

                val url = "http://vmrda.gov.in/ewpms_api/api/Usp_Ins_ProjectsLMS"

                val paramsMap = mutableMapOf<String, String>()

                // Create the JSON payload
                val jsonBody = JSONObject().apply {
                    put("ProjectId", "0")
                    put("Sno", "0")
                    put("SyNo", binding.syNoEt.text.toString().trim())
                    put("Ext_Ac", binding.exActEt.text.toString().trim())
                    put("P_Classification", binding.classiEt.text.toString().trim())
                    put("Covered_by", binding.coveredEt.text.toString().trim())
                    if (binding.extentAcEt.text.toString().trim().isNotEmpty()) {
                        put("Util_Extent_Ac", binding.extentAcEt.text.toString().trim())
                    } else {
                        put("Util_Extent_Ac", "0")
                    }
                    put("Util_NameofthePurpose", binding.enterPurpose.text.toString().trim())
                    if (binding.extentAc2Et.text.toString().trim().isNotEmpty()) {
                        put("Ali_Extent_Ac", binding.extentAc2Et.text.toString().trim())
                    } else {
                        put("Ali_Extent_Ac", "0")
                    }
                    put("Ali_NameoftheFirm", binding.firmNameEt.text.toString().trim())
                    if (binding.extentAc3Et.text.toString().trim().isNotEmpty()) {
                        put("Formed_Extent_Ac", binding.extentAc3Et.text.toString().trim())
                    } else {
                        put("Formed_Extent_Ac", "0")
                    }
                    put("Formed_NameoftheLayout", binding.layoutEt.text.toString().trim())
                    if (binding.extentAc4Et.text.toString().trim().isNotEmpty()) {
                        put("Public_Extent_Ac", binding.extentAc4Et.text.toString().trim())
                    } else {
                        put("Public_Extent_Ac", "0")
                    }
                    put("Public_purpose", binding.publicPurposeEt.text.toString().trim())
                    if (binding.extentAc5Et.text.toString().trim().isNotEmpty()) {
                        put("Unfit_Extent_Ac", binding.extentAc5Et.text.toString().trim())
                    } else {
                        put("Unfit_Extent_Ac", "0")
                    }
                    put("Unfit_Reason", binding.reasonEt.text.toString().trim())
                    if (binding.clearLandEt.text.toString().trim().isNotEmpty()) {
                        put("Clear_vacant_land", binding.clearLandEt.text.toString().trim())
                    } else {
                        put("Clear_vacant_land", "0")
                    }
                    if (binding.underPossesionEt.text.toString().trim().isNotEmpty()) {
                        put(
                            "Under_possession_of_others",
                            binding.underPossesionEt.text.toString().trim()
                        )
                    } else {
                        put("Under_possession_of_others", "0")
                    }
                    put("CourtCase_WP_No", binding.wpNoEt.text.toString().trim())
                    put("Gist_of_the_case", binding.gistEt.text.toString().trim())
                    put("PresentStatus", binding.presentEt.text.toString().trim())
                    put("Address1", binding.address1Et.text.toString().trim())
                    put("zone", "1")
                    put(
                        "Village",
                        villege_list[binding.spinnerVillage.selectedItemPosition].id.toString()
                    )
                    put(
                        "Mandal",
                        mandals_list[binding.spinnerMandal.selectedItemPosition].id.toString()
                    )
                    put(
                        "District",
                        district_list[binding.spinnerDistrict.selectedItemPosition].id.toString()
                    )
                    put("Latitude", binding.latitude1Et.text.toString().trim())
                    put("Longitude", binding.longidude1Et.text.toString().trim())
                    put("Latitide2", binding.latitude2Et.text.toString().trim())
                    put("Longitude2", binding.longitude2Et.text.toString().trim())
                    put("Latitide3", binding.latitude3Et.text.toString().trim())
                    put("Longitude3", binding.longitude3Et.text.toString().trim())
                    put("Latitide4", binding.latitude4Et.text.toString().trim())
                    put("Longitude4", binding.latitude4Et.text.toString().trim())
                    put("Location", binding.locationEt.text.toString().trim())
                    put("Remarks", binding.remarksEt.text.toString().trim())
                    //  put("Sold_Document_fn","")
                    put(
                        "UpdatedBy",
                        AppSharedPreferences.getStringSharedPreference(
                            requireContext(),
                            AppConstants.USERTYPE
                        )
                    )
                }

                jsonBody.keys().forEach { key ->
                    paramsMap[key] = jsonBody.getString(key)
                }

                Log.e("project_params ", jsonBody.toString())
                val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())

                if (pdfUri != null) {
                    val multipartRequest = object : MultipartRequest(
                        Method.POST,
                        url,
                        Response.Listener { response ->
                            try {
                                val responseString = String(response.data)
                                Log.e("Upload Success", responseString)

                                val jsonResponse = JSONArray(responseString)
                                for (i in 0 until jsonResponse.length()) {
                                    val jsonObject = jsonResponse.getJSONObject(i)
                                    if (jsonObject.has("projectId")) {
                                        val projectId = jsonObject.getString("projectId")
                                        val retVal = jsonObject.getString("RetVal")
                                        Log.e("Project ID", projectId)

                                        if (retVal == "Success") {
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.project_progress_updated_successfully),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startActivity(
                                                Intent(
                                                    requireContext(),
                                                    ProjectDetailsActivity::class.java
                                                )
                                                    .putExtra("id", projectId)
                                            )
                                            requireActivity().finish()
                                        } else {
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                requireContext(),
                                                getString(R.string.upload_failed) + ": $retVal",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                progressDialog.dismiss()
                                Log.e("API Error", "JSON Parsing error: ${e.message}")
                            }
                        },
                        Response.ErrorListener { error ->
                            progressDialog.dismiss()
                            error.printStackTrace()
                            Log.e("UploadError", "Error: ${error.message}")
                        }
                    ) {
                        override fun getParams(): Map<String, String> {
                            return emptyMap() // Pass all data in JSON body or file data
                        }

                        override fun getByteData(): Map<String, MultipartRequest.DataPart> {
                            val byteDataMap = mutableMapOf<String, MultipartRequest.DataPart>()

                            // Add files
                            pdfUri?.let {
                                val pdfFile = uriToFile(requireContext(), it, "file.pdf")
                                byteDataMap["FileUpload2"] = MultipartRequest.DataPart(
                                    "file.pdf",
                                    pdfFile!!.readBytes(),
                                    "application/pdf"
                                )
                            }

                            imageUri?.let {
                                val imageFile = uriToFile(requireContext(), it, "image.jpg")
                                byteDataMap["FileUpload3"] = MultipartRequest.DataPart(
                                    "image.jpg",
                                    imageFile!!.readBytes(),
                                    "image/jpeg"
                                )
                            }

                            return byteDataMap
                        }

                        override fun getBodyContentType(): String {
                            return "multipart/form-data; charset=utf-8; boundary=${boundary}" // Ensure proper content type
                        }
                    }

// Add request to queue
                    requestQueue.add(multipartRequest)
                } else {

                    val requestQueue: RequestQueue = Volley.newRequestQueue(requireContext())

                    val jsonArrayRequest = object : JsonArrayRequest(
                        Request.Method.POST,
                        url,
                        null, // Passing null here as the body will be sent in getBody
                        { response ->
                            try {
                                // Handle the JSONArray response
                                for (j in 0 until response.length()) {
                                    val jsonObject = response.getJSONObject(j)
                                    val retVal = jsonObject.getString("RetVal")
                                    val ProjectId = jsonObject.getString("ProjectId")
                                    Log.d("Response Value", "RetVal: $retVal")
                                    if (retVal == "Success") {
                                        progressDialog.dismiss()
                                        // Handle the successful response
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.project_progress_updated_successfully),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                ProjectDetailsActivity::class.java
                                            ).putExtra("id", ProjectId)
                                        )
                                        requireActivity().finish()
                                    } else {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            requireContext(),
                                            getString(R.string.upload_failed) + ": ${retVal}",
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
                            Toast.makeText(
                                requireContext(),
                                "API Request Failed: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
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
                }
            }catch (e: Exception) {
                if(progressDialog!=null) {
                    progressDialog.dismiss()
                }
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

    fun Uri.readBytes(contentResolver: ContentResolver): ByteArray {
        return contentResolver.openInputStream(this)?.use { inputStream ->
            inputStream.readBytes()
        } ?: throw IOException("Unable to open URI: $this")
    }


    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            uri?.let {
                val fileSizeInBytes = getFileSize(uri)

                if (fileSizeInBytes <= 3 * 1024 * 1024) { // Check if file size is less than or equal to 3MB
                    pdfUri=uri
                    val cursor = requireContext().contentResolver.query(it, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    cursor?.moveToFirst()
                    val pdfName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor?.close()

                    binding.fileNameTv.text = pdfName
                } else {
                    Toast.makeText(requireContext(), "Please select file size below 3MB", Toast.LENGTH_SHORT).show()
                }
            }
        }else  if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri= data.data
            imageUri?.let {
                // 3. Get the file name from the URI
                val cursor = requireContext().contentResolver.query(it, arrayOf(MediaStore.Images.Media.DISPLAY_NAME), null, null, null)
                cursor?.moveToFirst()
                val imageName = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                cursor?.close()

                // 4. Set the file name to the TextView
                binding.imageNameTv.text = imageName
            }
        }
    }

    private fun getFileSize(uri: Uri): Long {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        var size: Long = 0
        cursor?.use {
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            size = it.getLong(sizeIndex)
        }
        return size
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
                                for(i in villege_list.indices){
                                    villege_name_list.add(villege_list[i].name.toString())
                                }
                                lateinit var vilelge_adapter: ArrayAdapter<String>
                                vilelge_adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, villege_name_list)
                                binding.spinnerVillage.adapter = vilelge_adapter
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

    //uri to file
    fun uriToFile(context: Context, uri: Uri, fileName: String): File? {
        val file = File(context.cacheDir, fileName) // Temporary file
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            inputStream = context.contentResolver.openInputStream(uri)
            outputStream = FileOutputStream(file)

            // Copy content from the URI to the file
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream?.read(buffer).also { length = it ?: -1 } != -1) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return file
    }

    fun getBytesFromFile(file: File): ByteArray {
        return try {
            file.readBytes()
        } catch (e: IOException) {
            e.printStackTrace()
            byteArrayOf()
        }
    }
}