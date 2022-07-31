package com.example.pethaven.ui.features.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.adapter.ReptileInfoAdapter
import com.example.pethaven.domain.Reptile
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), ReptileInfoAdapter.OnReptileItemCLickedListener {
    private lateinit var addFab: FloatingActionButton
    private lateinit var optionsFab: FloatingActionButton

    private lateinit var addFabTextView: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var reptileAdapter: ReptileInfoAdapter

    private lateinit var testViewModel: HomeTestViewModel

    private lateinit var openFabAnimation: Animation
    private lateinit var closeFabAnimation: Animation
    private lateinit var traverseFromBottomFabAnimation: Animation
    private lateinit var traverseBottomFabAnimation: Animation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
            George/Dense: Change the R.layout.fragment_home_test. I only used this to test the
            reptileInfo activity
         */
        val view =  inflater.inflate(R.layout.fragment_home_test, container, false)
        openFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_open)
        closeFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_close)
        traverseBottomFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_traverse_bottom)
        traverseFromBottomFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_traverse_from_bottom)

        setUpTestViewModel()
        setUpRecyclerView(view)
        setUpFloatingActionButton(view)

        setFabVisibility(testViewModel.isFabChecked.value!!)


        receiveAllReptiles()

        return view
    }

    // --------------------- Functions for Testing ---------------- //

    private fun setUpRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.reptileInfoRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        reptileAdapter = ReptileInfoAdapter(requireActivity(), ArrayList(), this)
        recyclerView.adapter = reptileAdapter

    }

    private fun setUpTestViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(requireActivity())
        testViewModel = ViewModelProvider(this, factory).get(HomeTestViewModel::class.java)
    }

    private fun receiveAllReptiles() {
        val reptileListReference = testViewModel.getAllUserReptile()
        reptileListReference.addValueEventListener (object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //if (snapshot.exists()) {}
                val list = ArrayList<Reptile>()
                for (postSnapShot in snapshot.children) {
                    val reptile = postSnapShot.getValue(Reptile::class.java)
                    reptile?.let {
                        it.key = postSnapShot.key
                        list.add(it)
                    }
                }
                reptileAdapter.setReptileList(list)
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }
        })
    }

    private fun setUpFloatingActionButton(view: View) {
        optionsFab = view.findViewById(R.id.fabOptions)
        optionsFab.setOnClickListener {
            testViewModel.isFabChecked.value = !testViewModel.isFabChecked.value!!
            handleFabClicked(testViewModel.isFabChecked.value!!)
        }
        addFab = view.findViewById(R.id.fabAddReptile)
        addFabTextView = view.findViewById(R.id.addFabTextView)
        addFab.setOnClickListener{
            val intent = Intent(requireActivity(), AddEditReptileActivity::class.java)
            startActivity(intent)
        }

//        handleFabClicked(testViewModel.isFabChecked.value!!)
    }

    private fun handleFabClicked(isPressed: Boolean) {
        if (isPressed) {
            addFab.visibility = View.VISIBLE
            addFabTextView.visibility = View.VISIBLE
            optionsFab.startAnimation(openFabAnimation)
            addFab.startAnimation(traverseFromBottomFabAnimation)
            addFabTextView.startAnimation(traverseFromBottomFabAnimation)
            addFab.isClickable = true
        } else {
            optionsFab.startAnimation(closeFabAnimation)
            addFab.startAnimation(traverseBottomFabAnimation)
            addFabTextView.startAnimation(traverseBottomFabAnimation)
            addFab.visibility = View.GONE
            addFabTextView.visibility = View.GONE
            addFab.isClickable = false
        }
    }

    private fun setFabVisibility(isPressed: Boolean) {
        if (isPressed) {
            addFab.visibility = View.VISIBLE
            addFabTextView.visibility = View.VISIBLE
            addFab.isClickable = true
        } else {
            addFab.visibility = View.GONE
            addFabTextView.visibility = View.GONE
            addFab.isClickable = false
        }
    }

    override fun onReptileClicked(position: Int) {
        val reptileKey = reptileAdapter.getReptile(position).key

        if (reptileKey != null) {
            makeToast("$reptileKey")
            val intent = ReptileProfileActivity.makeIntent(requireActivity(), reptileKey)
            startActivity(intent)
        } else {
            makeToast("Error: Reptile Key not found!")
        }
    }
}