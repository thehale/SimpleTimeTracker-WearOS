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
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

const val LOG_TAG = "dev.jhale.android.wear.simpletimetracker"
const val START_TIME_TRACKING_ACTIVITY_PATH = "/start_time_tracking_activity"

class StartTimeTrackingActivityListener : Service(), MessageClient.OnMessageReceivedListener {
    inner class FakeBinder : Binder()

    private val binder = FakeBinder()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Wearable.getMessageClient(this).addListener(this);
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path.equals(START_TIME_TRACKING_ACTIVITY_PATH)) {
            processStartTimeTrackingActivityMessage(messageEvent)
        } else {
            Log.d(LOG_TAG, "Propagating message with path ${messageEvent.path}")
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