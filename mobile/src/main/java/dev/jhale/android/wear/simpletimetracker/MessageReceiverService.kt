/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

const val LOG_TAG = "dev.jhale.android.wear.simpletimetracker"
const val START_TIME_TRACKING_ACTIVITY_PATH = "/start_time_tracking_activity"

/**
 * Landing point for messages coming to Mobile from Wear.
 *
 * Implemented as a Service so that it can receive messages even when the Mobile app is stopped or
 * in the background.
 */
class MessageReceiverService : Service(), MessageClient.OnMessageReceivedListener {
    override fun onCreate() {
        super.onCreate()
        Log.i(LOG_TAG, "Creating MessageReceiverService")
        Wearable.getMessageClient(this).addListener(this)
    }

    private val binder = Binder()

    override fun onBind(_unused: Intent?): IBinder? {
        return binder
    }

    /**
     * Delegates received messages to their respective handlers
     */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        val path = messageEvent.path
        Log.i(LOG_TAG, "onMessageReceived $path")
        when (path) {
            START_TIME_TRACKING_ACTIVITY_PATH -> processStartTimeTrackingActivityMessage(messageEvent)
            // OTHER_PATH -> processOtherMessage(messageEvent)
            else -> Log.e(LOG_TAG, "Unable to process message with path $path")
        }
    }

    /**
     * Handles messages requesting the start of a new time tracking activity
     */
    private fun processStartTimeTrackingActivityMessage(messageEvent: MessageEvent) {
        val message = String(messageEvent.data)
        Log.i(LOG_TAG, "Message received: $message")
        Intent().also {intent ->
            intent.action = Intent.ACTION_SEND
            intent.putExtra("message", message)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }
}