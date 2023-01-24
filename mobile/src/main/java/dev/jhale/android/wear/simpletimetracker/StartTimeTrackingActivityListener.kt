package dev.jhale.android.wear.simpletimetracker

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

const val LOG_TAG = "dev.jhale.android.wear.simpletimetracker"
const val START_TIME_TRACKING_ACTIVITY_PATH = "/start_time_tracking_activity"

class StartTimeTrackingActivityListener : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path.equals(START_TIME_TRACKING_ACTIVITY_PATH)) {
            processStartTimeTrackingActivityMessage(messageEvent)
        } else {
            Log.d(LOG_TAG, "Propagating message with path ${messageEvent.path}")
            super.onMessageReceived(messageEvent)
        }
    }
    private fun processStartTimeTrackingActivityMessage(messageEvent: MessageEvent) {
        val message = String(messageEvent.data)
        Log.d(LOG_TAG, "Message received: $message")
        Intent().also {intent ->
            intent.action = Intent.ACTION_SEND
            intent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }
}