package com.example.pethaven.ui.features.fav

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pethaven.R
import com.example.pethaven.adapter.ReptileBoxAdaptor
import com.example.pethaven.adapter.ReptileInfoAdapterFav
import com.example.pethaven.domain.Reptile
import com.example.pethaven.ui.features.home.AddEditReptileActivity
import com.example.pethaven.ui.features.home.ReptileProfileActivity
import com.example.pethaven.ui.features.shop.TradePostActivity
import com.example.pethaven.util.AndroidExtensions.makeToast
import com.example.pethaven.util.FactoryUtil
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_fav.*

class FavFragment : Fragment(), ReptileInfoAdapterFav.OnReptileItemCLickedListener {

    private lateinit var progressBar: ProgressBar

    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private lateinit var searchLayout: LinearLayout
    private lateinit var recyclerSearchView: RecyclerView
    private lateinit var reptileInfoAdapterFav: ReptileInfoAdapterFav
    private lateinit var reptileBoxRecyclerview: RecyclerView
    private lateinit var reptileBoxAdaptor: ReptileBoxAdaptor

    private lateinit var testViewModel: FavTestViewModel

    private lateinit var botAppBar: BottomAppBar
    private lateinit var addFab: FloatingActionButton


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_fav, container, false)

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
            val reptileKey = reptileInfoAdapterFav.getReptile(viewHolder.adapterPosition).key
            if (reptileKey != null) {
                val intent = TradePostActivity.makeIntent(requireActivity(),reptileKey)
                startActivity(intent)
            } else {
                makeToast("Error: Reptile Key not found!")
            }
            reptileInfoAdapterFav.notifyItemChanged(viewHolder.adapterPosition)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    // --------------------- Initializing Views ---------------- //
    private fun setUpProgressBar(view: View) {
        progressBar = view.findViewById(R.id.reptileListProgressBar)
        progressBar.isIndeterminate = true
    }

    private fun setUpSearchLayout(view: View) {
        searchLayout = view.findViewById(R.id.reptileInfoLayout)
        recyclerSearchView = view.findViewById(R.id.reptileInfoRecyclerView)
        if(testViewModel.isSearchOn.value!!)
        {
            searchLayout.visibility = View.VISIBLE
        }
        else
        {
            searchLayout.visibility = View.GONE
        }
        recyclerSearchView.setHasFixedSize(true)
        recyclerSearchView.layoutManager = LinearLayoutManager(requireActivity())

        reptileInfoAdapterFav = ReptileInfoAdapterFav(requireActivity(), ArrayList(), this, testViewModel)
        recyclerSearchView.adapter = reptileInfoAdapterFav

        val itemTouchHelper = ItemTouchHelper(swipeItemCallback)
        itemTouchHelper.attachToRecyclerView(recyclerSearchView)
    }

    private fun setUpReptileBoxRecyclerView(view: View)
    {
        reptileBoxRecyclerview = view.findViewById(R.id.reptileBoxRecyclerView)
        if(testViewModel.isSearchOn.value!!)
        {
            reptileBoxRecyclerview.visibility = View.GONE
        }
        else
        {
            reptileBoxRecyclerview.visibility = View.VISIBLE
        }
        reptileBoxRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            reptileBoxAdaptor = ReptileBoxAdaptor(requireActivity(), testViewModel)
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
        if(testViewModel.isSearchOn.value!!)
        {
            botAppBar.menu[0].icon = requireContext().getDrawable(R.drawable.ic_search_off)
        }
        else
        {
            botAppBar.menu[0].icon = requireContext().getDrawable(R.drawable.ic_search_white)
        }
        botAppBar.setOnMenuItemClickListener{
            when(it.itemId)
            {
                R.id.menuBtnSearch -> {
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
                    testViewModel.isSearchOn.value = !testViewModel.isSearchOn.value!!
                    true
                }
                R.id.menuBtnTop -> {
                    reptileInfoRecyclerView.adapter = null
                    reptileInfoRecyclerView.adapter = reptileInfoAdapterFav
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
                reptileInfoAdapterFav.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                reptileInfoAdapterFav.filter.filter(newText)
                return true
            }
        })
    }


    // --------------------- Initializing View Model  ---------------- //
    private fun setUpTestViewModel() {
        val factory = FactoryUtil.generateReptileViewModelFactoryFav(requireActivity())
        testViewModel = ViewModelProvider(this, factory)[FavTestViewModel::class.java]
        testViewModel.init()
        if(testViewModel.isSearchOn.value == null)
        {
            testViewModel.isSearchOn.value = false
        }

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
                testViewModel.reptilesBoxes.value!![0].clear()
                testViewModel.reptilesBoxes.value!![1].clear()
                testViewModel.reptilesBoxes.value!![2].clear()
                for (postSnapShot in snapshot.children) {
                    val reptile = postSnapShot.getValue(Reptile::class.java)
                    reptile?.let {
                        it.key = postSnapShot.key
                        list.add(it)
                        if(it.isFav)
                        {
                            val _added = testViewModel.addReptileToBox(it)
                        }
                    }
                }
                reptileInfoAdapterFav.setReptileList(list)
                reptileInfoAdapterFav.filter.filter(searchView.query)
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
        val reptileKey = reptileInfoAdapterFav.getReptile(position).key

        if (reptileKey != null) {
            val intent = ReptileProfileActivity.makeIntent(requireActivity(), reptileKey)
            startActivity(intent)
        } else {
            makeToast("Error: Reptile Key not found!")
        }
    }
}