package com.example.driverangkot.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.example.driverangkot.databinding.DropdownItemBinding
import com.example.driverangkot.domain.entity.Trayek

class RouteArrayAdapter(
    context: Context,
    resource: Int,
    private val trayeks: List<Trayek>
) : ArrayAdapter<Trayek>(context, resource, trayeks) {

    private val originalTrayeks: List<Trayek> = trayeks
    private val filteredTrayeks: MutableList<Trayek> = mutableListOf()

    init {
        filteredTrayeks.addAll(originalTrayeks)
    }

    override fun getCount(): Int = filteredTrayeks.size

    override fun getItem(position: Int): Trayek? = filteredTrayeks[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: DropdownItemBinding
        val view: View

        if (convertView == null) {
            binding = DropdownItemBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as DropdownItemBinding
            view = convertView
        }
        binding.dropdownText.text = filteredTrayeks[position].name
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val filteredList = mutableListOf<Trayek>()

                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(originalTrayeks)
                } else {
                    val filterPattern = constraint.toString().trim().lowercase()
                    filteredList.addAll(
                        originalTrayeks.filter {
                            it.name.lowercase().contains(filterPattern)
                        }
                    )
                }

                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredTrayeks.clear()
                if (results?.values != null) {
                    filteredTrayeks.addAll(results.values as List<Trayek>)
                }
                notifyDataSetChanged()
            }
        }
    }
}