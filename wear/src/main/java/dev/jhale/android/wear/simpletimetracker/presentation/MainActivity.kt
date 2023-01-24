/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package dev.jhale.android.wear.simpletimetracker.presentation

import android.content.Intent
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
    name: String, tag: String,
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
        onClick = {
            Intent().also { intent ->
                intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                intent.setPackage("com.razeeman.util.simpletimetracker")
                intent.putExtra("extra_activity_name", name)
                intent.putExtra("extra_record_tag", tag)
                Log.i(LOG_TAG, "Broadcasting Intent: $intent")
                context.sendBroadcast(intent)
            }
        }
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
        onClick = {
            Intent().also { intent ->
                intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                intent.setPackage("com.razeeman.util.simpletimetracker")
                intent.putExtra("extra_activity_name", name)
                Log.i(LOG_TAG, "Broadcasting Intent: $intent")

                context.sendBroadcast(intent)
            }

        }
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
