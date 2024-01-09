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

class StartTimeTrackingActivityListener : Service(), MessageClient.OnMessageReceivedListener {
    override fun onCreate() {
        super.onCreate()
        Log.i(LOG_TAG, "Creating StartTimeTrackingActivityListener")
        Wearable.getMessageClient(this).addListener(this)
    }

    private val binder = Binder()

    override fun onBind(_unused: Intent?): IBinder? {
        return binder
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val path = messageEvent.path
        Log.i(LOG_TAG, "onMessageReceived $path")
        when (path) {
            START_TIME_TRACKING_ACTIVITY_PATH -> processStartTimeTrackingActivityMessage(messageEvent)
            // OTHER_PATH -> processOtherMessage(messageEvent)
            else -> Log.e(LOG_TAG, "Unable to process message with path $path")
        }
    }
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