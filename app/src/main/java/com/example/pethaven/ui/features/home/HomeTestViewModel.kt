package com.example.pethaven.ui.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository

class HomeTestViewModel(private val repository: ReptileRepository): ViewModel() {
    var isFabChecked = MutableLiveData(false)
    var reptileTask = getAllUserReptile()
    private val reptileList = ArrayList<Reptile>();

    val reptilesBoxes = MutableLiveData<ArrayList<ArrayList<Reptile>>>(ArrayList()).apply {
//        val arr:ArrayList<ArrayList<Reptile>> = ArrayList()
//        arr.add(ArrayList<Reptile>(3))
        value = ArrayList()
    }

    val btnSwitches = MutableLiveData<ArrayList<Boolean>>(ArrayList()).apply {
//        val arr:ArrayList<Boolean> = ArrayList()
//        arr.add(false)
        value = ArrayList()
    }

    fun addReptile(reptile: Reptile)
    {
        if(reptilesBoxes.value!!.isEmpty() || reptilesBoxes.value!!.last().size == 3)
        {
            val arr = ArrayList<Reptile>(3)
            arr.add(reptile)
            reptilesBoxes.value!!.add(arr)
            btnSwitches.value!!.add(false)
        }
        else
        {
            reptilesBoxes.value!!.last().add(reptile)
            if(reptilesBoxes.value!!.last().size == 3)
            {
                reptilesBoxes.value!!.add(ArrayList(3))
                btnSwitches.value!!.add(false)
            }
        }
    }

    fun toggleBtnSwitch(position: Int)
    {
        btnSwitches.value!![position] = !btnSwitches.value!![position]
    }

    fun parseData()
    {
        for(i: Reptile in reptileList)
        {
            addReptile(i)
            println(i.name)
        }
    }

    fun getAllUserReptile() = repository.getAllUserReptile()
}