package dev.jhale.android.wear.simpletimetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class STTBroadcastTransmitter @Inject constructor() {
    fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action

        when (action) {
            //This branch not used... see STTBroadcastReceiver
            "com.razeeman.util.simpletimetracker.ACTION_SEND_CATEGORIES" -> {
            }
            else -> {
                val message = intent?.extras?.getString("message") ?: "|"

                val messageParts = message.split("|")
                val activity = messageParts[0]
                val tag = messageParts[1]
                if (activity.isNotEmpty()) {
                    Intent().also { intent ->
                        intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                        intent.setPackage("com.razeeman.util.simpletimetracker.debug")
                        intent.putExtra("extra_activity_name", activity)
                        if (tag.isNotEmpty()) {
                            intent.putExtra("extra_record_tag", tag)
                        }
                        Log.i(LOG_TAG, "Broadcasting Intent: $intent")
                        context?.sendBroadcast(intent)
                    }
                }
            }
        }
    }
}