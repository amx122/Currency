package com.example.kursova_mobile.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kursova_mobile.R
import com.example.kursova_mobile.data.ErrorLogEntity
import java.text.SimpleDateFormat
import java.util.*

class LogsAdapter(private var logs: List<ErrorLogEntity>) :
    RecyclerView.Adapter<LogsAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvLogDate)
        val tvMessage: TextView = view.findViewById(R.id.tvLogMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

        holder.tvDate.text = sdf.format(Date(log.timestamp))
        holder.tvMessage.text = log.errorMessage
    }

    override fun getItemCount() = logs.size

    fun updateData(newLogs: List<ErrorLogEntity>) {
        logs = newLogs
        notifyDataSetChanged()
    }
}