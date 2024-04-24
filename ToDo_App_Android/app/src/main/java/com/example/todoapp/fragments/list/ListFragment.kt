package com.example.todoapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.todoapp.ExampleFlutterActivity
import com.example.todoapp.MainApplication
import com.example.todoapp.R
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.databinding.FragmentListBinding
import com.example.todoapp.fragments.SharedViewModel
import com.example.todoapp.utils.hideKeyboard
import com.example.todoapp.utils.observeOnce
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.RenderMode
import io.flutter.embedding.android.TransparencyMode
import kotlinx.coroutines.*

class ListFragment : Fragment(), SearchView.OnQueryTextListener {
    companion object {
        private const val TAG_FLUTTER_FRAGMENT = "flutter_fragment"
    }

    private var flutterFragment: ListFlutterFragment? = null
    private var _binding: FragmentListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this


        val fragmentManager: FragmentManager = parentFragmentManager

        flutterFragment = fragmentManager
            .findFragmentByTag(TAG_FLUTTER_FRAGMENT) as? ListFlutterFragment

        if (flutterFragment != null) {
            fragmentManager.beginTransaction().remove(flutterFragment as ListFlutterFragment)
                .commit()
        }


        val newFlutterFragment = FlutterFragment.CachedEngineFragmentBuilder(
            ListFlutterFragment::class.java,
            MainApplication.listTodosModuleEngineId
        ).renderMode(RenderMode.texture)
            .build<ListFlutterFragment>()


        flutterFragment = newFlutterFragment
        fragmentManager
            .beginTransaction()
            .add(
                R.id.fragment_container_view,
                newFlutterFragment,
                TAG_FLUTTER_FRAGMENT
            )
            .commit()

        // set menu
        setHasOptionsMenu(true)

        // hide soft keyboard
        hideKeyboard(requireActivity())

        setUpRecyclerView()
        mToDoViewModel.getAllData.observe(viewLifecycleOwner) { data ->
            mSharedViewModel.isDatabaseEmpty(data)
            flutterFragment?.setData(data)
        }


        return binding.root
    }

    private fun setUpRecyclerView() {


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoval()
            R.id.menu_open_flutter_activity -> startActivity(
                ExampleFlutterActivity.build(requireContext())
            )

            R.id.menu_priority_high -> mToDoViewModel
                .sortedDataHigh
                .observe(viewLifecycleOwner) {
                    flutterFragment?.setData(it)
                }

            R.id.menu_priority_low -> mToDoViewModel
                .sortedDataLow
                .observe(viewLifecycleOwner) {
                    flutterFragment?.setData(it)
                }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoval() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setPositiveButton("YES") { _, _ ->
                mToDoViewModel.deleteAll()
                Toast.makeText(
                    requireContext(),
                    "Successfully Removed everything! ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            setNegativeButton("NO") { _, _ -> /*DO NOTHING*/ }
            setTitle("Delete Everything?")
            setMessage("Are you sure you want to remove: Everything?")
            create()
        }
        alertDialog.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchJob?.cancel()
            searchJob = coroutineScope.launch {
                delay(500)
                searchThroughDatabase(query)
            }
        }
        return true
    }

    private fun searchThroughDatabase(query: String) {
        val searchQuery = "%$query%"
        mToDoViewModel
            .searchDatabase(searchQuery)
            .observeOnce(viewLifecycleOwner) {
                flutterFragment?.setData(it)
            }
    }
}