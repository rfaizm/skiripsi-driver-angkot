package com.example.driverangkot.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.driverangkot.databinding.ItemListPassengerBinding
import com.example.driverangkot.domain.entity.Passenger
import com.ncorti.slidetoact.SlideToActView

class ListPassengerAdapter(
    var passengers: List<Passenger>,
    private val onSlideComplete: (Passenger) -> Unit,
    private val isWaitingFragment: Boolean, // 👈 tambahan parameter
    private val onCancelClicked: (Passenger) -> Unit // 👈 callback baru
) : RecyclerView.Adapter<ListPassengerAdapter.PassengerViewHolder>() {

    inner class PassengerViewHolder(
        private val binding: ItemListPassengerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(passenger: Passenger) {
            binding.fullnamePassenger.text = passenger.name
            binding.numberHandphone.text = passenger.phone
            binding.tujuanText.text = passenger.placeName
            binding.methodPayment.text = passenger.methodPayment.replaceFirstChar { it.uppercase() }

            // 👇 tampilkan atau sembunyikan tombol cancel tergantung fragment
            binding.buttonCancel.visibility = if (isWaitingFragment) View.VISIBLE else View.GONE

            // 👇 klik tombol cancel
            binding.buttonCancel.setOnClickListener {
                onCancelClicked(passenger)
            }

            // Setup slider
            binding.slideIsDone.isLocked = passenger.isDone
            binding.slideIsDone.isEnabled = !passenger.isDone
            binding.slideIsDone.resetSlider()
            binding.slideIsDone.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
                override fun onSlideComplete(view: SlideToActView) {
                    onSlideComplete(passenger)
                }
            }
        }

        fun resetSlider() {
            binding.slideIsDone.resetSlider()
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

    fun resetSliderAtPosition(position: Int) {
        if (position in 0 until itemCount) {
            notifyItemChanged(position)
        }
    }
}
