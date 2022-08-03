package com.example.pethaven.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.domain.Reptile

class ReptileBoxAdaptor(): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var reptileBoxes: ArrayList<ArrayList<Reptile>> = ArrayList()
    private var btnSwitches: ArrayList<Boolean> = ArrayList()
    private lateinit var listener: OnItemClickListener

    class ViewHolder(v: View, listenerIn: OnItemClickListener): RecyclerView.ViewHolder(v)
    {
        private val imgReptileBox: ImageView = v.findViewById(R.id.img_reptile_box)
        private val btnLeft: Button = v.findViewById(R.id.btn_left)
        private val btnMid: Button = v.findViewById(R.id.btn_mid)
        private val btnRight: Button = v.findViewById(R.id.btn_right)
        init {
            v.setOnClickListener{
                listenerIn.onItemClick(adapterPosition)
            }
        }

        fun bindImg(reptileBoxes: ArrayList<Reptile>, canShowBtn: Boolean, position: Int)
        {
            imgReptileBox.adjustViewBounds = true
            imgReptileBox.scaleType = ImageView.ScaleType.CENTER_INSIDE
            if(position == 0)
            {
                when (reptileBoxes.size) {
                    3 -> {
                        imgReptileBox.setImageResource(R.drawable.three_in_box)
                    }
                    2 -> {
                        imgReptileBox.setImageResource(R.drawable.two_in_box)
                    }
                    1 -> {
                        imgReptileBox.setImageResource(R.drawable.one_in_box)
                    }
                    0 -> {
                        imgReptileBox.setImageResource(R.drawable.empty_box)
                    }
                }
            }
            else
            {
                when (reptileBoxes.size) {
                    3 -> {
                        imgReptileBox.setImageResource(R.drawable.three_in_box_small)
                    }
                    2 -> {
                        imgReptileBox.setImageResource(R.drawable.two_in_box_small)
                    }
                    1 -> {
                        imgReptileBox.setImageResource(R.drawable.one_in_box_small)
                    }
                    0 -> {
                        imgReptileBox.setImageResource(R.drawable.empty_box_small)
                    }
                }
            }
            if(canShowBtn)
            {
                when (reptileBoxes.size) {
                    3 -> {
                        btnLeft.text = reptileBoxes[0].name
                        btnMid.text = reptileBoxes[1].name
                        btnRight.text = reptileBoxes[2].name
                        btnLeft.visibility = View.VISIBLE
                        btnMid.visibility = View.VISIBLE
                        btnRight.visibility = View.VISIBLE
                        btnLeft.isEnabled = true
                        btnMid.isEnabled = true
                        btnRight.isEnabled = true
                    }
                    2 -> {
                        btnLeft.text = reptileBoxes[0].name
                        btnMid.text = reptileBoxes[1].name
                        btnLeft.visibility = View.VISIBLE
                        btnMid.visibility = View.VISIBLE
                        btnLeft.isEnabled = true
                        btnMid.isEnabled = true
                    }
                    1 -> {
                        btnLeft.text = reptileBoxes[0].name
                        btnLeft.visibility = View.VISIBLE
                        btnLeft.isEnabled = true
                    }
                    0 -> {
                    }
                }
            }
            if(!canShowBtn)
            {
                btnLeft.text = ""
                btnMid.text = ""
                btnRight.text = ""
                btnLeft.visibility = View.INVISIBLE
                btnMid.visibility = View.INVISIBLE
                btnRight.visibility = View.INVISIBLE
                btnLeft.isEnabled = false
                btnMid.isEnabled = false
                btnRight.isEnabled = false
            }
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_reptile_collection, parent, false), listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder)
        {
            is ViewHolder ->{
                    holder.bindImg(reptileBoxes[position], btnSwitches[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return reptileBoxes.size
    }

    fun updateList(reptileBoxes: ArrayList<ArrayList<Reptile>>, btnSwitches: ArrayList<Boolean>)
    {
        this.reptileBoxes = reptileBoxes
        this.btnSwitches = btnSwitches
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
        {

        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener)
    {
        this.listener = listener
    }
}