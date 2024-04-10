package com.example.todoapp.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todoapp.MainApplication
import com.example.todoapp.R
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.databinding.FragmentUpdateBinding
import com.example.todoapp.fragments.SharedViewModel
import io.flutter.embedding.android.FlutterFragment
import kotlinx.coroutines.runBlocking

class UpdateFragment : Fragment() {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val TAG_FLUTTER_FRAGMENT = "flutter_update_fragment"
    }

    private var flutterFragment: FlutterUpdateFragment? = null

    private val mSharedViewModel: SharedViewModel by viewModels()
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val args by navArgs<UpdateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        val fragmentManager: FragmentManager = parentFragmentManager

        flutterFragment = fragmentManager
            .findFragmentByTag(UpdateFragment.TAG_FLUTTER_FRAGMENT) as FlutterUpdateFragment?

        if (flutterFragment != null) {
            fragmentManager.beginTransaction().remove(flutterFragment as FlutterFragment).commit()
        }

        var newFlutterFragment = CustomCachedEngineFragmentBuilder(
            MainApplication.editTodoNoduleEngineId
        ).buildWithParams(
            mSharedViewModel,
            mToDoViewModel,
            args,
        )
        flutterFragment = newFlutterFragment
        fragmentManager
            .beginTransaction()
            .add(
                R.id.flutter_update_fragment,
                newFlutterFragment,
                UpdateFragment.TAG_FLUTTER_FRAGMENT
            )
            .commit()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save -> runBlocking {
                flutterFragment?.attemptToSave()
            }
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemRemoval() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setPositiveButton("YES") { _, _ ->
                mToDoViewModel.deleteItem(args.currentItem)
                Toast.makeText(
                    requireContext(),
                    "Successfully Removed: '${args.currentItem.title}'",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            setNegativeButton("NO") { _, _ -> /*DO NOTHING*/ }
            setTitle("Delete '${args.currentItem.title}'")
            setMessage("Are you sure you want to remove: '${args.currentItem.title}'?")
            create()
        }
        alertDialog.show()
    }
//
}