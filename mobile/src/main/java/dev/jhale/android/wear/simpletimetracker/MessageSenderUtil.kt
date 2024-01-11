package dev.jhale.android.wear.simpletimetracker

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable

class MessageSenderUtil {
    //Duplicates the Wear apps's MessagingFacade's sendMessage(). Shared library module this can go in instead?
    fun sendMessage(
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