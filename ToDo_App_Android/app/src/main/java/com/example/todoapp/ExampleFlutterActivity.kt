package com.example.todoapp

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.widget.Toast
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler


class ExampleFlutterActivity : FlutterActivity(), MethodCallHandler {
    private lateinit var channel: MethodChannel

    companion object Factory {
        fun build(context: Context): Intent {
            return CachedEngineIntentBuilder(
                ExampleFlutterActivity::class.java,
                MainApplication.exampleActivityNoduleEngineId
            ).build(context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channel =
            MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, "example_activity_channel")
        channel.setMethodCallHandler(this)
        sendBatteryLevel()
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "showSnack" -> {
                val text = call.arguments
                if (text !is String) result.error("missing data", null, null)
                Toast.makeText(this, text as String, Toast.LENGTH_SHORT).show()
                result.success(null)
            }

            "getBatteryLevel" -> {
                result.success(null)
                sendBatteryLevel()
            }
        }
    }

    private fun sendBatteryLevel() {
        val bm: BatteryManager = context.getSystemService(BATTERY_SERVICE) as BatteryManager
        val lvl = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        channel.invokeMethod("sendBatteryLevel", lvl)
    }
}