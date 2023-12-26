/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var buttonDebug: Button

    lateinit var categoriesView: TextView
    lateinit var buttonDebugCategories: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        this.textView = findViewById(R.id.text_view_message)



        this.buttonDebug = findViewById(R.id.button_debug)

        this.buttonDebugCategories = findViewById(R.id.button_debug_read_categories)
        this.buttonDebugCategories.setOnClickListener {
            Intent("com.razeeman.util.simpletimetracker.ACTION_READ_CATEGORIES").also {
                it.setPackage("com.razeeman.util.simpletimetracker.debug")
                Log.i(LOG_TAG, "Broadcasting Intent: $it")
                applicationContext.sendBroadcast(it)
            }
        }

        this.buttonDebugCategories = findViewById(R.id.button_debug_read_running_records)
        this.buttonDebugCategories.setOnClickListener {
            Intent("com.razeeman.util.simpletimetracker.ACTION_READ_RUNNING_RECORDS").also {
                it.setPackage("com.razeeman.util.simpletimetracker.debug")
                Log.i(LOG_TAG, "Broadcasting Intent: $it")
                applicationContext.sendBroadcast(it)
            }
        }

        this.buttonDebugCategories = findViewById(R.id.button_debug_prefs)
        this.buttonDebugCategories.setOnClickListener {
            Intent("com.razeeman.util.simpletimetracker.ACTION_READ_PREFS").also {
                it.setPackage("com.razeeman.util.simpletimetracker.debug")
                Log.i(LOG_TAG, "Broadcasting Intent: $it")
                applicationContext.sendBroadcast(it)
            }
        }
    }
}
