package com.example.pethaven.ui.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository

/**
 *  ViewModel for Home Fragment
 */
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
/*
package com.example.pethaven.ui.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository
import com.example.pethaven.util.LiveDataExtensions.notifyObserver

class HomeTestViewModel(private val repository: ReptileRepository): ViewModel() {
    val isSearchOn = MutableLiveData(false)
    var reptileTask = getAllUserReptile()
    private val reptileList = ArrayList<Reptile>();

    val reptilesBoxes = MutableLiveData<ArrayList<ArrayList<Reptile>>>(ArrayList()).apply {
        value = arrayListOf(ArrayList<Reptile>(3), ArrayList<Reptile>(3), ArrayList<Reptile>(3))
    }

    val btnSwitches = MutableLiveData<ArrayList<Boolean>>(ArrayList()).apply {
        value = arrayListOf(false, false, false)
    }

    fun addReptile(reptile: Reptile)
    {
        if(reptilesBoxes.value!!.isEmpty())
        {
            reptilesBoxes.value!!.add(ArrayList<Reptile>(3))
            reptilesBoxes.value!!.add(ArrayList<Reptile>(3))
            reptilesBoxes.value!!.add(ArrayList<Reptile>(3))
        }
        if(btnSwitches.value!!.isEmpty())
        {
            btnSwitches.value!!.add(false)
            btnSwitches.value!!.add(false)
            btnSwitches.value!!.add(false)
        }
        for(arr:ArrayList<Reptile> in reptilesBoxes.value!!)
        {
            if(arr.size < 3)
            {
                arr.add(reptile)
                break
            }
        }
        reptilesBoxes.notifyObserver()
    }

    fun toggleBtnSwitch(position: Int)
    {
        btnSwitches.value!![position] = !btnSwitches.value!![position]
        btnSwitches.notifyObserver()
    }

    fun getAllUserReptile() = repository.getAllUserReptile()
}*/
