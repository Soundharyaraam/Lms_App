package com.app.lms.fragment

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.Adapter.EmployeeAdapter
import com.app.lms.MainActivity
import com.app.lms.R
import com.app.lms.dataResponse.DashBoardSpinnerResponse
import com.app.lms.dataResponse.EmployeeData
import com.app.lms.databinding.FragmentEmployeeMasterBinding
import com.app.lms.utilities.Common
import com.finowizx.CallBackInterface.CallBackData
import org.json.JSONArray
import org.json.JSONException
import android.os.Environment
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import android.graphics.pdf.PdfDocument
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import com.EWPMS.utilities.AppConstants
import com.app.lms.Adapter.VillageAdapter
import com.app.lms.dataResponse.VillageDataResponse
import com.app.lms.utilities.AppSharedPreferences
import java.io.IOException

class EmployeeMasterFragment : Fragment(),CallBackData {
    private lateinit var binding: FragmentEmployeeMasterBinding

    //api call
    lateinit var progressDialog: Dialog
    private lateinit var employee_list: ArrayList<EmployeeData>
    private lateinit var employee_name_list: ArrayList<EmployeeData>
    private lateinit var employeeAdapter: EmployeeAdapter

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

        binding = FragmentEmployeeMasterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callCommonClass()
        onClickListners()

        binding.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                employee_name_list = ArrayList<EmployeeData>()
                employee_name_list.clear()

