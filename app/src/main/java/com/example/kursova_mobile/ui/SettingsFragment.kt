package com.example.kursova_mobile.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kursova_mobile.R
import com.example.kursova_mobile.databinding.FragmentSettingsBinding
import com.example.kursova_mobile.utils.PrefsManager

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupBaseCurrency()
        setupLanguage()
        setupPrecision()
        setupThemeSwitch()
    }

    private fun setupBaseCurrency() {
        val currencies = listOf("UAH", "USD", "EUR", "GBP", "PLN")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBaseCurrency.adapter = adapter

        val current = PrefsManager.getBaseCurrency(requireContext())
        binding.spinnerBaseCurrency.setSelection(currencies.indexOf(current))

        binding.spinnerBaseCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = currencies[position]
                if (selected != current) {
                    PrefsManager.setBaseCurrency(requireContext(), selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupLanguage() {
        val languages = listOf("Українська", "English")
        val codes = listOf("uk", "en")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        val currentLang = PrefsManager.getLanguage(requireContext())
        binding.spinnerLanguage.setSelection(codes.indexOf(currentLang))

        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCode = codes[position]
                if (selectedCode != currentLang) {
                    PrefsManager.setLanguage(requireContext(), selectedCode)
                    requireActivity().recreate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    private fun setupPrecision() {
        val options = listOf(
            getString(R.string.precision_2),
            getString(R.string.precision_3),
            getString(R.string.precision_4)
        )
        val values = listOf(2, 3, 4)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrecision.adapter = adapter

        val current = PrefsManager.getPrecision(requireContext())
        binding.spinnerPrecision.setSelection(values.indexOf(current))

        binding.spinnerPrecision.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedValue = values[position]
                if (selectedValue != current) {
                    PrefsManager.setPrecision(requireContext(), selectedValue)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupThemeSwitch() {
        binding.switchTheme.isChecked = PrefsManager.isDarkMode(requireContext())

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            PrefsManager.setDarkMode(requireContext(), isChecked)
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}