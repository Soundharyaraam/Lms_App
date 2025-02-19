package com.app.lms.activity

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.app.lms.MainActivity
import com.app.lms.R
import com.app.lms.databinding.ActivityProjectDetailsBinding
import com.app.lms.utilities.Common
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONException

class ProjectDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDetailsBinding
    //api call
    lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityProjectDetailsBinding.inflate(layoutInflater)
        Common.fullScreen(window)
        setContentView(binding.root)

        callCommonClass()

        binding.backImg.setOnClickListener {
            startActivity(Intent(this@ProjectDetailsActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@ProjectDetailsActivity, MainActivity::class.java))
        finish()
    }

    private fun callCommonClass() {
        progressDialog = Common.progressDialog(this)

        var value=intent.getStringExtra("id").toString()
        call_projects_api(value)
    }

    private fun call_projects_api(id:String) {
        try {
            if (Common.isInternetAvailable(this)) {
                progressDialog.show()
                val url ="http://vmrda.gov.in/ewpms_api/api/Usp_get_ProjectDetailsLMS/?id="+id.toString()
                Log.d("API_URL", url)

                val queue = Volley.newRequestQueue(this)
                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        try {
                            val jsonArray = JSONArray(response)
                            Log.d("Response", response)

                                val jsonObject = jsonArray.getJSONObject(0)

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

                            if (SyNo!=null) {
                                binding.syNoTv.text=SyNo.toString()
                                binding.classificationTxt.text=P_Classification.toString()
                                binding.remarksTxt.text=Remarks.toString()
                                binding.coveredTxt.text=Covered_by.toString()
                                binding.extentPurposeTxt.text="Extent (Ac.): "+Util_Extent_Ac.toString()
                                binding.purposeNameTxt.text="Name of the purpose: "+Util_NameofthePurpose.toString()
                                binding.extentSoldTxt.text="Extent (Ac.): "+Ali_Extent_Ac.toString()
                                binding.firmNameTxt.text="Name of the firm: "+Ali_NameoftheFirm.toString()

                                binding.extentFormedTxt.text="Extent (Ac.): "+Formed_Extent_Ac.toString()
                                binding.formNameTxt.text="Name of the Layout: "+Formed_NameoftheLayout.toString()

                                binding.extentPublicTxt.text="Extent (Ac.): "+Public_Extent_Ac.toString()
                                binding.pubPrposeTxt.text="Public Purpose "+Public_purpose.toString()

                                binding.extentUnitTxt.text="Extent (Ac.): "+Unfit_Extent_Ac.toString()
                                binding.reasonUnitTxt.text="Reason: "+Unfit_Reason.toString()

                                binding.clearLandTxt.text=": "+Clear_vacant_land.toString()
                                binding.underPossTxt.text=": "+Under_possession_of_others.toString()

                                binding.wpNoTxt.text=": "+CourtCase_WP_No.toString()
                                binding.presentStatusTxt.text=": "+PresentStatus.toString()

                                binding.mapIcon.setOnClickListener {
                                    val uri = "geo:$Latitude,$Longitude?q=$Latitude,$Longitude($VillageName)"
                                    println("URI: $uri") // Log the URI

                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                    intent.setPackage("com.google.android.apps.maps") // Ensure it opens in Google Maps

                                    val resolvedActivity = intent.resolveActivity(packageManager)
                                    println("Resolved Activity: $resolvedActivity") // Log the resolved activity

                                    if (resolvedActivity != null) {
                                        startActivity(intent)
                                    } else {
                                        // Fallback to web browser
                                        val webUri = "https://www.google.com/maps/search/?api=1&query=$Latitude,$Longitude"
                                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUri))
                                        if (webIntent.resolveActivity(packageManager) != null) {
                                            startActivity(webIntent)
                                        } else {
                                            Toast.makeText(this, "No app to handle the request", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }

                                binding.gallaryIcon.setOnClickListener {
                                    if (fn1.isNotEmpty()) {
                                        val dialog = Dialog(this@ProjectDetailsActivity)
                                        dialog.setContentView(R.layout.popup_project)
                                        dialog.setCancelable(false)
                                        dialog.setCanceledOnTouchOutside(false)
                                        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

                                        // Set the width of the dialog
                                        val displayMetrics = DisplayMetrics()
                                        windowManager.defaultDisplay.getMetrics(displayMetrics)
                                        val width = displayMetrics.widthPixels
                                        dialog.window?.setLayout(
                                            (width * 0.9).toInt(),
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )

                                        var yes_btn = dialog.findViewById<Button>(R.id.close_btn_gallary)
                                        var geo_layout =
                                            dialog.findViewById<LinearLayout>(R.id.geo_layout)
                                        var webview_layout =
                                            dialog.findViewById<LinearLayout>(R.id.webview_layout)
                                        var galary_view_layout =
                                            dialog.findViewById<LinearLayout>(R.id.gallary_view_layout)
                                        var imageview =
                                            dialog.findViewById<ImageView>(R.id.gallary_image)

                                        webview_layout.visibility = View.GONE
                                        geo_layout.visibility = View.GONE
                                        galary_view_layout.visibility = View.VISIBLE

                                        Glide.with(this).load(fn1).into(imageview)

                                        yes_btn.setOnClickListener {
                                            dialog.dismiss()
                                        }

                                        dialog.show()
                                    } else {
                                        Toast.makeText(
                                            this@ProjectDetailsActivity,
                                            getString(R.string.image_not_available),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                binding.markerImg.setOnClickListener {
                                    val dialog = Dialog(this@ProjectDetailsActivity)
                                    dialog.setContentView(R.layout.popup_project)
                                    dialog.setCancelable(false)
                                    dialog.setCanceledOnTouchOutside(false)
                                    dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

                                    // Set the width of the dialog
                                    val displayMetrics = DisplayMetrics()
                                    windowManager.defaultDisplay.getMetrics(displayMetrics)
                                    val width = displayMetrics.widthPixels
                                    dialog.window?.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

                                    var yes_btn = dialog.findViewById<Button>(R.id.yesButton)
                                    var geo_tv = dialog.findViewById<TextView>(R.id.geo_tv)
                                    var geo_layout = dialog.findViewById<LinearLayout>(R.id.geo_layout)
                                    var webview_layout = dialog.findViewById<LinearLayout>(R.id.webview_layout)
                                    var galary_view_layout = dialog.findViewById<LinearLayout>(R.id.gallary_view_layout)

                                    galary_view_layout.visibility=View.GONE
                                    webview_layout.visibility=View.GONE
                                    geo_layout.visibility=View.VISIBLE

                                    if(Address1.isNotEmpty()) {
                                        geo_tv.text = Address1
                                    }else{
                                        geo_tv.text = VillageName+","+MandalName+","+DistrictName
                                    }

                                    yes_btn.setOnClickListener {
                                       dialog.dismiss()
                                    }

                                    dialog.show()
                                }

                                binding.locationIcon.setOnClickListener {
                                    val dialog = Dialog(this@ProjectDetailsActivity)
                                    dialog.setContentView(R.layout.popup_project)
                                    dialog.setCancelable(false)
                                    dialog.setCanceledOnTouchOutside(false)
                                    dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

                                    // Set the width of the dialog
                                    val displayMetrics = DisplayMetrics()
                                    windowManager.defaultDisplay.getMetrics(displayMetrics)
                                    val width = displayMetrics.widthPixels
                                    dialog.window?.setLayout((width * 0.9).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

                                    var yes_btn = dialog.findViewById<Button>(R.id.yesButton)
                                    var geo_tv = dialog.findViewById<TextView>(R.id.geo_tv)
                                    var geo_layout = dialog.findViewById<LinearLayout>(R.id.geo_layout)
                                    var webview_layout = dialog.findViewById<LinearLayout>(R.id.webview_layout)

                                    webview_layout.visibility=View.GONE
                                    geo_layout.visibility=View.VISIBLE

                                    if(Latitude.isNotEmpty()) {
                                        geo_tv.text = Latitude+" , "+Longitude
                                    }

                                    yes_btn.setOnClickListener {
                                       dialog.dismiss()
                                    }

                                    dialog.show()
                                }

                                binding.pdf1.setOnClickListener {
                                    if (Sold_Document_fn.isNotEmpty()) {
                                        val dialog = Dialog(this@ProjectDetailsActivity)
                                        dialog.setContentView(R.layout.popup_project)
                                        dialog.setCancelable(false)
                                        dialog.setCanceledOnTouchOutside(false)
                                        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

                                        // Set the width of the dialog
                                        val displayMetrics = DisplayMetrics()
                                        windowManager.defaultDisplay.getMetrics(displayMetrics)
                                        val width = displayMetrics.widthPixels
                                        dialog.window?.setLayout(
                                            (width * 0.9).toInt(),
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )

                                        var yes_btn = dialog.findViewById<Button>(R.id.close_btn)
                                        var webview = dialog.findViewById<WebView>(R.id.webview)
                                        var geo_layout =
                                            dialog.findViewById<LinearLayout>(R.id.geo_layout)
                                        var webview_layout =
                                            dialog.findViewById<LinearLayout>(R.id.webview_layout)
                                        var galary_view_layout = dialog.findViewById<LinearLayout>(R.id.gallary_view_layout)

                                        galary_view_layout.visibility=View.GONE
                                        geo_layout.visibility = View.GONE
                                        webview_layout.visibility = View.VISIBLE

                                        webview.settings.javaScriptEnabled = true
                                        // Load the URL
                                        webview.loadUrl(Sold_Document_fn)

                                        yes_btn.setOnClickListener {
                                            dialog.dismiss()
                                        }

                                        dialog.show()
                                    }else{
                                        Toast.makeText(this@ProjectDetailsActivity,
                                            getString(R.string.pdf_not_available),Toast.LENGTH_SHORT).show()
                                    }
                                }

                                binding.pdf2.setOnClickListener {
                                    if (Sold_Document_fn1.isNotEmpty()) {
                                        val dialog = Dialog(this@ProjectDetailsActivity)
                                        dialog.setContentView(R.layout.popup_project)
                                        dialog.setCancelable(false)
                                        dialog.setCanceledOnTouchOutside(false)
                                        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

                                        // Set the width of the dialog
                                        val displayMetrics = DisplayMetrics()
                                        windowManager.defaultDisplay.getMetrics(displayMetrics)
                                        val width = displayMetrics.widthPixels
                                        dialog.window?.setLayout(
                                            (width * 0.9).toInt(),
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )

                                        var yes_btn = dialog.findViewById<Button>(R.id.close_btn)
                                        var webview = dialog.findViewById<WebView>(R.id.webview)
                                        var geo_layout =
                                            dialog.findViewById<LinearLayout>(R.id.geo_layout)
                                        var webview_layout =
                                            dialog.findViewById<LinearLayout>(R.id.webview_layout)

                                        geo_layout.visibility = View.GONE
                                        webview_layout.visibility = View.VISIBLE

                                        webview.settings.javaScriptEnabled = true
                                        // Load the URL
                                        webview.loadUrl(Sold_Document_fn1)

                                        yes_btn.setOnClickListener {
                                            dialog.dismiss()
                                        }

                                        dialog.show()
                                    }else{
                                        Toast.makeText(this@ProjectDetailsActivity,
                                            getString(R.string.pdf_not_available),Toast.LENGTH_SHORT).show()
                                    }
                                }

                                progressDialog.dismiss()
                            } else {
                                progressDialog.dismiss()
                            }

                        } catch (e: JSONException) {
                            Log.e("JSONError", "Parsing error", e)
                            progressDialog.dismiss()
                            Toast.makeText(
                                this,
                                getString(R.string.response_failure_please_try_again),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { error ->
                        progressDialog.dismiss()
                        Log.e("VolleyError", "Request failed", error)
                        Toast.makeText(
                            this,
                            getString(R.string.response_failure_please_try_again),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                queue.add(stringRequest)
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.please_check_with_the_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}