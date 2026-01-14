package com.example.kursova_mobile.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kursova_mobile.R
import com.example.kursova_mobile.data.ExchangeRateEntity
import com.example.kursova_mobile.databinding.ItemCurrencyBinding
import com.example.kursova_mobile.utils.CurrencyUtils
import com.example.kursova_mobile.utils.PrefsManager
import java.util.Locale

class RatesAdapter(
    private var rates: List<ExchangeRateEntity>,
    private val onFavoriteClick: (ExchangeRateEntity) -> Unit,
    private val onItemClick: (ExchangeRateEntity) -> Unit
) : RecyclerView.Adapter<RatesAdapter.RateViewHolder>() {

    class RateViewHolder(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        val binding = ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        val rate = rates[position]
        val context = holder.itemView.context

        holder.binding.tvCurrencyCode.text = rate.currencyCode
        holder.binding.tvFlag.text = CurrencyUtils.getFlagEmoji(rate.currencyCode)
        holder.binding.tvCurrencyName.text = context.getString(R.string.nbu_label)
        val format = PrefsManager.getPrecisionFormat(context)
        holder.binding.tvRate.text = String.format(Locale.getDefault(), format, rate.rate)

        val iconRes = if (rate.isFavorite) android.R.drawable.star_on else android.R.drawable.star_off
        holder.binding.btnFavorite.setImageResource(iconRes)

        holder.binding.btnFavorite.setOnClickListener { onFavoriteClick(rate) }
        holder.itemView.setOnClickListener { onItemClick(rate) }
    }

    override fun getItemCount() = rates.size

    fun updateData(newRates: List<ExchangeRateEntity>) {
        rates = newRates
        notifyDataSetChanged()
    }
}