/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.example.util.simpletimetracker.wearrpc

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CompletableDeferred
import java.util.concurrent.CancellationException

interface Messenger {
    suspend fun send(capability: String): ByteArray? {
        return this.send(capability, ByteArray(0))
    }
    suspend fun send(capability: String, message: ByteArray): ByteArray?
}

class ContextMessenger(private val context: Context): Messenger {
    private val TAG: String = ContextMessenger::class.java.name

    override suspend fun send(capability: String, message: ByteArray): ByteArray? {
        val def = CompletableDeferred<ByteArray?>()
        val bestNode = findNearestNode(capability)

        // Send the message
        bestNode?.also { nodeId ->
            Log.d(TAG, "Sending message to ${bestNode?.displayName}")
            Wearable.getMessageClient(context).sendRequest(bestNode.id, capability, message)
                .addOnSuccessListener {
                    Log.d(TAG, "Response received for $capability")
                    Log.d(TAG, "${String(it)}")
                    def.complete(it)
                }.addOnCanceledListener {
                    val message = "Request $capability to ${bestNode.displayName} was cancelled"
                    Log.d(TAG, message)
                    def.cancel(CancellationException(message))
                }.addOnFailureListener {
                    val message =
                        "No response received from mobile for $capability : ${String(message)}"
                    Log.d(TAG, message)
                    def.cancel(CancellationException(message))
                }
        } ?: run {
            val message = "No nodes found with the capability $capability"
            Log.d(TAG, message)
            def.cancel(CancellationException(message))
        }
        return def.await()
    }

    private suspend fun findNearestNode(capability: String): Node? {
        // Find all nodes which support the time tracking message
        Log.d(TAG, "Searching for nodes with ${context.packageName} installed")
        val nodeClient = Wearable.getNodeClient(context)
        val connectedNodes = Tasks.await(nodeClient.connectedNodes)
        connectedNodes.forEach {
            Log.d(TAG, "Connected to ${it.displayName} (id: ${it.id}) (nearby: ${it.isNearby})")
        }

        val capabilityInfo: CapabilityInfo = Tasks.await(
            Wearable.getCapabilityClient(context)
                .getCapability(capability, CapabilityClient.FILTER_REACHABLE),
        )

        // Choose the best node (the closest one connected to the watch)
        val nodes = capabilityInfo.nodes
        Log.d(TAG, nodes.toString())
        return nodes.firstOrNull { it.isNearby } ?: nodes.firstOrNull()
    }
}