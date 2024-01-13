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
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

const val LOG_TAG = "dev.jhale.android.wear.simpletimetracker"

class MessageReceiverService : Service(), MessageClient.OnMessageReceivedListener {
    private val sttBroadcastTransmitter: STTBroadcastTransmitter = STTBroadcastTransmitter()

    // This the right way to implement collections of constants in Kotlin?
    // hoist to common library module?
    companion object {
        const val START_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME = "start_time_tracking_activity";
        const val STOP_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME = "stop_time_tracking_activity";
        const val REQUEST_CATEGORIES = "request_categories"
        const val RECEIVE_CATEGORIES = "receive_categories"
    }

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
        val truncatedEventPath = messageEvent.path.drop(1)

        when (truncatedEventPath) {
            START_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME -> {
                processStartTimeTrackingActivityMessage(messageEvent)
            }

            STOP_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME -> {
                processStopTimeTrackingActivityMessage(messageEvent)
            }

            REQUEST_CATEGORIES -> {
                sttBroadcastTransmitter.transmitSTTBroadcast(
                    this,
                    Intent("com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_CATEGORIES")
                )
            }

            else -> {
                Log.d(LOG_TAG, "Propagating message with path ${messageEvent.path}")
            }
        }
    }

    private fun processStartTimeTrackingActivityMessage(messageEvent: MessageEvent) {
        val message = String(messageEvent.data)
        Log.d(LOG_TAG, "Message received: $message")

        val messageParts = message.split("|")
        val activity = messageParts[0]
        val tag = messageParts[1]
        if (activity.isNotEmpty()) {
            sttBroadcastTransmitter.transmitSTTBroadcast(this,
                Intent().also { intent ->
                    intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                    intent.putExtra("extra_activity_name", activity)
                    if (tag.isNotEmpty()) {
                        intent.putExtra("extra_record_tag", tag)
                    }
                })
        }
    }

    private fun processStopTimeTrackingActivityMessage(messageEvent: MessageEvent) {
        val message = String(messageEvent.data)
        Log.d(LOG_TAG, "Message received: $message")

        val messageParts = message.split("|")
        val activity = messageParts[0]
        val tag = messageParts[1]
        if (activity.isNotEmpty()) {
            sttBroadcastTransmitter.transmitSTTBroadcast(this,
                Intent().also { intent ->
                    intent.action = "com.razeeman.util.simpletimetracker.ACTION_STOP_ACTIVITY"
                    intent.putExtra("extra_activity_name", activity)
                    if (tag.isNotEmpty()) {
                        intent.putExtra("extra_record_tag", tag)
                    }
                })
        }
    }
}