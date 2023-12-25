package dev.jhale.android.wear.simpletimetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class STTBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        val action = intent?.action
        if (intent == null || action == null) { return }

        Log.i(LOG_TAG, action)

        when (action) {
            "com.razeeman.util.simpletimetracker.ACTION_SEND_CATEGORIES" -> {
                intent.getStringExtra("categories")?.also {
                    Log.i(LOG_TAG, it)
                }
            }

            else -> {
            }
        }
    }
}