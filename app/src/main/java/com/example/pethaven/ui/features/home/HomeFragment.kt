package com.example.pethaven.ui.features.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.adapter.ReptileBoxAdaptor
import com.example.pethaven.adapter.ReptileInfoAdapter
import com.example.pethaven.domain.Reptile
import com.example.pethaven.ui.features.shop.TradePostActivity
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

/**
 *  Fragment showing list of user collection of reptiles
 */
class HomeFragment : Fragment(), ReptileInfoAdapter.OnReptileItemCLickedListener {

    private lateinit var fabLayout: LinearLayout

    private lateinit var progressBar: ProgressBar

    private lateinit var addFabTextView: TextView
    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var recyclerSearchView: RecyclerView
    private lateinit var reptileInfoAdapter: ReptileInfoAdapter
    private lateinit var reptileBoxRecyclerview: RecyclerView
    private lateinit var reptileBoxAdaptor: ReptileBoxAdaptor

    private lateinit var testViewModel: HomeTestViewModel

    private lateinit var addFab: FloatingActionButton
    private lateinit var optionsFab: FloatingActionButton

    private lateinit var openFabAnimation: Animation
    private lateinit var closeFabAnimation: Animation
    private lateinit var traverseFromBottomFabAnimation: Animation
    private lateinit var traverseBottomFabAnimation: Animation

    private var valueEventListener: ValueEventListener? = null
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_home_test, container, false)
        openFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_open)
        closeFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_close)
        traverseBottomFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_traverse_bottom)
        traverseFromBottomFabAnimation = AnimationUtils.loadAnimation(requireActivity(), R.anim.anim_fab_traverse_from_bottom)
        fabLayout = view.findViewById(R.id.addFabLayout)

        setUpTestViewModel()
        setUpRecyclerView(view)
        setUpProgressBar(view)
        setUpFloatingActionButton(view)

        receiveAllReptiles(view)
        //setUpReptileBoxRecyclerView(view)
        setUpSearchView(view)
        setFabVisibility(testViewModel.isFabChecked.value!!)

        return view
    }

    override fun onDestroy() {
        valueEventListener?.let { databaseReference.removeEventListener(it) }
    }

    private val swipeItemCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        // Start Trade Post Activity when swiped
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val reptileKey = reptileInfoAdapter.getReptile(viewHolder.adapterPosition).key
            if (reptileKey != null) {
                val intent = TradePostActivity.makeIntent(requireActivity(),reptileKey)
                startActivity(intent)
            } else {
                makeToast("Error: Reptile Key not found!")
            }
            reptileInfoAdapter.notifyItemChanged(viewHolder.adapterPosition)
        }
    }


    // --------------------- Initializing Views ---------------- //
    private fun setUpProgressBar(view: View) {
        progressBar = view.findViewById(R.id.reptileListProgressBar)
        progressBar.isIndeterminate = true
    }

    private fun setUpRecyclerView(view: View) {
        recyclerSearchView = view.findViewById(R.id.reptileInfoRecyclerView)
        recyclerSearchView.setHasFixedSize(true)
        recyclerSearchView.layoutManager = LinearLayoutManager(requireActivity())

        reptileInfoAdapter = ReptileInfoAdapter(requireActivity(), ArrayList(), this)
        recyclerSearchView.adapter = reptileInfoAdapter

        val itemTouchHelper = ItemTouchHelper(swipeItemCallback)
        itemTouchHelper.attachToRecyclerView(recyclerSearchView)
    }

/*
    private fun setUpReptileBoxRecyclerView(view: View)
    {
        reptileBoxRecyclerview = view.findViewById(R.id.reptileBoxRecyclerView)
        reptileBoxRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            reptileBoxAdaptor = ReptileBoxAdaptor()
            adapter = reptileBoxAdaptor
            reptileBoxAdaptor.setOnItemClickListener(object : ReptileBoxAdaptor.OnItemClickListener
            {
                override fun onItemClick(position: Int) {
                    testViewModel.toggleBtnSwitch(position)
                    refreshList()
                }
            })
        }
    }
*/

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
                reptileInfoAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                reptileInfoAdapter.filter.filter(newText)
                return true
            }
        })
    }


    // --------------------- Initializing View Model  ---------------- //
    private fun setUpTestViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(requireActivity())
        testViewModel = ViewModelProvider(this, factory)[HomeTestViewModel::class.java]

