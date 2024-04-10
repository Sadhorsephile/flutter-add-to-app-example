package com.example.todoapp.fragments.update

import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.fragments.SharedViewModel
import io.flutter.embedding.android.FlutterFragment.CachedEngineFragmentBuilder

/**
 * Кастомный билдер для Flutter-фрагмента редактирования заметки.
 */
class CustomCachedEngineFragmentBuilder(engineId: String) :
    CachedEngineFragmentBuilder(FlutterUpdateFragment::class.java, engineId) {

    fun buildWithParams(
        mSharedViewModel: SharedViewModel,
        mTodoViewModel: ToDoViewModel,
        args: UpdateFragmentArgs
    ): FlutterUpdateFragment {
        val frag = FlutterUpdateFragment(mTodoViewModel, args, mSharedViewModel)
        val args = createArgs()
        frag.arguments = args

        return frag
    }
}