/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.example.util.simpletimetracker.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.OutlinedButton
import com.example.util.simpletimetracker.R
import com.example.util.simpletimetracker.presentation.remember.rememberAnimationRotation

@Composable
fun RefreshButton(onClick: () -> Unit, contentDescription: String? = null) {
    val context = LocalContext.current
    var pressCount by remember { mutableIntStateOf(0) }
    val targetRotation = rememberAnimationRotation(key = pressCount)
    OutlinedButton(
        onClick = {
            pressCount++
            onClick()
        },
        content = {
            Icon(
                Icons.Rounded.Refresh,
                contentDescription = contentDescription ?: context.getString(
                    R.string.refresh_button_default_content_description,
                ),
            )
        },
        modifier = Modifier
            .rotate(targetRotation)
            .padding(all = 8.dp),
    )
}

@Preview
@Composable
private fun Preview() {
    RefreshButton(
        onClick = { /* Log.i("Preview", "Refresh Button clicked!") */ },
    )
}