/*        testViewModel.reptilesBoxes.observe(viewLifecycleOwner)
        {
            println("debug: reptile boxes changed")
            reptileBoxAdaptor.updateList(testViewModel.reptilesBoxes.value!!, testViewModel.btnSwitches.value!!)
        }

        testViewModel.btnSwitches.observe(viewLifecycleOwner)
        {
            println("debug: btn switches changed")
            reptileBoxAdaptor.updateList(testViewModel.reptilesBoxes.value!!, testViewModel.btnSwitches.value!!)
        }*/
    }

    // --------------------- Updating Views  ---------------- //
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

    private fun refreshList()
    {
        reptileBoxAdaptor.notifyDataSetChanged()
        reptileBoxRecyclerview.apply {
            //adapter = null
            //adapter = reptileAdaptor
        }
    }

    // --------------------- Database Operations  ---------------- //
    private fun receiveAllReptiles(view: View) {
        databaseReference = testViewModel.reptileTask
        valueEventListener = databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    progressBar.visibility = View.GONE
                    return
                }

                val list = ArrayList<Reptile>()
                testViewModel.reptilesBoxes.value!!.clear()
                for (postSnapShot in snapshot.children) {
                    val reptile = postSnapShot.getValue(Reptile::class.java)
                    reptile?.let {
                        it.key = postSnapShot.key
                        list.add(it)
                        testViewModel.addReptile(it)
                    }
                }
                reptileInfoAdapter.setReptileList(list)
                reptileInfoAdapter.filter.filter(searchView.query)
                // searchView.setQuery(searchView.query, true)

                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
                progressBar.visibility = View.GONE
            }
        })
    }


    // --------------------- Adapter OnClick Listener  ---------------- //
    override fun onReptileClicked(position: Int) {
        val reptileKey = reptileInfoAdapter.getReptile(position).key

        if (reptileKey != null) {
            val intent = ReptileProfileActivity.makeIntent(requireActivity(), reptileKey)
            startActivity(intent)
        } else {
            makeToast("Error: Reptile Key not found!")
        }
    }
}

