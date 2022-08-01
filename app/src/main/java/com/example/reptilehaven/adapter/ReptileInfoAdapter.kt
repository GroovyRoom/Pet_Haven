package com.example.reptilehaven.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reptilehaven.R
import com.example.reptilehaven.domain.Reptile

class ReptileInfoAdapter(private var context: Context,
                         private var reptileList: ArrayList<Reptile>,
                         private var clickListener: OnReptileItemCLickedListener)
    : RecyclerView.Adapter<ReptileInfoAdapter.ViewHolder>(), Filterable{

    var reptileListAll = ArrayList<Reptile>(reptileList)
    private var reptileFilter = object : Filter(){
        // Run in Background Thread
        override fun performFiltering(constraint: CharSequence): FilterResults {
            var filteredList = ArrayList<Reptile>()
            if (constraint.toString().isEmpty()) {
                println("debug: adding all reptileListAll with size = ${reptileListAll.size}")
                filteredList.addAll(reptileListAll)
            } else {
                for (reptile in reptileListAll) {
                    if (reptile.name.lowercase().contains(constraint.toString().lowercase())) {
                        filteredList.add(reptile)
                    }
                }
            }
            val result = FilterResults().apply {
                values = filteredList
            }
            return result
        }

        // Run in UI Thread
        override fun publishResults(constraint: CharSequence, result: FilterResults) {
            reptileList.clear()
            reptileList.addAll(result.values as ArrayList<Reptile>)
            notifyDataSetChanged()
        }

    }
    interface OnReptileItemCLickedListener {
        fun onReptileClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_reptile_info, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reptile = reptileList[position]

        holder.reptileNameText.text = reptile.name
        holder.reptileSpeciesText.text = reptile.species
        holder.reptileAgeText.text = reptile.age.toString()
        holder.reptileDescText.text = reptile.description

        reptile.imgUri?.let {
            Glide.with(context).
                load(it).
                fitCenter().
                into(holder.reptileImageView)
        }
    }

    fun getReptile(position: Int): Reptile {
        return reptileList[position]
    }

    override fun getItemCount(): Int {
        return reptileList.size
    }

    fun setReptileList(list: ArrayList<Reptile>) {
        reptileList.clear()
        reptileList.addAll(list)

        reptileListAll.clear()
        reptileListAll.addAll(list)
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemView: View, private val listener: OnReptileItemCLickedListener)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var reptileImageView: ImageView = itemView.findViewById(R.id.reptileImageAdapter)
        var reptileNameText: TextView = itemView.findViewById(R.id.reptileNameAdapter)
        var reptileSpeciesText: TextView = itemView.findViewById(R.id.reptileSpeciesAdapter)
        var reptileAgeText: TextView = itemView.findViewById(R.id.reptileAgeAdapter)
        var reptileDescText: TextView = itemView.findViewById(R.id.reptileDescriptionAdapter)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener.onReptileClicked(adapterPosition)
        }
    }

    override fun getFilter(): Filter {
        return reptileFilter
    }

}