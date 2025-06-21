package com.example.driverangkot.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.driverangkot.databinding.ItemListPassengerBinding
import com.example.driverangkot.domain.entity.Passenger
import com.ncorti.slidetoact.SlideToActView

class ListPassengerAdapter(
    private var passengers: List<Passenger>,
    private val onSlideComplete: (Passenger) -> Unit // [Baru] Callback untuk event geser
) : RecyclerView.Adapter<ListPassengerAdapter.PassengerViewHolder>() {

    inner class PassengerViewHolder(
        private val binding: ItemListPassengerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(passenger: Passenger) {
            binding.fullnamePassenger.text = passenger.name
            binding.numberHandphone.text = passenger.phone
            binding.tujuanText.text = passenger.placeName
            binding.slideIsDone.isLocked = passenger.isDone
            binding.slideIsDone.isEnabled = !passenger.isDone // [Berubah] Aktifkan jika belum done
            binding.slideIsDone.resetSlider() // [Baru] Reset slider saat bind
            binding.slideIsDone.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    onSlideComplete(passenger)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        val binding = ItemListPassengerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PassengerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        holder.bind(passengers[position])
    }

    override fun getItemCount(): Int = passengers.size

    fun updatePassengers(newPassengers: List<Passenger>) {
        passengers = newPassengers
        notifyDataSetChanged()
    }
}