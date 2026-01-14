package com.example.kursova_mobile.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kursova_mobile.R
import com.example.kursova_mobile.databinding.FragmentHistoryBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment(R.layout.fragment_history) {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHistoryBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.allRates.observe(viewLifecycleOwner) { rates ->
            if (rates.isEmpty()) return@observe

            val codes = rates.map { it.currencyCode }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, codes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerHistoryCurrency.adapter = adapter
            binding.spinnerHistoryCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedCode = codes[position]
                    viewModel.loadHistory(selectedCode)
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        viewModel.historyData.observe(viewLifecycleOwner) { entries ->
            if (entries.isNotEmpty()) {
                updateChart(entries)
            }
        }
    }

    private fun updateChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Динаміка курсу")

        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        dataSet.cubicIntensity = 0.2f
        dataSet.setDrawFilled(true)
        dataSet.setDrawCircles(true)
        dataSet.circleRadius = 4f
        dataSet.setCircleColor(Color.parseColor("#2196F3"))
        dataSet.lineWidth = 2f
        dataSet.color = Color.parseColor("#2196F3")
        dataSet.fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_blue)
        dataSet.setDrawValues(true)
        dataSet.valueTextSize = 10f

        val lineData = LineData(dataSet)
        binding.chart.data = lineData
        binding.chart.description.isEnabled = false
        binding.chart.legend.isEnabled = false
        binding.chart.axisRight.isEnabled = false
        binding.chart.axisLeft.setDrawGridLines(false)
        binding.chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.chart.xAxis.setDrawGridLines(false)
        val days = getLast7DaysLabels()
        binding.chart.xAxis.valueFormatter = IndexAxisValueFormatter(days)

        binding.chart.animateY(800)
        binding.chart.invalidate()
    }

    private fun getLast7DaysLabels(): List<String> {
        val labels = mutableListOf<String>()
        val sdf = SimpleDateFormat("dd.MM", Locale.getDefault())
        val cal = Calendar.getInstance()
        for (i in 6 downTo 0) {
            val temp = Calendar.getInstance()
            temp.add(Calendar.DAY_OF_YEAR, -i)
            labels.add(sdf.format(temp.time))
        }
        return labels
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}