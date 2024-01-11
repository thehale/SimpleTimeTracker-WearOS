/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.jhale.android.wear.simpletimetracker.data.TimeTrackingActivity
import dev.jhale.android.wear.simpletimetracker.presentation.LOG_TAG

class MessagingFacade : MessageClient.OnMessageReceivedListener {

    // This the right way to implement collections of constants in Kotlin?
    companion object KnownMessages {
        const val START_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME = "start_time_tracking_activity";
        const val STOP_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME = "stop_time_tracking_activity";
        const val REQUEST_CATEGORIES = "request_categories"
        const val REQUEST_PREFS = "request_prefs"
        const val RECEIVE_CATEGORIES = "/receive_categories"
        const val RECEIVE_PREFS = "receive_prefs"
    }

    lateinit var categoriesCallback: (List<TimeTrackingActivity>) -> Unit

    fun requestCategoriesList(
        context: Context,
        categoriesCallback: (List<TimeTrackingActivity>) -> Unit
    ) {
        this.categoriesCallback = categoriesCallback

        Thread(Runnable {
            sendMessage(
                context,
                REQUEST_CATEGORIES
            )
        }).start()
    }

    fun startTimeTracking(context: Context, activity: String, tag: String) {
        Thread(Runnable {
            sendMessage(
                context,
                START_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME,
                "$activity|$tag"
            )
        }).start()
    }

    fun stopTimeTracking(context: Context, activity: String, tag: String) {
        Thread(Runnable {
            sendMessage(
                context,
                STOP_TIME_TRACKING_ACTIVITY_CAPABILITY_NAME,
                "$activity|$tag"
            )
        }).start()
    }

    private fun sendMessage(
        context: Context,
        capability: String,
        message: String = ""
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
        when (messageEvent.path) {
            RECEIVE_CATEGORIES -> {
                val gson = Gson()
                val sttExportModel =
                    object : TypeToken<Map<Long, STTAdhocExportRecordModel>>() {}.type

                val activitiesList = gson.fromJson<Map<Long, STTAdhocExportRecordModel>>(
                    String(messageEvent.data),
                    sttExportModel
                )

                val categoryNames = activitiesList.values
                    .map { activity ->
                        TimeTrackingActivity(
                            activity.name,
                            color = Color(activity.colorInt),
                            timeStarted = activity.timeStarted
                        )
                    }
                categoriesCallback(categoryNames)
            }

            else -> {}
        }
    }
}