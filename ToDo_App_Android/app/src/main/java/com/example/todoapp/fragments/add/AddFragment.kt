package com.example.todoapp.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todoapp.MainApplication
import com.example.todoapp.R
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.databinding.FragmentAddBinding
import com.example.todoapp.fragments.SharedViewModel
import io.flutter.embedding.android.FlutterFragment
import io.flutter.embedding.android.FlutterFragment.CachedEngineFragmentBuilder

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null

    private val binding get() = _binding!!

    companion object {
        private const val TAG_FLUTTER_FRAGMENT = "flutter_add_fragment"
    }

    private var flutterFragment: FlutterAddFragment? = null

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBinding.inflate(inflater, container, false)

        val fragmentManager: FragmentManager = parentFragmentManager

        flutterFragment = fragmentManager
            .findFragmentByTag(TAG_FLUTTER_FRAGMENT) as FlutterAddFragment?

        if (flutterFragment != null) {
            fragmentManager.beginTransaction().remove(flutterFragment as FlutterFragment).commit()
        }

        var newFlutterFragment = CustomCachedEngineFragmentBuilder(
            MainApplication.addTodoNoduleEngineId
        ).buildWithParam(mSharedViewModel, mToDoViewModel)
        flutterFragment = newFlutterFragment
        fragmentManager
            .beginTransaction()
            .add(
                R.id.flutter_add_fragment,
                newFlutterFragment,
                TAG_FLUTTER_FRAGMENT
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
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add)
            // Если используем кнопку сохранения из апп-бара,
            // извещаем об этом Flutter-фрагмент
            flutterFragment?.tryToAdd()

        return super.onOptionsItemSelected(item)
    }

}