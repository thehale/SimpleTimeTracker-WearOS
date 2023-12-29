package dev.jhale.android.wear.simpletimetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class STTBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (intent == null || action == null) { return }

        Log.i(LOG_TAG, action)

        when (action) {
            "com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_CATEGORIES_RESPONSE" -> {
                intent.getStringExtra("categories")?.also {
                    Log.i(LOG_TAG, it)

                    if (context != null) {
                        returnCategoriesList(context, it)
                    }
                }
            }

            "com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_PREFS_RESPONSE" -> {
                intent.getBooleanExtra("allow_multitasking", false)?.also {
                    Log.i(LOG_TAG, it.toString())
                }
            }

            else -> {
            }
        }
    }

    fun returnCategoriesList(context: Context, sttExportModelJson: String) {
        Thread(Runnable {
            sendMessage(
                context,
                MobileMessagingFacade.RECEIVE_CATEGORIES,
                sttExportModelJson
            )
        }).start()
    }

    //rather be able to re-use MobileMessagingFacade or some other consistent phone->wear thing
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
}