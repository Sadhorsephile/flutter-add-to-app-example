package com.example.todoapp.fragments.add

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
import java.util.*

class FlutterAddFragment public constructor(
    private val mSharedViewModel: SharedViewModel,
    private val mToDoViewModel: ToDoViewModel
) : FlutterFragment(), MethodCallHandler {

    private lateinit var channel: MethodChannel
    fun tryToAdd() {
        channel.invokeMethod("tryToAddTodo", null)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        channel = MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, "add_todo_channel")
        channel.setMethodCallHandler(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "addTodo"-> insertDataToDB(call.arguments as String, result)
        }
    }

    private fun insertDataToDB(title: String, result: MethodChannel.Result) {
        val mPriority = "High"
        val mDescription = "Description"
        val validation = mSharedViewModel.verifyDataFromUser(title, mDescription)
        if(validation) {
            val newData = ToDoData(
                Calendar.getInstance().timeInMillis.toInt(),
                title,
                mSharedViewModel.parsePriorityString(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            result.success(true)
            Toast.makeText(
                requireContext(),
                "Successfully Added!",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().navigate(R.id.action_addFragment_to_listFragment)
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