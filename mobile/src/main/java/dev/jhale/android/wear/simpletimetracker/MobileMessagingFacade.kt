/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val LOG_TAG = "dev.jhale.android.wear.simpletimetracker"

@AndroidEntryPoint
class MobileMessagingFacade : Service(), MessageClient.OnMessageReceivedListener {
    @Inject
    lateinit var sttBroadcastTransmitter: STTBroadcastTransmitter

    lateinit var sttBroadcastHandler: BroadcastReceiver

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

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(sttBroadcastHandler)
        super.onDestroy()
    }

    //might move out of here and into something triggerable by broadcast receiver
    fun returnCategoriesList(context: Context, categories: List<String>) {
        Thread(Runnable {
            sendMessage(
                context,
                RECEIVE_CATEGORIES,
                categories.joinToString()
            )
        }).start()
    }

    private fun sendMessage(
        context: Context,
        capability: String,
        message: String
    ) {
        // Find all nodes which support the time tracking message
        val capabilityInfo: CapabilityInfo = Tasks.await(
            Wearable.getCapabilityClient(context)
                .getCapability(
                    capability,
                    CapabilityClient.FILTER_REACHABLE
                )
        )

        // Choose the best node (the closest one connected to the watch
        val nodes = capabilityInfo.nodes
        val bestNode = nodes.firstOrNull { it.isNearby }?.id ?: nodes.firstOrNull()?.id

        // Send the message
        bestNode?.also { nodeId ->
            val sendTask: Task<*> = Wearable.getMessageClient(context).sendMessage(
                nodeId,
                "/$capability",
                message.toByteArray()
            ).apply {
                addOnSuccessListener {
                    Log.i(
                        LOG_TAG,
                        "Sent $capability message: $message"
                    )
                }
                addOnFailureListener {
                    Log.e(
                        LOG_TAG,
                        "Failed to send $capability message: $message"
                    )
                }
            }
        } ?: run {
            Log.e(
                LOG_TAG,
                "No nodes found with the capability $capability"
            )
        }
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
                    Intent("com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_CATEGORIES_RESPONSE")
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