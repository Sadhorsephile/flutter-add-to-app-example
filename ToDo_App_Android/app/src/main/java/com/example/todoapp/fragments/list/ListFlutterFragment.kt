package com.example.todoapp.fragments.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import com.example.todoapp.data.models.ToDoData
import io.flutter.embedding.android.FlutterFragment
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


class ListFlutterFragment : FlutterFragment(), MethodChannel.MethodCallHandler {
    private var todos: List<ToDoData>? = null
    private var channel: MethodChannel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val engine = flutterEngine

        if (engine != null) {
            channel =
                MethodChannel(engine.dartExecutor.binaryMessenger, "list_todos_module")
            channel?.setMethodCallHandler(this)
            todos?.let { data -> setData(data) }
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }


    fun setData(toDoData: List<ToDoData>) {
        todos = toDoData
        val ch = channel ?: return

        val args = ArrayList<String>()
        args.addAll(toDoData.map { it -> "${it.id}:${it.title}" })
        ch.invokeMethod("sendList", args)


    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "onPressed" -> {
                val id = call.arguments
                val item = todos?.firstOrNull { it -> it.id.toString() == id }
                if (item != null) {
                    val action = ListFragmentDirections.actionListFragmentToUpdateFragment(item)
                    val navController = NavHostFragment.findNavController(this)
                    navController.navigate(action)
                }
            }

        }
    }
}