                if (binding.searchEdt.text.toString().isNotEmpty()) {
                    if (binding.searchEdt.text.toString().length >= 2) {
                        for (i in employee_list.indices) {
                            if (employee_list[i].EmployeeName.lowercase().substring(0, 2) == binding.searchEdt.text.toString().toLowerCase().substring(0, 2)) {
                                employee_name_list.add(employee_list[i])
                            }
                        }

                        if(employee_name_list.size>0) {
                            binding.noDataLayout.visibility=View.GONE
                            binding.empRv.visibility=View.VISIBLE

                            binding.empRv.adapter = EmployeeAdapter(requireContext(), employee_name_list,this@EmployeeMasterFragment)
                        }else{
                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.empRv.visibility=View.GONE
                        }
                    }else{
                        binding.noDataLayout.visibility=View.GONE
                        binding.empRv.visibility=View.VISIBLE

                        binding.empRv.adapter = EmployeeAdapter(requireContext(), employee_list,this@EmployeeMasterFragment)
                    }
                }
            }
        })

    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        call_employe_list()
    }

    private fun call_employe_list() {
        try {
            if (Common.isInternetAvailable(requireContext())) {
                progressDialog.show()
                val url ="http://vmrda.gov.in/ewpms_api/api/Usp_Get_Employees/"
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(requireContext())
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            employee_list = ArrayList<EmployeeData>()
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)

                                // Extract the required fields from the JSON object
                                var Sno = jsonObject.optString("Sno")
                                var EmployeeID = jsonObject.optString("EmployeeID")
                                var EmployeeName = jsonObject.optString("EmployeeName")
                                var LoginName = jsonObject.optString("LoginName")
                                var MobileNo = jsonObject.optString("MobileNo")
                                var EmailID = jsonObject.optString("EmailID")

                                // Create a new MyWorksResponse object and add it to the list
                                val employee_Item = EmployeeData(
                                    EmailID,EmployeeID,EmployeeName,
                                    LoginName,MobileNo,Sno
                                )

                                employee_list.add(employee_Item)
                            }

                            if (employee_list.size > 0) {
                                binding.noDataLayout.visibility = View.GONE
                                binding.empRv.visibility = View.VISIBLE

                                if(employee_list.size>=10){
                                    var new_list=ArrayList<EmployeeData>()
                                    binding.previousBtn.isEnabled = true
                                    binding.nextBtn.isEnabled = true
                                    binding.pageNoTv.text = "1"

                                    for(i in employee_list.indices){
                                        if(i<10){
                                            new_list.add(employee_list[i])
                                        }
                                    }
                                    employeeAdapter = EmployeeAdapter(
                                        requireContext(),
                                        new_list,
                                        this@EmployeeMasterFragment
                                    )
                                    binding.empRv.adapter = employeeAdapter
                                }else {
                                    binding.previousBtn.isEnabled = false
                                    binding.nextBtn.isEnabled = false
                                    binding.pageNoTv.text = "1"

                                    employeeAdapter = EmployeeAdapter(
                                        requireContext(),
                                        employee_list,
                                        this@EmployeeMasterFragment
                                    )
                                    binding.empRv.adapter = employeeAdapter
                                }
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()
                                binding.noDataLayout.visibility = View.VISIBLE
                                binding.empRv.visibility = View.GONE
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
                                Toast.LENGTH_SHORT).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT).show()
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
                            requireContext(), getString(R.string.response_failure_please_try_again), Toast.LENGTH_SHORT).show()
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
                                    Toast.LENGTH_SHORT).show()
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

    private fun onClickListners() {

        binding.previousBtn.setOnClickListener {
            if(binding.pageNoTv.text.toString()!="1") {
                var new_list = ArrayList<EmployeeData>()
                binding.pageNoTv.text = (binding.pageNoTv.text.toString().toInt()-1).toString()

                for (i in employee_list.indices) {
                    if (i.toString().length >= 2) {
                        if ((binding.pageNoTv.text.toString()
                                .toInt() - 1).toString() == i.toString().substring(0, 1)
                        ) {
                            new_list.add(employee_list[i])
                        }
                    }
                }

                if(new_list.size <= 0){
                    for(j in employee_list.indices){
                        if(j<10){
                            new_list.add(employee_list[j])
                        }
                    }
                }

                employeeAdapter = EmployeeAdapter(
                    requireContext(),
                    new_list,
                    this@EmployeeMasterFragment
                )
                binding.empRv.adapter = employeeAdapter
            }
        }

        binding.nextBtn.setOnClickListener {
            var new_list = ArrayList<EmployeeData>()
            for (i in employee_list.indices) {
                if (i.toString().length >= 2) {
                    if(binding.pageNoTv.text.toString() == "1") {
                        if ((binding.pageNoTv.text.toString() == i.toString().substring(0, 1))) {
                            new_list.add(employee_list[i])
                        }
                    }else if (binding.pageNoTv.text.toString() == i.toString().substring(0, 1)) {
                        new_list.add(employee_list[i])
                    }
                }
            }

            if(new_list.size>0) {
                binding.pageNoTv.text = (binding.pageNoTv.text.toString().toInt() +1).toString()
                employeeAdapter = EmployeeAdapter(
                    requireContext(),
                    new_list,
                    this@EmployeeMasterFragment
                )
                binding.empRv.adapter = employeeAdapter
            }
        }

        binding.addBtn.setOnClickListener {
            val taskFragment = AddEmployeeFragment()
            requireFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, taskFragment)
                .addToBackStack(null)
                .commit()
            (activity as? MainActivity)?.updateTextView(getString(R.string.add_employe))
        }

        binding.excelBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)  {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 111)
            } else {
                convertLinearLayoutToExcel()
            }
        }

        binding.csvBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)  {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 112)
            } else {
                convertLinearLayoutToCsv()
            }
        }

        binding.pdfBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)  {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 113)
            } else {
                convertLinearLayoutToPdf()
            }
        }

        binding.copyBtn.setOnClickListener {
            copyLinearLayoutDataToClipboard(requireContext())
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            111 -> {
                convertLinearLayoutToExcel()
                return
            }
            112 -> {
                convertLinearLayoutToCsv()
                return
            }
            113 -> {
                convertLinearLayoutToPdf()
                return
            }
        }
    }

    override fun getTaskStatus(status: String, id: String) {
        val bundle = Bundle()
        bundle.putString("emp_id", status.toString())

        val addEmployeeFragment = AddEmployeeFragment()
        addEmployeeFragment.arguments = bundle

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, addEmployeeFragment)
        transaction.commit()

        (activity as? MainActivity)?.updateTextView(getString(R.string.add_employe))
    }

    fun convertLinearLayoutToExcel() {
        try {
            progressDialog.show()
            // Create a new workbook and sheet
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("emp_data")

            // Create header row
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("S.No")
            headerRow.createCell(1).setCellValue("Employee Name")
            headerRow.createCell(2).setCellValue("Mobile")
            headerRow.createCell(3).setCellValue("Email")

            // Populate data rows
            for ((index, person) in employee_list.withIndex()) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(person.Sno.toString())
                row.createCell(1).setCellValue(person.EmployeeName)
                row.createCell(2).setCellValue(person.MobileNo)
                row.createCell(3).setCellValue(person.EmailID)
            }

            // Define the file name and path
            val fileName = "empList("+AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERTYPE)+").xlsx"
            var directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/LMS")
            } else {
                File(Environment.getExternalStorageDirectory().toString() + "/LMS")
            }
            if (directory != null && (directory.exists() || directory.mkdirs())) {
                val excelFile = File(directory, fileName)
                excelFile.parentFile?.mkdirs()
                try {
                    FileOutputStream(excelFile).use { outputStream ->
                        workbook.write(outputStream)
                        progressDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Excel file saved: ${excelFile.absolutePath}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Error saving file: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    workbook.close()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to create directory.", Toast.LENGTH_SHORT)
                    .show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun convertLinearLayoutToCsv() {
        try {
            progressDialog.show()

            // Define the file name and path
            val fileName = "empList(${AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERTYPE)}).csv"
            val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/LMS")
            } else {
                File(Environment.getExternalStorageDirectory().toString() + "/LMS")
            }

            if (directory.exists() || directory.mkdirs()) {
                val csvFile = File(directory, fileName)

                try {
                    FileWriter(csvFile).use { writer ->
                        // Write header row
                        writer.append("S.No,Employee Name,Mobile,Email\n")

                        // Populate data rows
                        for (person in employee_list) {
                            writer.append("${person.Sno},")
                                .append("\"${person.EmployeeName}\",") // Enclose in quotes to handle commas
                                .append("${person.MobileNo},")
                                .append("${person.EmailID}\n")
                        }
                    }
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "CSV file saved: ${csvFile.absolutePath}",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Error saving file: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to create directory.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun convertLinearLayoutToPdf() {
        try {
            progressDialog.show()

            // Define the file name and path
            val fileName = "empList(${AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERTYPE)}).pdf"
            val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/LMS")
            } else {
                File(Environment.getExternalStorageDirectory().toString() + "/LMS")
            }

            if (directory.exists() || directory.mkdirs()) {
                val pdfFile = File(directory, fileName)

                try {
                    // Create a new PdfDocument
                    val pdfDocument = PdfDocument()

                    // Create a PageInfo object with desired page width and height
                    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points

                    // Start a page
                    val page = pdfDocument.startPage(pageInfo)

                    // Get the Canvas object from the page
                    val canvas = page.canvas

                    // Set up paint for drawing text
                    val paint = Paint()
                    paint.color = Color.BLACK
                    paint.textSize = 12f

                    // Define starting positions
                    var xPosition = 50f
                    var yPosition = 50f

                    // Write header row
                    canvas.drawText("S.No", xPosition, yPosition, paint)
                    canvas.drawText("Employee Name", xPosition + 100, yPosition, paint)
                    canvas.drawText("Mobile", xPosition + 300, yPosition, paint)
                    canvas.drawText("Email", xPosition + 450, yPosition, paint)

                    // Increment yPosition for the next row
                    yPosition += 30f

                    // Populate data rows
                    for (person in employee_list) {
                        canvas.drawText(person.Sno.toString(), xPosition, yPosition, paint)
                        canvas.drawText(person.EmployeeName, xPosition + 100, yPosition, paint)
                        canvas.drawText(person.MobileNo, xPosition + 300, yPosition, paint)
                        canvas.drawText(person.EmailID, xPosition + 450, yPosition, paint)
                        yPosition += 30f
                    }

                    // Finish the page
                    pdfDocument.finishPage(page)

                    // Write the document content
                    FileOutputStream(pdfFile).use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }

                    // Close the document
                    pdfDocument.close()

                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "PDF file saved: ${pdfFile.absolutePath}",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: IOException) {
                    e.printStackTrace()
                    progressDialog.dismiss()
                    Toast.makeText(
                        requireContext(),
                        "Error saving file: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to create directory.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyLinearLayoutDataToClipboard(context: Context) {
        // Step 1: Convert data to CSV format
        val headers = "S.No,Employee Name,Mobile,Email"
        val dataRows = employee_list.joinToString(separator = "\n") { employee ->
            "${employee.Sno},${employee.EmployeeName},${employee.MobileNo},${employee.EmailID}"
        }
        val csvData = "$headers\n$dataRows"

        // Step 2: Access the Clipboard Manager
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Step 3: Create a ClipData object
        val clipData = ClipData.newPlainText("Employee Data", csvData)

        // Step 4: Set the clip to the clipboard
        clipboardManager.setPrimaryClip(clipData)

        // Optional: Notify the user
        Toast.makeText(context, "Employees data copied to clipboard.", Toast.LENGTH_SHORT).show()
    }
}