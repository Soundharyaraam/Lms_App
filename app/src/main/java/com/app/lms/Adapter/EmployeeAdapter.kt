package com.app.lms.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.lms.R
import com.app.lms.dataResponse.EmployeeData
import com.app.lms.databinding.EmployeMasterAdapterBinding
import com.finowizx.CallBackInterface.CallBackData

class EmployeeAdapter  (
    private val context: Context,
    private val list: List<EmployeeData>,
    private val callBackData: CallBackData
) : RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

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
        holder.binding.empNameTv.text=data.EmployeeName.toString()

        if(data.MobileNo.toString()!=null) {
            if (data.MobileNo.toString().isNotEmpty()) {
                holder.binding.mobileTv.text = data.MobileNo.toString()
            } else {
                holder.binding.mobileTv.text = context.getString(R.string.mob_no).toString()
            }
        }else{
            holder.binding.mobileTv.text = context.getString(R.string.mob_no).toString()
        }

        if(data.EmailID.toString()!=null) {
            if (data.EmailID.toString().isNotEmpty()) {
                holder.binding.emailTv.text = data.EmailID.toString()
            } else {
                holder.binding.emailTv.text = context.getString(R.string.email_id).toString()
            }
        }else{
            holder.binding.emailTv.text = context.getString(R.string.email_id).toString()
        }

        holder.binding.editImg.setOnClickListener {
            callBackData.getTaskStatus(data.Sno.toString(),position.toString())
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}