/*
package com.example.pethaven.ui.features.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.adapter.ReptileBoxAdaptor
import com.example.pethaven.adapter.ReptileInfoAdapter
import com.example.pethaven.domain.Reptile
import com.example.pethaven.ui.features.shop.TradePostActivity
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home_test.*

class HomeFragment : Fragment(), ReptileInfoAdapter.OnReptileItemCLickedListener {

    private lateinit var progressBar: ProgressBar

    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var searchLayout: LinearLayout
    private lateinit var recyclerSearchView: RecyclerView
    private lateinit var reptileInfoAdapter: ReptileInfoAdapter
    private lateinit var reptileBoxRecyclerview: RecyclerView
    private lateinit var reptileBoxAdaptor: ReptileBoxAdaptor

    private lateinit var testViewModel: HomeTestViewModel

    private lateinit var botAppBar: BottomAppBar
    private lateinit var addFab: FloatingActionButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_home_test, container, false)

        setUpTestViewModel()
        setUpSearchLayout(view)
        setUpProgressBar(view)
        setUpBotAppBar(view)

        receiveAllReptiles()
        setUpReptileBoxRecyclerView(view)
        setUpSearchView(view)

        return view
    }

    private val swipeItemCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        // Start Trade Post Activity when swiped
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val reptileKey = reptileInfoAdapter.getReptile(viewHolder.adapterPosition).key
            if (reptileKey != null) {
                val intent = TradePostActivity.makeIntent(requireActivity(),reptileKey)
                startActivity(intent)
            } else {
                makeToast("Error: Reptile Key not found!")
            }
            reptileInfoAdapter.notifyItemChanged(viewHolder.adapterPosition)
        }
    }


    // --------------------- Initializing Views ---------------- //
    private fun setUpProgressBar(view: View) {
        progressBar = view.findViewById(R.id.reptileListProgressBar)
        progressBar.isIndeterminate = true
    }

    private fun setUpSearchLayout(view: View) {
        searchLayout = view.findViewById(R.id.reptileInfoLayout)
        recyclerSearchView = view.findViewById(R.id.reptileInfoRecyclerView)
        recyclerSearchView.setHasFixedSize(true)
        recyclerSearchView.layoutManager = LinearLayoutManager(requireActivity())

        reptileInfoAdapter = ReptileInfoAdapter(requireActivity(), ArrayList(), this)
        recyclerSearchView.adapter = reptileInfoAdapter

        val itemTouchHelper = ItemTouchHelper(swipeItemCallback)
        itemTouchHelper.attachToRecyclerView(recyclerSearchView)
    }

    private fun setUpReptileBoxRecyclerView(view: View)
    {
        reptileBoxRecyclerview = view.findViewById(R.id.reptileBoxRecyclerView)
        reptileBoxRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            reptileBoxAdaptor = ReptileBoxAdaptor(requireActivity())
            adapter = reptileBoxAdaptor
            reptileBoxAdaptor.setOnItemClickListener(object : ReptileBoxAdaptor.OnItemClickListener
            {
                override fun onItemClick(position: Int) {
                    testViewModel.toggleBtnSwitch(position)
                }
            })
        }
    }

    private fun setUpBotAppBar(view: View) {
        botAppBar = view.findViewById(R.id.botAppBar)
        addFab = view.findViewById(R.id.fabAddReptile)
        addFab.setOnClickListener{
            val intent = Intent(requireActivity(), AddEditReptileActivity::class.java)
            startActivity(intent)
        }
        botAppBar.setOnMenuItemClickListener{
            when(it.itemId)
            {
                R.id.menuBtnSearch -> {
                    testViewModel.isSearchOn.value = !testViewModel.isSearchOn.value!!
                    if(!testViewModel.isSearchOn.value!!)
                    {
                        it.icon = requireContext().getDrawable(R.drawable.ic_search_off)
                        searchLayout.visibility = View.VISIBLE
                        reptileBoxRecyclerview.visibility = View.GONE
                    }
                    else
                    {
                        it.icon = requireContext().getDrawable(R.drawable.ic_search_white)
                        searchLayout.visibility = View.GONE
                        reptileBoxRecyclerview.visibility = View.VISIBLE
                    }
                    true
                }
                R.id.menuBtnTop -> {
                    reptileInfoRecyclerView.adapter = null
                    reptileInfoRecyclerView.adapter = reptileInfoAdapter
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun setUpSearchView(view: View) {
        searchView = view.findViewById(R.id.reptileSearchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                reptileInfoAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                reptileInfoAdapter.filter.filter(newText)
                return true
            }
        })
    }


    // --------------------- Initializing View Model  ---------------- //
    private fun setUpTestViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactory(requireActivity())
        testViewModel = ViewModelProvider(this, factory)[HomeTestViewModel::class.java]

        testViewModel.reptilesBoxes.observe(viewLifecycleOwner)
        {
            println("debug: reptile boxes changed")
            reptileBoxAdaptor.updateList(testViewModel.reptilesBoxes.value!!, testViewModel.btnSwitches.value!!)
        }

        testViewModel.btnSwitches.observe(viewLifecycleOwner)
        {
            println("debug: btn switches changed")
            reptileBoxAdaptor.updateList(testViewModel.reptilesBoxes.value!!, testViewModel.btnSwitches.value!!)
        }
    }


    // --------------------- Database Operations  ---------------- //
    private fun receiveAllReptiles() {
        testViewModel.reptileTask.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    progressBar.visibility = View.GONE
                    return
                }

                val list = ArrayList<Reptile>()
                testViewModel.reptilesBoxes.value!!.clear()
                for (postSnapShot in snapshot.children) {
                    val reptile = postSnapShot.getValue(Reptile::class.java)
                    reptile?.let {
                        //println(it.isFav)
                        it.key = postSnapShot.key
                        list.add(it)
                        testViewModel.addReptile(it)
                    }
                }
                reptileInfoAdapter.setReptileList(list)
                reptileInfoAdapter.filter.filter(searchView.query)
                // searchView.setQuery(searchView.query, true)

                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                makeToast(error.message)
                progressBar.visibility = View.GONE
            }
        })
    }


    // --------------------- Adapter OnClick Listener  ---------------- //
    override fun onReptileClicked(position: Int) {
        val reptileKey = reptileInfoAdapter.getReptile(position).key

        if (reptileKey != null) {
            val intent = ReptileProfileActivity.makeIntent(requireActivity(), reptileKey)
            startActivity(intent)
        } else {
            makeToast("Error: Reptile Key not found!")
        }
    }
}*/
