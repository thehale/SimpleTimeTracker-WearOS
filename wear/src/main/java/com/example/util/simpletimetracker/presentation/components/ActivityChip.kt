/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.example.util.simpletimetracker.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.SplitToggleChip
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.SwitchDefaults
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChipDefaults
import com.example.util.simpletimetracker.presentation.theme.hexCodeToColor
import com.example.util.simpletimetracker.wearrpc.Activity
import com.example.util.simpletimetracker.wearrpc.Tag
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ActivityChip(
    activity: Activity,
    startedAt: Long? = null,
    tags: Array<Tag> = arrayOf(),
    onSelectActivity: () -> Unit = {},
    onSelectActivitySkipTagSelection: () -> Unit = {},
    onDeselectActivity: () -> Unit = {},
) {
    val briefIcon = if (activity.icon.startsWith("ic_")) {
        "?"
    } else {
        activity.icon.substring(0, activity.icon.length.coerceAtMost(2))
    }
    val tagsList = if (tags.isNotEmpty()) {
        tags.joinToString(", ") { it.name }
    } else {
        ""
    }
    val tagString = if (tagsList.isNotEmpty()) {
        " ($tagsList)"
    } else {
        ""
    }
    val color = hexCodeToColor(activity.color)
    var modifier = Modifier
        .fillMaxWidth(0.9f)
        .padding(top = 10.dp)
    var switchChecked by remember { mutableStateOf(startedAt != null) }
    SplitToggleChip(
        modifier = modifier,
        label = {
            Text(
                text = "$briefIcon : ${activity.name}" + tagString,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        secondaryLabel = {
            if (startedAt != null) {
                Text("Since ${recentTimestampToString(startedAt)}")
            } else {
                null
            }
        },
        colors = ToggleChipDefaults.splitToggleChipColors(
            backgroundColor = color,
            splitBackgroundOverlayColor = if (switchChecked) {
                Color.White.copy(alpha = .1F)
            } else {
                Color.Black.copy(alpha = .3F)
            },
        ),
        onCheckedChange = {
            if (it) {
                onSelectActivitySkipTagSelection()
            } else {
                onDeselectActivity()
            }
            switchChecked = it
        },
        checked = switchChecked,
        onClick = {
            onSelectActivity()
        },
        toggleControl = {
            Switch(
                checked = switchChecked,
                enabled = true,
                modifier = Modifier.semantics {
                    this.contentDescription = if (switchChecked) "On" else "Off"
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color.White.copy(alpha = 0.5F),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.White.copy(alpha = 0.5F),
                ),
            )
        },
    )
}

fun recentTimestampToString(epochMillis: Long): String {
    // Someday, it would be nice for this to show nicer time strings
    // e.g. "a few minutes ago", "yesterday", etc.
    val time = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(epochMillis),
        ZoneId.systemDefault(),
    )
    if (time > LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).minusDays(1)) {
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME)
    } else {
        return time.format(DateTimeFormatter.ISO_DATE_TIME).replace("T", " ")
    }
}

@Preview()
@Composable
fun SampleCooking() {
    ActivityChip(Activity(123, "Cooking", "🎉", "#123456"))
}

@Preview()
@Composable
fun SampleSleep() {
    ActivityChip(Activity(456, "Sleeping", "🛏️", "#ABCDEF"))
}

@Preview()
@Composable
fun White() {
    // TODO handle the look of light colored chips
    // Note: A white color is only possible when using the RGB color picker.
    // The default color options in the phone app are mostly darker shades.
    ActivityChip(Activity(456, "Sleeping", "🛏️", "#FFFFFF"))
}

@Preview()
@Composable
fun CurrentlyRunning() {
    ActivityChip(Activity(456, "Sleeping", "🛏️", "#ABCDEF"), startedAt = 1706751601000L)
}

@Preview()
@Composable
fun CurrentlyRunningWithTags() {
    ActivityChip(
        Activity(456, "Sleeping", "🛏️", "#ABCDEF"), startedAt = 1706751601000L,
        tags = arrayOf(
            Tag(id = 2, name = "Work"),
            Tag(id = 4, name = "Hotel"),
        ),
    )
}