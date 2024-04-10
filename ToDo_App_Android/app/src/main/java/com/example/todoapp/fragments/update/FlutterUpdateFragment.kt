package com.example.todoapp.fragments.update

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.todoapp.R
import com.example.todoapp.data.models.ToDoData
import com.example.todoapp.data.viewmodel.ToDoViewModel
import com.example.todoapp.fragments.SharedViewModel
import io.flutter.embedding.android.FlutterFragment
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

class FlutterUpdateFragment public constructor(
    private val mTodoViewModel: ToDoViewModel,
    private val args: UpdateFragmentArgs,
    private val mSharedViewModel: SharedViewModel
) : FlutterFragment(), MethodCallHandler {
    private lateinit var channel: MethodChannel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        channel = MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, "edit_todo_channel")

        channel.setMethodCallHandler(this)
        channel.invokeMethod("setInitialValue", args.currentItem.title)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun attemptToSave() {
        channel.invokeMethod("attemptToSave", null)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "editTodo" -> updateItem(call.arguments as String, result)
        }
    }


    private fun updateItem(title: String, result: MethodChannel.Result) {
        val desc = args.currentItem.description
        val priority =
            mSharedViewModel.parsePriorityString(args.currentItem.priority.toString())
        if (mSharedViewModel.verifyDataFromUser(title, desc)) {
            val updatedItem = ToDoData(
                id = args.currentItem.id,
                title = title,
                priority = priority,
                description = desc
            )
            mTodoViewModel.updateData(updatedItem)
            result.success(true)
            Toast.makeText(
                requireContext(),
                "Successfully Updated!",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            result.success(false)
            Toast.makeText(
                requireContext(),
                "Please fill out all the fields.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}