/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import dev.jhale.android.wear.simpletimetracker.R
import dev.jhale.android.wear.simpletimetracker.data.Messaging
import dev.jhale.android.wear.simpletimetracker.data.getTimeTrackingActivities
import dev.jhale.android.wear.simpletimetracker.presentation.theme.SimpleTimeTrackerForWearOSTheme


const val LOG_TAG = "dev.jhale.android.wear.simpletimetracker"


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rotaryEventDispatcher = RotaryEventDispatcher()

            CompositionLocalProvider(
                LocalRotaryEventDispatcher provides rotaryEventDispatcher,
            ) {
                RotaryEventHandlerSetup(rotaryEventDispatcher)
                WearApp()
            }
        }
    }
}

@Composable
fun WearApp() {
    SimpleTimeTrackerForWearOSTheme {
        val scrollState = rememberScalingLazyListState()
        RotaryEventState(scrollState)
        Scaffold(
            timeText = {
                TimeText(modifier = Modifier.scrollAway(scrollState))
            },
            vignette = {
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            },
            positionIndicator = {
                PositionIndicator(scalingLazyListState = scrollState)
            }
        ) {
            ActivityList(scrollState)
        }
    }
}

@Composable
fun ActivityList(scrollState: ScalingLazyListState) {
    val activities = getTimeTrackingActivities()
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .selectableGroup(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.Center,
        state = scrollState,
    ) {
        for (activity in activities) {
            if (activity.tags.isEmpty()) {
                item {
                    Activity(
                        name = activity.name,
                        tag = "",
                        color = activity.color,
                        icon = activity.iconId
                    )
                }
            } else {
                for (tag in activity.tags) {
                    item {
                        Activity(
                            name = activity.name,
                            tag = tag,
                            color = activity.color,
                            icon = activity.iconId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Activity(
    name: String,
    tag: String,
    color: Color = Color(96, 125, 139, 255),
    icon: Int = R.drawable.baseline_question_mark_24
) {
    if (tag.isNotEmpty()) {
        ActivityWithTag(name = name, tag = tag, color = color, icon = icon)
    } else {
        ActivityWithoutTag(name = name, color = color, icon = icon)
    }
}

@Composable
fun ActivityWithTag(
    name: String,
    tag: String,
    color: Color = Color(96, 125, 139, 255),
    icon: Int = R.drawable.baseline_question_mark_24
) {
    val context = LocalContext.current
    Chip(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
        icon = {
            ActivityIcon(iconId = icon)
        },
        label = {
            Text(
                text = tag,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = color
        ),
        onClick = { startTimeTracking(context, name, tag) }
    )
}


@Composable
fun ActivityWithoutTag(
    name: String,
    color: Color = Color(96, 125, 139, 255),
    icon: Int = R.drawable.baseline_question_mark_24
) {
    val context = LocalContext.current
    Chip(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
        icon = {
            ActivityIcon(iconId = icon)
        },
        label = {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = color
        ),
        onClick = { startTimeTracking(context, name, "") }
    )
}

@Composable
fun ActivityIcon(iconId: Int) {
    Icon(
        painter = painterResource(id = iconId),
        contentDescription = "activity icon",
        modifier = Modifier
            .size(ChipDefaults.IconSize)
            .wrapContentSize(align = Alignment.Center),
    )
}

fun startTimeTracking(context: Context, activity: String, tag: String) {
    Messaging().startTimeTracking(context, activity, tag)
}
