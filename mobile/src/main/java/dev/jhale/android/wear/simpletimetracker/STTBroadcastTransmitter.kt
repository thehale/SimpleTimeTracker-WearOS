package dev.jhale.android.wear.simpletimetracker

import android.content.Context
import android.content.Intent
import android.util.Log

class STTBroadcastTransmitter {
    fun transmitSTTBroadcast(context: Context, intent: Intent) {
        intent.setPackage("com.razeeman.util.simpletimetracker.debug")
        intent.putExtra("callback_package_name", "dev.jhale.android.wear.simpletimetracker")
        Log.i(LOG_TAG, "Broadcasting Intent: $intent")
        context.sendBroadcast(intent)
    }
}