package com.example.pethaven.ui.features.fav

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pethaven.domain.Reptile
import com.example.pethaven.domain.ReptileRepository
import com.example.pethaven.util.LiveDataExtensions.notifyObserver

class FavTestViewModel(private val repository: ReptileRepository): ViewModel() {
    val isSearchOn = MutableLiveData<Boolean>(false)
    var reptileTask = getAllUserReptile()

    val reptilesBoxes = MutableLiveData<ArrayList<ArrayList<Reptile>>>()

    val btnSwitches = MutableLiveData<ArrayList<Boolean>>()

    fun init()
    {
        if(reptilesBoxes.value == null || reptilesBoxes.value!!.isEmpty())
        {
            val arr = arrayListOf<ArrayList<Reptile>>(ArrayList<Reptile>(3), ArrayList<Reptile>(3), ArrayList<Reptile>(3))
            reptilesBoxes.value = arr
        }
        if(btnSwitches.value == null || btnSwitches.value!!.isEmpty())
        {
            val arr = arrayListOf(false, false, false)
            btnSwitches.value = arr
        }
    }

    fun addReptileToBox(reptile: Reptile): Boolean
    {
        if(getBoxSize() >= 9)
            return false

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
        return true
    }

    private fun removeReptileFromBox(reptile: Reptile)
    {
        var arr: Reptile
        for(i in 0..2)
        {
            for(j in 0 until reptilesBoxes.value!![i].size)
            {
                if(reptilesBoxes.value!![i][j].key == reptile.key)
                {
                    when (reptilesBoxes.value!![i].size) {
                        1 -> {
                            reptilesBoxes.value!![i].removeLast()
                        }
                        2 -> {
                            if(j == 0) {
                                reptilesBoxes.value!![i][0] = reptilesBoxes.value!![i][1]
                            }
                            reptilesBoxes.value!![i].removeLast()
                        }
                        3 -> {
                            if(j == 0) {
                                reptilesBoxes.value!![i][0] = reptilesBoxes.value!![i][2]
                            } else if(j == 1) {
                                reptilesBoxes.value!![i][1] = reptilesBoxes.value!![i][2]
                            }
                            reptilesBoxes.value!![i].removeLast()
                        }
                    }
                    break
                }
            }
        }

        reptilesBoxes.notifyObserver()
    }

    fun getBoxSize(): Int
    {
        return reptilesBoxes.value!![0].size + reptilesBoxes.value!![1].size + reptilesBoxes.value!![2].size;
    }

    fun toggleBtnSwitch(position: Int)
    {
        btnSwitches.value!![position] = !btnSwitches.value!![position]
        btnSwitches.notifyObserver()
    }

    private fun getAllUserReptile() = repository.getAllUserReptile()

    private fun updateReptileInDatabase(key:String, reptile: Reptile) = repository.updateReptile(key, reptile)

    fun fav(reptile: Reptile)
    {
        reptile.isFav = true
        updateReptileInDatabase(reptile.key!!, reptile)
        addReptileToBox(reptile)

/*        updateReptileInDatabase(reptile.key!!, reptile).addOnSuccessListener {
            reptile.isFav = true
            addReptileToBox(reptile)
        }*/

    }

    fun unFav(reptile: Reptile)
    {
        reptile.isFav = false
        updateReptileInDatabase(reptile.key!!, reptile)
        removeReptileFromBox(reptile)

/*        updateReptileInDatabase(reptile.key!!, reptile).addOnSuccessListener {
            reptile.isFav = false
            removeReptileFromBox(reptile)
        }*/
    }
}