package com.app.lms.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.lms.dataResponse.VillageDataResponse
import com.app.lms.databinding.EmployeMasterAdapterBinding
import com.finowizx.CallBackInterface.CallBackData

class VillageAdapter(
    private val context: Context,
    private val list: ArrayList<VillageDataResponse>,
    private val callBackData: CallBackData
) : RecyclerView.Adapter<VillageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: EmployeMasterAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EmployeMasterAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = list[position]

        holder.binding.sNoTv.text=data.Sno.toString()
        holder.binding.empNameTv.text=data.DistrictName.toString()
        holder.binding.mobileTv.text=data.MandalName.toString()
        holder.binding.emailTv.text=data.VillageName.toString()

        holder.binding.editImg.setOnClickListener {
            callBackData.getTaskStatus(data.Sno.toString(),position.toString())
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}