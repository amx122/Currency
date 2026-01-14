package com.example.kursova_mobile.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kursova_mobile.R
import com.example.kursova_mobile.data.ExchangeRateEntity
import com.example.kursova_mobile.databinding.FragmentRatesBinding
import com.example.kursova_mobile.utils.DateUtils

class RatesFragment : Fragment(R.layout.fragment_rates) {

    private var _binding: FragmentRatesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: RatesAdapter

    private var fullList: List<ExchangeRateEntity> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRatesBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        adapter = RatesAdapter(
            emptyList(),
            onFavoriteClick = { rate -> viewModel.toggleFavorite(rate) },
            onItemClick = { rate ->
                val bundle = Bundle().apply { putString("currencyCode", rate.currencyCode) }
                findNavController().navigate(R.id.action_rates_to_details, bundle)
            }
        )

        binding.rvRates.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRates.adapter = adapter
        viewModel.displayRates.observe(viewLifecycleOwner) { rates ->
            fullList = rates
            filterList(binding.searchView.query.toString())

            if (rates.isNotEmpty()) {
                val lastUpdate = rates[0].timestamp
                binding.tvDate.text = com.example.kursova_mobile.utils.DateUtils.formatLastUpdate(lastUpdate)
            } else {
                binding.tvDate.text = "Дані відсутні"
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
        if (fullList.isEmpty()) {
            viewModel.refreshData()
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.forceRefreshDisplay()
    }

    private fun filterList(query: String?) {
        if (query.isNullOrEmpty()) {
            adapter.updateData(fullList)
        } else {
            val filtered = fullList.filter {
                it.currencyCode.contains(query, ignoreCase = true)
            }
            adapter.updateData(filtered)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}