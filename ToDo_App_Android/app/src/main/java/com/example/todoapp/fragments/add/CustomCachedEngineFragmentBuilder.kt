package com.example.todoapp.fragments.add

import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.fragments.SharedViewModel
import io.flutter.embedding.android.FlutterFragment.CachedEngineFragmentBuilder

/**
 * Кастомный билдер для Flutter-фрагмента добавления заметки.
 */
class CustomCachedEngineFragmentBuilder(engineId: String) :
    CachedEngineFragmentBuilder(FlutterAddFragment::class.java, engineId) {

    fun buildWithParam(mSharedViewModel: SharedViewModel, mToDoViewModel: ToDoViewModel): FlutterAddFragment {
        val frag = FlutterAddFragment(mSharedViewModel, mToDoViewModel)
        val args = createArgs()
        frag.arguments = args

        return frag
    }
}