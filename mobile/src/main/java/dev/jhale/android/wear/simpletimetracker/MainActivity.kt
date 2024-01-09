/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var buttonDebug: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val messageReceiver = Receiver()

        setContentView(R.layout.activity_main)
        this.textView = findViewById(R.id.text_view_message)
        this.buttonDebug = findViewById(R.id.button_debug)
        this.buttonDebug.setOnClickListener {
            messageReceiver.onReceive(applicationContext, Intent().also{intent ->
                intent.putExtra("message", "Personal Learning|Hobbies")
            })
        }

        val newFilter = IntentFilter(Intent.ACTION_SEND)

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter)
    }

    inner class Receiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.extras?.getString("message") ?: "|"
            textView.text = message

            val messageParts = message.split("|")
            val activity = messageParts[0]
            val tag = messageParts[1]
            if (activity.isNotEmpty()) {
                Intent().also { intent ->
                    intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                    intent.setPackage("com.razeeman.util.simpletimetracker")
                    intent.putExtra("extra_activity_name", activity)
                    if (tag.isNotEmpty()) {
                        intent.putExtra("extra_record_tag", tag)
                    }
                    Log.i(LOG_TAG, "Broadcasting Intent: $intent")
                    context?.sendBroadcast(intent)
                }
            } else {
                Log.e(LOG_TAG, "Activity name is empty!")
            }
        }
    }
}