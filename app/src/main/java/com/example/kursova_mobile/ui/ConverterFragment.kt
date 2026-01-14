package com.example.kursova_mobile.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kursova_mobile.R
import com.example.kursova_mobile.databinding.FragmentConverterBinding
import com.example.kursova_mobile.utils.PrefsManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*

class ConverterFragment : Fragment(R.layout.fragment_converter) {
    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentConverterBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.allRates.observe(viewLifecycleOwner) { rates ->
            if (rates.isNullOrEmpty()) {
                binding.tvResult.text = "..."
                return@observe
            }

            val codes = rates.map { it.currencyCode }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, codes)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerFrom.adapter = adapter
            binding.spinnerTo.adapter = adapter

            binding.btnSwap.setOnClickListener {
                val fromPosition = binding.spinnerFrom.selectedItemPosition
                val toPosition = binding.spinnerTo.selectedItemPosition
                binding.spinnerFrom.setSelection(toPosition)
                binding.spinnerTo.setSelection(fromPosition)
                binding.btnSwap.animate().rotationBy(180f).setDuration(300).start()
            }

            binding.btnConvert.setOnClickListener {
                val amountStr = binding.etAmount.text.toString()
                if (amountStr.isEmpty()) {
                    Toast.makeText(context, getString(R.string.search_hint), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val fromCode = binding.spinnerFrom.selectedItem?.toString() ?: return@setOnClickListener
                val toCode = binding.spinnerTo.selectedItem?.toString() ?: return@setOnClickListener

                val fromRate = rates.find { it.currencyCode == fromCode }?.rate ?: 1.0
                val toRate = rates.find { it.currencyCode == toCode }?.rate ?: 1.0

                val amount = amountStr.toDouble()
                val result = (amount * fromRate) / toRate
                val format = PrefsManager.getPrecisionFormat(requireContext())
                binding.tvResult.text = String.format(Locale.getDefault(), format, result)

                viewModel.saveConversion(fromCode, toCode, amount, result)
            }

            binding.btnHistory.setOnClickListener {
                showHistorySheet()
            }
        }
    }

    private fun showHistorySheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.layout_history_sheet, null)

        val container = view.findViewById<android.widget.LinearLayout>(R.id.historyContainer)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyHistory)
        view.findViewById<TextView>(R.id.tvSheetTitle)?.text = getString(R.string.last_operations)
        tvEmpty.text = getString(R.string.history_empty)

        viewModel.conversionHistory.observe(viewLifecycleOwner) { list ->
            container.removeAllViews()

            if (list.isNullOrEmpty()) {
                tvEmpty.visibility = View.VISIBLE
            } else {
                tvEmpty.visibility = View.GONE
                val sdf = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())

                list.forEach { item ->
                    val itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_history, container, false)

                    val format = PrefsManager.getPrecisionFormat(requireContext())
                    val amFrom = String.format(Locale.getDefault(), format, item.amountFrom)
                    val amTo = String.format(Locale.getDefault(), format, item.amountTo)

                    val text = "$amFrom ${item.fromCurrency} ➔ $amTo ${item.toCurrency}"

                    itemView.findViewById<TextView>(R.id.tvHistoryText).text = text
                    itemView.findViewById<TextView>(R.id.tvHistoryDate).text = sdf.format(Date(item.timestamp))

                    container.addView(itemView)
                }
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}