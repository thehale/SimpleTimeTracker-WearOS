/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package dev.jhale.android.wear.simpletimetracker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import dev.jhale.android.wear.simpletimetracker.R
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
    /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
     * version of LazyColumn for wear devices with some added features. For more information,
     * see d.android.com/wear/compose.
     */
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .selectableGroup(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.Center,
        state = scrollState,
    ) {
        item { Activity(activityName = "TODOs", tagName = "") }
        item { Activity(activityName = "Travel", tagName = "") }
        item { Activity(activityName = "Planning", tagName = "") }
        item { Activity(activityName = "Learning", tagName = "Classes") }
        item { Activity(activityName = "Learning", tagName = "Hobbies") }
        item { Activity(activityName = "Learning", tagName = "Homework") }
        item { Activity(activityName = "Learning", tagName = "Music") }
        item { Activity(activityName = "Learning", tagName = "Thesis") }
        item { Activity(activityName = "Professional", tagName = "Anva") }
        item { Activity(activityName = "Professional", tagName = "Networking") }
        item { Activity(activityName = "Professional", tagName = "Trainings") }
        item { Activity(activityName = "Professional", tagName = "Volunteer") }
        item { Activity(activityName = "Activities", tagName = "Personal") }
        item { Activity(activityName = "Activities", tagName = "Family") }
        item { Activity(activityName = "Activities", tagName = "Friends") }
        item { Activity(activityName = "Basic Needs", tagName = "Exercise") }
        item { Activity(activityName = "Basic Needs", tagName = "Hygiene") }
        item { Activity(activityName = "Basic Needs", tagName = "Meals") }
        item { Activity(activityName = "Basic Needs", tagName = "Sleep") }
        item { Activity(activityName = "Church", tagName = "Service") }
        item { Activity(activityName = "Church", tagName = "Study") }
        item { Activity(activityName = "Church", tagName = "Worship") }
        item { Activity(activityName = "Dates", tagName = "") }
        item { Activity(activityName = "Other", tagName = "") }
    }
}

@Composable
fun Activity(activityName: String, tagName: String) {
    if (tagName.isNotEmpty()) {
        ActivityWithTag(activityName = activityName, tagName = tagName)
    } else {
        ActivityWithoutTag(activityName = activityName)
    }
}

@Composable
fun ActivityWithTag(activityName: String, tagName: String) {
    val context = LocalContext.current
    Chip(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
        icon = {
            ActivityIcon(activityName = activityName)
        },
        label = {
            Text(
                text = tagName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        secondaryLabel = {
            Text(
                text = activityName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = colorForActivity(activityName)
        ),
        onClick = {
            Intent().also {intent ->
                intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                intent.setPackage("com.razeeman.util.simpletimetracker")
                intent.putExtra("extra_activity_name", activityName)
                intent.putExtra("extra_record_tag", tagName)
                Log.i(LOG_TAG, "Broadcasting Intent: $intent")
                context.sendBroadcast(intent)
            }
        }
    )
}


@Composable
fun ActivityWithoutTag(activityName: String) {
    val context = LocalContext.current
    Chip(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
        icon = {
            ActivityIcon(activityName = activityName)
        },
        label = {
            Text(
                text = activityName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = colorForActivity(activityName)
        ),
        onClick = {
            Intent().also {intent ->
                intent.action = "com.razeeman.util.simpletimetracker.ACTION_START_ACTIVITY"
                intent.setPackage("com.razeeman.util.simpletimetracker")
                intent.putExtra("extra_activity_name", activityName)
                Log.i(LOG_TAG, "Broadcasting Intent: $intent")

                context.sendBroadcast(intent)
            }

        }
    )
}

@Composable
fun ActivityIcon(activityName: String) {
    Icon(
        painter = painterResource(id = iconForActivity(activityName)),
        contentDescription = "airplane",
        modifier = Modifier
            .size(ChipDefaults.IconSize)
            .wrapContentSize(align = Alignment.Center),
    )
}

fun iconForActivity(activityName: String): Int {
    val icons = mapOf<String, Int>(
        "Activities" to R.drawable.baseline_people_24,
        "Basic Needs" to R.drawable.baseline_bed_24,
        "Church" to R.drawable.baseline_church_24,
        "Dates" to R.drawable.baseline_heart_broken_24,
        "Other" to R.drawable.baseline_horizontal_rule_24,
        "Learning" to R.drawable.baseline_computer_24,
        "Planning" to R.drawable.baseline_timer_24,
        "Professional" to R.drawable.baseline_home_work_24,
        "TODOs" to R.drawable.baseline_check_box_24,
        "Travel" to R.drawable.baseline_directions_car_24,
    )
    return icons.getOrDefault(activityName, R.drawable.baseline_question_mark_24)
}

fun colorForActivity(activityName: String): Color {
    val icons = mapOf<String, Color>(
        "Activities" to Color(3, 169, 244, 255),
        "Basic Needs" to Color(205, 220, 57, 255),
        "Church" to Color(76, 175, 80, 255),
        "Dates" to Color(156, 39, 176, 255),
        "Other" to Color(96, 125, 139, 255),
        "Learning" to Color(255, 193, 7, 255),
        "Planning" to Color(255, 87, 34, 255),
        "Professional" to Color(233, 30, 99, 255),
        "TODOs" to Color(245, 54, 57, 255),
        "Travel" to Color(120, 82, 72, 255),
    )
    return icons.getOrDefault(activityName, Color(96, 125, 139, 255))
}
