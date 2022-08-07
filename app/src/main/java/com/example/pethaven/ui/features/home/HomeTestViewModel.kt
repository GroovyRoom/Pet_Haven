package com.example.pethaven.ui.features.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository
import com.example.pethaven.util.LiveDataExtensions.notifyObserver

class HomeTestViewModel(private val repository: ReptileRepository): ViewModel() {
    val isSearchOn = MutableLiveData<Boolean>(false)
    var reptileTask = getAllUserReptile()
    var favCount = MutableLiveData<Int>(0)

    val reptilesBoxes = MutableLiveData<ArrayList<ArrayList<Reptile>>>(ArrayList()).apply {
        value = arrayListOf(ArrayList<Reptile>(3), ArrayList<Reptile>(3), ArrayList<Reptile>(3))
    }

    val btnSwitches = MutableLiveData<ArrayList<Boolean>>(ArrayList()).apply {
        value = arrayListOf(false, false, false)
    }

    fun addReptile(reptile: Reptile)
    {
        if(favCount.value!! >= 9)
            return

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
        favCount.value = favCount.value!! + 1
        reptilesBoxes.notifyObserver()
    }

    fun toggleBtnSwitch(position: Int)
    {
        btnSwitches.value!![position] = !btnSwitches.value!![position]
        btnSwitches.notifyObserver()
    }

    fun getAllUserReptile() = repository.getAllUserReptile()
}