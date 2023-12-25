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

    lateinit var categoriesView: TextView
    lateinit var buttonDebugCategories: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val messageSTTBroadcastTransmitter = STTBroadcastTransmitter()

        setContentView(R.layout.activity_main)
        this.textView = findViewById(R.id.text_view_message)
        this.buttonDebug = findViewById(R.id.button_debug)
        this.buttonDebug.setOnClickListener {
            messageSTTBroadcastTransmitter.onReceive(applicationContext, Intent().also{ intent ->
                intent.putExtra("message", "Personal Learning|Hobbies")
            })
        }

        this.categoriesView = findViewById(R.id.text_view_categories)
        this.buttonDebugCategories = findViewById(R.id.button_debug_read_categories)
        this.buttonDebugCategories.setOnClickListener {
            Intent("com.razeeman.util.simpletimetracker.ACTION_READ_CATEGORIES").also {
                it.setPackage("com.razeeman.util.simpletimetracker.debug")
                Log.i(LOG_TAG, "Broadcasting Intent: $it")
                applicationContext.sendBroadcast(it)
            }
        }

        val newFilter = IntentFilter(Intent.ACTION_SEND)

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter)
    }

    inner class STTBroadcastTransmitter() : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            when (action) {
                //This branch not used... see STTBroadcastReceiver
                "com.razeeman.util.simpletimetracker.ACTION_SEND_CATEGORIES" -> {
                    intent.getStringExtra("categories").also {
                        categoriesView.text = it
                    }
                }
                else -> {
                    val message = intent?.extras?.getString("message") ?: "|"
                    textView.text = message

                    val messageParts = message.split("|")
                    val activity = messageParts[0]
                    val tag = messageParts[1]
                    if (activity.isNotEmpty()) {
                        Intent().also { intent ->
                            intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                            intent.setPackage("com.razeeman.util.simpletimetracker.debug")
                            intent.putExtra("extra_activity_name", activity)
                            if (tag.isNotEmpty()) {
                                intent.putExtra("extra_record_tag", tag)
                            }
                            Log.i(LOG_TAG, "Broadcasting Intent: $intent")
                            context?.sendBroadcast(intent)
                        }
                    }
                }
            }
        }
    }
}
