package com.example.pethaven.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.domain.Reptile
import com.example.pethaven.ui.features.home.AddEditReptileActivity
import com.example.pethaven.ui.features.home.HomeTestViewModel
import com.example.pethaven.ui.features.home.ReptileProfileActivity

class ReptileBoxAdaptor(private val activity: Context,private val viewModel: HomeTestViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var reptileBoxes: ArrayList<ArrayList<Reptile>> = ArrayList()
    private var btnSwitches: ArrayList<Boolean> = ArrayList()
    private lateinit var listener: OnItemClickListener

    class ViewHolder(private val activity: Context, v: View, listenerIn: OnItemClickListener, private val viewModel: HomeTestViewModel): RecyclerView.ViewHolder(v)
    {
        private val imgReptileBox: ImageView = v.findViewById(R.id.img_reptile_box)
        private val controlLeft: LinearLayout = v.findViewById(R.id.controlLeft)
        private val controlMid: LinearLayout = v.findViewById(R.id.controlMid)
        private val controlRight: LinearLayout = v.findViewById(R.id.controlRight)
        private val btnLeft: Button = v.findViewById(R.id.btn_left)
        private val btnMid: Button = v.findViewById(R.id.btn_mid)
        private val btnRight: Button = v.findViewById(R.id.btn_right)
        private val editLeft: ImageButton = v.findViewById(R.id.btnEditLeft)
        private val editMid: ImageButton = v.findViewById(R.id.btnEditMid)
        private val editRight: ImageButton = v.findViewById(R.id.btnEditRight)
        private val favLeft: ImageButton = v.findViewById(R.id.btnFavLeft)
        private val favMid: ImageButton = v.findViewById(R.id.btnFavMid)
        private val favRight: ImageButton = v.findViewById(R.id.btnFavRight)
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
                        if (reptileBoxes[0].name.length < 5) {
                            btnLeft.text = reptileBoxes[0].name
                        } else {
                            btnLeft.text = reptileBoxes[0].name.subSequence(0,5)
                        }
                        if (reptileBoxes[1].name.length < 5) {
                            btnMid.text = reptileBoxes[1].name
                        } else {
                            btnMid.text = reptileBoxes[1].name.subSequence(0,5)
                        }
                        if (reptileBoxes[2].name.length < 5) {
                            btnRight.text = reptileBoxes[2].name
                        } else {
                            btnRight.text = reptileBoxes[2].name.subSequence(0,5)
                        }
                        controlLeft.visibility = View.VISIBLE
                        controlMid.visibility = View.VISIBLE
                        controlRight.visibility = View.VISIBLE
                        btnLeft.isEnabled = true
                        btnMid.isEnabled = true
                        btnRight.isEnabled = true
                    }
                    2 -> {
                        if (reptileBoxes[0].name.length < 5) {
                            btnLeft.text = reptileBoxes[0].name
                        } else {
                            btnLeft.text = reptileBoxes[0].name.subSequence(0,5)
                        }
                        if (reptileBoxes[1].name.length < 5) {
                            btnMid.text = reptileBoxes[1].name
                        } else {
                            btnMid.text = reptileBoxes[1].name.subSequence(0,5)
                        }
                        controlLeft.visibility = View.VISIBLE
                        controlMid.visibility = View.VISIBLE
                        btnLeft.isEnabled = true
                        btnMid.isEnabled = true
                    }
                    1 -> {
                        if (reptileBoxes[0].name.length < 5) {
                            btnLeft.text = reptileBoxes[0].name
                        } else {
                            btnLeft.text = reptileBoxes[0].name.subSequence(0,5)
                        }
                        controlLeft.visibility = View.VISIBLE
                        btnLeft.isEnabled = true
                    }
                    0 -> {
                    }
                }
                setAllBtnListener(reptileBoxes, position)
            }
            if(!canShowBtn)
            {
                btnLeft.text = ""
                btnMid.text = ""
                btnRight.text = ""
                controlLeft.visibility = View.GONE
                controlMid.visibility = View.GONE
                controlRight.visibility = View.GONE
                btnLeft.isEnabled = false
                btnMid.isEnabled = false
                btnRight.isEnabled = false
            }
        }

        private fun setAllBtnListener(reptileBoxes: ArrayList<Reptile>, position: Int)
        {
            when (reptileBoxes.size) {
                3 -> {
                    setLeftListener(reptileBoxes[0], position)
                    setMidListener(reptileBoxes[1], position)
                    setRightListener(reptileBoxes[2], position)
                }
                2 -> {
                    setLeftListener(reptileBoxes[0], position)
                    setMidListener(reptileBoxes[1], position)
                }
                1 -> {
                    setLeftListener(reptileBoxes[0], position)
                }
            }
        }

        private fun setLeftListener(reptile: Reptile, position: Int)
        {
            btnLeft.setOnClickListener{
                startDetailIntent(reptile.key!!)
            }
            editLeft.setOnClickListener {
                startEditIntent(reptile.key!!)
            }
            favLeft.setOnClickListener {
                viewModel.unFav(reptile)
                viewModel.toggleBtnSwitch(position)
            }
        }

        private fun setMidListener(reptile: Reptile, position: Int)
        {
            btnMid.setOnClickListener{
                startDetailIntent(reptile.key!!)
            }
            editMid.setOnClickListener {
                startEditIntent(reptile.key!!)
            }
            favMid.setOnClickListener {
                viewModel.unFav(reptile)
                viewModel.toggleBtnSwitch(position)
            }
        }

        private fun setRightListener(reptile: Reptile, position: Int)
        {
            btnRight.setOnClickListener{
                startDetailIntent(reptile.key!!)
            }
            editRight.setOnClickListener {
                startEditIntent(reptile.key!!)
            }
            favRight.setOnClickListener {
                viewModel.unFav(reptile)
                viewModel.toggleBtnSwitch(position)
            }
        }

        private fun startDetailIntent(reptileKey: String)
        {
            val intent = ReptileProfileActivity.makeIntent(activity, reptileKey)
            activity.startActivity(intent)
        }

        private fun startEditIntent(reptileKey: String)
        {
            val intent = AddEditReptileActivity.makeIntent(activity, true, reptileKey)
            activity.startActivity(intent)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(activity, LayoutInflater.from(parent.context).inflate(R.layout.list_reptile_collection, parent, false), listener, viewModel)
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
        notifyDataSetChanged()
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