package com.example.kursova_mobile.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kursova_mobile.R

class LogsFragment : Fragment(R.layout.fragment_logs) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvLogs)

        val adapter = LogsAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.errorLogs.observe(viewLifecycleOwner) { logs ->
            adapter.updateData(logs)
        }
    }
}