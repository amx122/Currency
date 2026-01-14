package com.example.kursova_mobile.ui

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.kursova_mobile.R
import com.example.kursova_mobile.databinding.FragmentDetailsBinding
import com.example.kursova_mobile.utils.CurrencyUtils
import com.example.kursova_mobile.utils.PrefsManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private var currencyCode: String = "USD"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        currencyCode = arguments?.getString("currencyCode") ?: "USD"
        binding.tvTitleCode.text = "$currencyCode ${CurrencyUtils.getFlagEmoji(currencyCode)}"

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAlert.setOnClickListener {
            showAlertDialog()
        }

        setupChartStyle()
        viewModel.displayRates.observe(viewLifecycleOwner) { rates ->
            val rate = rates.find { it.currencyCode == currencyCode }
            if (rate != null) {
                val format = PrefsManager.getPrecisionFormat(requireContext())
                binding.tvCurrentRate.text = String.format(Locale.getDefault(), format, rate.rate)
            }
        }
        viewModel.loadHistory(currencyCode)

        viewModel.historyData.observe(viewLifecycleOwner) { entries ->
            if (entries.isNotEmpty()) {
                updateChart(entries)
                calculateStats(entries)
            }
        }
    }
    private fun showAlertDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Повідомити мене")
        builder.setMessage("Введіть курс, при досягненні якого надіслати сповіщення:")
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Наприклад: 42.50"
        builder.setView(input)

        builder.setPositiveButton("Зберегти") { _, _ ->
            val targetStr = input.text.toString()
            if (targetStr.isNotEmpty()) {
                val target = targetStr.toDoubleOrNull()
                if (target != null) {
                    viewModel.addAlert(currencyCode, target, true)
                    Toast.makeText(context, "Сповіщення створено!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Скасувати") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun calculateStats(entries: List<Entry>) {
        val min = entries.minByOrNull { it.y }?.y ?: 0f
        val max = entries.maxByOrNull { it.y }?.y ?: 0f

        val format = PrefsManager.getPrecisionFormat(requireContext())
        binding.tvMinRate.text = String.format(Locale.getDefault(), format, min)
        binding.tvMaxRate.text = String.format(Locale.getDefault(), format, max)
    }

    private fun setupChartStyle() {
        val chart = binding.chartCurrency

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textColor = if (PrefsManager.isDarkMode(requireContext())) Color.WHITE else Color.BLACK

        xAxis.valueFormatter = object : ValueFormatter() {
            private val sdf = SimpleDateFormat("dd.MM", Locale.getDefault())
            override fun getFormattedValue(value: Float): String {
                return sdf.format(Date(value.toLong()))
            }
        }

        val axisLeft = chart.axisLeft
        axisLeft.textColor = if (PrefsManager.isDarkMode(requireContext())) Color.WHITE else Color.BLACK
        axisLeft.setDrawGridLines(true)

        chart.axisRight.isEnabled = false
    }

    private fun updateChart(entries: List<Entry>) {
        val sortedEntries = entries.sortedBy { it.x }

        val dataSet = LineDataSet(sortedEntries, "Rate")

        dataSet.color = Color.parseColor("#007AFF")
        dataSet.valueTextColor = if (PrefsManager.isDarkMode(requireContext())) Color.WHITE else Color.BLACK
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(true)
        dataSet.setCircleColor(Color.parseColor("#007AFF"))
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#007AFF")
        dataSet.fillAlpha = 50

        val lineData = LineData(dataSet)
        binding.chartCurrency.data = lineData
        binding.chartCurrency.invalidate()
        binding.chartCurrency.animateX(1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}