package com.example.pethaven.ui.features.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.SearchView
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
    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var recyclerView: RecyclerView
    private lateinit var reptileAdapter: ReptileInfoAdapter

    private lateinit var testViewModel: HomeTestViewModel

    private lateinit var openFabAnimation: Animation
    private lateinit var closeFabAnimation: Animation
    private lateinit var traverseFromBottomFabAnimation: Animation
    private lateinit var traverseBottomFabAnimation: Animation

    private lateinit var fabLayout: LinearLayout

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
        fabLayout = view.findViewById(R.id.addFabLayout)

        setUpTestViewModel()
        setUpRecyclerView(view)
        setUpFloatingActionButton(view)

        receiveAllReptiles(view)
        setUpSearchView(view)
        setFabVisibility(testViewModel.isFabChecked.value!!)

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
    }

    private fun setUpSearchView(view: View) {
        searchView = view.findViewById(R.id.reptileSearchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                println("debug: OnQueryTextSubmit called")
                reptileAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                println("debug: OnQueryTextChanged")
                reptileAdapter.filter.filter(newText)
                return true
            }
        })
    }


    private fun receiveAllReptiles(view:View) {
        testViewModel.reptileList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    return
                }

                val list = ArrayList<Reptile>()
                for (postSnapShot in snapshot.children) {
                    val reptile = postSnapShot.getValue(Reptile::class.java)
                    reptile?.let {
                        it.key = postSnapShot.key
                        list.add(it)
                    }
                }
                reptileAdapter.setReptileList(list)

                reptileAdapter.filter.filter(searchView.query)
                // searchView.setQuery(searchView.query, true)
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
            }
        })
    }


    private fun handleFabClicked(isPressed: Boolean) {
        if (isPressed) {
            addFab.visibility = View.VISIBLE
            addFabTextView.visibility = View.VISIBLE
            fabLayout.visibility = View.VISIBLE

            optionsFab.startAnimation(openFabAnimation)
            fabLayout.startAnimation(traverseFromBottomFabAnimation)

            addFab.isClickable = true
        } else {
            optionsFab.startAnimation(closeFabAnimation)
            fabLayout.startAnimation(traverseBottomFabAnimation)
            fabLayout.visibility = View.GONE
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
            val intent = ReptileProfileActivity.makeIntent(requireActivity(), reptileKey)
            startActivity(intent)
        } else {
            makeToast("Error: Reptile Key not found!")
        }
    }
}