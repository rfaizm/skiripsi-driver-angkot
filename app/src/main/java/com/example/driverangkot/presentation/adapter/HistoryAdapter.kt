package com.example.driverangkot.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.driverangkot.data.api.dto.DataItemHistory
import com.example.driverangkot.databinding.ItemHistoryBinding

class HistoryAdapter : ListAdapter<DataItemHistory, HistoryAdapter.HistoryViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<DataItemHistory>() {
        override fun areItemsTheSame(oldItem: DataItemHistory, newItem: DataItemHistory): Boolean {
            return oldItem.orderNumber == newItem.orderNumber
        }

        override fun areContentsTheSame(oldItem: DataItemHistory, newItem: DataItemHistory): Boolean {
            return oldItem == newItem
        }
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItemHistory?) {
            if (item != null) {
                binding.descriptionHistory.text = item.orderNumber
                binding.date.text = item.date
                binding.price.text = "Rp. ${item.priceTotal?.toString() ?: "0"}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}