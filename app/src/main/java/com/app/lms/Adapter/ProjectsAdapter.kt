package com.app.lms.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.lms.R
import com.app.lms.dataResponse.ProjectsListResponse
import com.app.lms.databinding.UtilizationAdapterBinding
import com.finowizx.CallBackInterface.CallBackData

class ProjectsAdapter  (
    private val context: Context,
    private val list: List<ProjectsListResponse>,
    private val callBackStatus: CallBackData,
) : RecyclerView.Adapter<ProjectsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: UtilizationAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UtilizationAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

        holder.binding.utilizedAmount.text=data.Ext_Ac.toString()

        holder.binding.utilizationTv.text=context.getString(R.string.utilize_land_txt)+" "+data.P_Classification.toString()
        holder.binding.utilizationLayout.setOnClickListener {
            callBackStatus.getTaskStatus(data.ProjectId.toString(),position.toString())
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}