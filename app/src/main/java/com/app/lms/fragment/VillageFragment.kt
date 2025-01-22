package com.app.lms.fragment

import android.Manifest
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.EWPMS.utilities.AppConstants
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.Adapter.VillageAdapter
import com.app.lms.MainActivity
import com.app.lms.R
import com.app.lms.dataResponse.VillageDataResponse
import com.app.lms.databinding.FragmentVillageBinding
import com.app.lms.utilities.AppSharedPreferences
import com.app.lms.utilities.Common
import com.finowizx.CallBackInterface.CallBackData
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException

class VillageFragment : Fragment(),CallBackData {
    private lateinit var binding: FragmentVillageBinding
    //api call
    lateinit var progressDialog: Dialog
    private lateinit var village_list: ArrayList<VillageDataResponse>
    private lateinit var village_name_list: ArrayList<VillageDataResponse>
    private lateinit var villageAdapter: VillageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentVillageBinding.inflate(inflater)
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
                village_name_list = ArrayList<VillageDataResponse>()
                village_name_list.clear()

                if (binding.searchEdt.text.toString().isNotEmpty()) {
                    if (binding.searchEdt.text.toString().length >= 2) {
                        for (i in village_list.indices) {
                            if (village_list[i].DistrictName.lowercase().substring(0, 2) == binding.searchEdt.text.toString().toLowerCase().substring(0, 2)) {
                                village_name_list.add(village_list[i])
                            }
                        }

                        if(village_name_list.size>0) {
                            binding.noDataLayout.visibility=View.GONE
                            binding.villageRv.visibility=View.VISIBLE

                            villageAdapter = VillageAdapter(requireContext(), village_name_list,this@VillageFragment)
                            binding.villageRv.adapter = villageAdapter
                        }else{
                            binding.noDataLayout.visibility=View.VISIBLE
                            binding.villageRv.visibility=View.GONE
                        }
                    }else{
                        binding.noDataLayout.visibility=View.GONE
                        binding.villageRv.visibility=View.VISIBLE

                        villageAdapter = VillageAdapter(requireContext(), village_list,this@VillageFragment)
                        binding.villageRv.adapter = villageAdapter
                    }
                }
            }
        })

    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(requireContext())

        call_village_list()
    }

    private fun call_village_list() {
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
                                binding.noDataLayout.visibility = View.GONE
                                binding.villageRv.visibility = View.VISIBLE

                                if(village_list.size>=10){
                                    var new_list=ArrayList<VillageDataResponse>()
                                    binding.previousBtn.isEnabled = true
                                    binding.nextBtn.isEnabled = true
                                    binding.pageNoTv.text = "1"

                                    for(i in village_list.indices){
                                        if(i<10){
                                            new_list.add(village_list[i])
                                        }
                                    }
                                    villageAdapter = VillageAdapter(
                                        requireContext(),
                                        new_list,
                                        this@VillageFragment
                                    )
                                    binding.villageRv.adapter = villageAdapter
                                }else {
                                    binding.previousBtn.isEnabled = false
                                    binding.nextBtn.isEnabled = false
                                    binding.pageNoTv.text = "1"

                                    villageAdapter = VillageAdapter(
                                        requireContext(),
                                        village_list,
                                        this@VillageFragment
                                    )
                                    binding.villageRv.adapter = villageAdapter
                                }
                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()
                                binding.noDataLayout.visibility = View.VISIBLE
                                binding.villageRv.visibility = View.GONE
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

    private fun onClickListners() {
        binding.addBtn.setOnClickListener {
            val addVillageFragment = AddVillageFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, addVillageFragment)
            transaction.commit()

            (activity as? MainActivity)?.updateTextView(getString(R.string.add_village))
        }

        binding.previousBtn.setOnClickListener {
            if(binding.pageNoTv.text.toString()!="1") {
                var new_list = ArrayList<VillageDataResponse>()
                binding.pageNoTv.text = (binding.pageNoTv.text.toString().toInt()-1).toString()

                for (i in village_list.indices) {
                    if (i.toString().length >= 2) {
                        if ((binding.pageNoTv.text.toString()
                                .toInt() - 1).toString() == i.toString().substring(0, 1)
                        ) {
                            new_list.add(village_list[i])
                        }
                    }
                }

                if(new_list.size <= 0){
                    for(j in village_list.indices){
                        if(j<10){
                            new_list.add(village_list[j])
                        }
                    }
                }

                villageAdapter = VillageAdapter(
                    requireContext(),
                    new_list,
                    this@VillageFragment
                )
                binding.villageRv.adapter = villageAdapter
            }
        }

        binding.nextBtn.setOnClickListener {
            var new_list = ArrayList<VillageDataResponse>()
            for (i in village_list.indices) {
                if (i.toString().length >= 2) {
                    if(binding.pageNoTv.text.toString() == "1") {
                        if ((binding.pageNoTv.text.toString() == i.toString().substring(0, 1))) {
                            new_list.add(village_list[i])
                        }
                    }else if (binding.pageNoTv.text.toString() == i.toString().substring(0, 1)) {
                        new_list.add(village_list[i])
                    }
                }
            }

            if(new_list.size>0) {
                binding.pageNoTv.text = (binding.pageNoTv.text.toString().toInt() +1).toString()
                villageAdapter = VillageAdapter(
                    requireContext(),
                    new_list,
                    this@VillageFragment
                )
                binding.villageRv.adapter = villageAdapter
            }
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
        bundle.putString("village_id", status.toString())

        val addVillageFragment = AddVillageFragment()
        addVillageFragment.arguments = bundle

        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, addVillageFragment)
        transaction.commit()

        (activity as? MainActivity)?.updateTextView(getString(R.string.add_village))
    }

    fun convertLinearLayoutToExcel() {
        try {
            progressDialog.show()
            // Create a new workbook and sheet
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("village_data")

            // Create header row
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("S.No")
            headerRow.createCell(1).setCellValue("District")
            headerRow.createCell(2).setCellValue("Mandal")
            headerRow.createCell(3).setCellValue("Village")

            // Populate data rows
            for ((index, person) in village_list.withIndex()) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(person.Sno.toString())
                row.createCell(1).setCellValue(person.DistrictName)
                row.createCell(2).setCellValue(person.MandalName)
                row.createCell(3).setCellValue(person.VillageName)
            }

            // Define the file name and path
            val fileName = "villageList("+ AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERTYPE)+").xlsx"
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
            val fileName = "villageList(${AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERTYPE)}).csv"
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
                        writer.append("S.No,District,Mandal,Village\n")

                        // Populate data rows
                        for (person in village_list) {
                            writer.append("${person.Sno},")
                                .append("\"${person.DistrictName}\",") // Enclose in quotes to handle commas
                                .append("${person.MandalName},")
                                .append("${person.VillageName}\n")
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
            val fileName = "villageList(${AppSharedPreferences.getStringSharedPreference(requireContext(), AppConstants.USERTYPE)}).pdf"
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
                    canvas.drawText("District", xPosition + 100, yPosition, paint)
                    canvas.drawText("Mandal", xPosition + 300, yPosition, paint)
                    canvas.drawText("Village", xPosition + 450, yPosition, paint)

                    // Increment yPosition for the next row
                    yPosition += 30f

                    // Populate data rows
                    for (person in village_list) {
                        canvas.drawText(person.Sno.toString(), xPosition, yPosition, paint)
                        canvas.drawText(person.DistrictName, xPosition + 100, yPosition, paint)
                        canvas.drawText(person.MandalName, xPosition + 300, yPosition, paint)
                        canvas.drawText(person.VillageName, xPosition + 450, yPosition, paint)
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
        val headers = "S.No,District,Mandal,Village"
        val dataRows = village_list.joinToString(separator = "\n") { vilage ->
            "${vilage.Sno},${vilage.DistrictName},${vilage.MandalName},${vilage.VillageName}"
        }
        val csvData = "$headers\n$dataRows"

        // Step 2: Access the Clipboard Manager
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Step 3: Create a ClipData object
        val clipData = ClipData.newPlainText("Village Data", csvData)

        // Step 4: Set the clip to the clipboard
        clipboardManager.setPrimaryClip(clipData)

        // Optional: Notify the user
        Toast.makeText(context, "Village data copied to clipboard.", Toast.LENGTH_SHORT).show()
    }
}