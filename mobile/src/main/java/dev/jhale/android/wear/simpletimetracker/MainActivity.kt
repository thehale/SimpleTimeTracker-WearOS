/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var buttonDebugCategories: Button
    lateinit var buttonDebugPrefs: Button

    @Inject
    lateinit var sttBroadcastTransmitter: STTBroadcastTransmitter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        this.buttonDebugCategories = findViewById(R.id.button_debug_read_categories)
        this.buttonDebugCategories.setOnClickListener {
            sttBroadcastTransmitter.transmitSTTBroadcast(
                this, Intent("com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_CATEGORIES"))
        }

        this.buttonDebugPrefs = findViewById(R.id.button_debug_prefs)
        this.buttonDebugPrefs.setOnClickListener {
            sttBroadcastTransmitter.transmitSTTBroadcast(
                this, Intent("com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_PREFS"))
        }
    }
}
