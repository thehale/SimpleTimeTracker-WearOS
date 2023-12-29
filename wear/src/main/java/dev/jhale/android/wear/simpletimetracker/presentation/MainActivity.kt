/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.ToggleChip
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.material.scrollAway
import com.google.android.gms.wearable.Wearable
import com.google.android.horologist.compose.focus.rememberActiveFocusRequester
import com.google.android.horologist.compose.navscaffold.ExperimentalHorologistComposeLayoutApi
import com.google.android.horologist.compose.rotaryinput.rememberRotaryHapticFeedback
import com.google.android.horologist.compose.rotaryinput.rotaryWithScroll
import dagger.hilt.android.AndroidEntryPoint
import dev.jhale.android.wear.simpletimetracker.MessagingFacade
import dev.jhale.android.wear.simpletimetracker.R
import dev.jhale.android.wear.simpletimetracker.data.TimeTrackingActivity
import dev.jhale.android.wear.simpletimetracker.presentation.theme.SimpleTimeTrackerForWearOSTheme
import javax.inject.Inject


const val LOG_TAG = "dev.jhale.android.wear.simpletimetracker"

@AndroidEntryPoint
class MainActivity @Inject constructor() : ComponentActivity() {
    @Inject
    lateinit var messagingFacade: MessagingFacade;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Wearable.getMessageClient(this).addListener(messagingFacade)

        messagingFacade.requestCategoriesList(applicationContext) { categories ->
            this.renderActivitiesList(
                categories
            )
        }

        setContent {
            WearApp(false, this::toggleChipDataInteraction)
        }
    }

    fun renderActivitiesList(activityList: List<TimeTrackingActivity>) {
        setContent {
            WearApp(true, this::toggleChipDataInteraction, activityList)
        }
    }

    private fun toggleChipDataInteraction(checked: Boolean, context: Context, activity: String, tag: String): Unit {
        if (checked) {
            messagingFacade.startTimeTracking(context, activity, tag)
        } else {
            messagingFacade.stopTimeTracking(context, activity, tag)
        }
    }
}

@Composable
fun WearApp(
    loadedActivities: Boolean,
    dataInteraction: (checked: Boolean, context: Context, activity: String, tag: String) -> Unit,
    activityList: List<TimeTrackingActivity> = listOf()
) {
    SimpleTimeTrackerForWearOSTheme {
        val scrollState = rememberScalingLazyListState()
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
            if (loadedActivities) ActivityList(activityList, scrollState, dataInteraction) else CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalHorologistComposeLayoutApi::class)
@Composable
fun ActivityList(
    activities: List<TimeTrackingActivity>,
    scrollState: ScalingLazyListState = rememberScalingLazyListState(),
    dataInteraction: (checked: Boolean, context: Context, activity: String, tag: String) -> Unit
) {
    val focusRequester = rememberActiveFocusRequester()
    val rotaryHapticFeedback = rememberRotaryHapticFeedback()
    ScalingLazyColumn(
        modifier = Modifier
            .rotaryWithScroll(focusRequester, scrollState, rotaryHapticFeedback)
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
                        icon = activity.iconId,
                        timeStarted = activity.timeStarted,
                        dataInteraction
                    )
                }
            } else {
                for (tag in activity.tags) {
                    item {
                        Activity(
                            name = activity.name,
                            tag = tag,
                            color = activity.color,
                            icon = activity.iconId,
                            timeStarted = activity.timeStarted,
                            dataInteraction
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
    icon: Int = R.drawable.baseline_question_mark_24,
    timeStarted: Long,
    dataInteraction: (checked: Boolean, context: Context, activity: String, tag: String) -> Unit
) {
    if (tag.isNotEmpty()) {
        ActivityWithTag(name = name, tag = tag, color = color, icon = icon, timeStarted = timeStarted,
                dataInteraction)
    } else {
        ActivityWithoutTag(name = name, color = color, icon = icon, timeStarted = timeStarted, dataInteraction)
    }
}

@Composable
fun ActivityWithTag(
    name: String,
    tag: String,
    color: Color = Color(96, 125, 139, 255),
    icon: Int = R.drawable.baseline_question_mark_24,
    timeStarted: Long,
    dataInteraction: (checked: Boolean, context: Context, activity: String, tag: String) -> Unit
) {
    var switchChecked by remember { mutableStateOf(timeStarted > -1) }
    val context = LocalContext.current
    ToggleChip(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
        appIcon = {
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
        colors = ToggleChipDefaults.toggleChipColors(
            checkedStartBackgroundColor = color,
            checkedEndBackgroundColor = color,
            uncheckedToggleControlColor = ToggleChipDefaults.SwitchUncheckedIconColor
        ),
        onCheckedChange = { switchChecked = it; dataInteraction(switchChecked, context, name, "") },
        checked = switchChecked,
        toggleControl = {
            Switch(
                checked = switchChecked,
                enabled = true,
                modifier = Modifier.semantics {
                    this.contentDescription =
                        if (switchChecked) "On" else "Off"
                }
            )
        }
    )
}


@Composable
fun ActivityWithoutTag(
    name: String,
    color: Color = Color(96, 125, 139, 255),
    icon: Int = R.drawable.baseline_question_mark_24,
    timeStarted: Long,
    dataInteraction: (checked: Boolean, context: Context, activity: String, tag: String) -> Unit
) {
    var switchChecked by remember { mutableStateOf(timeStarted > -1) }
    val context = LocalContext.current
    ToggleChip(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
        appIcon = {
            ActivityIcon(iconId = icon)
        },
        label = {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = ToggleChipDefaults.toggleChipColors(
            checkedStartBackgroundColor = color,
            checkedEndBackgroundColor = color,
            uncheckedToggleControlColor = ToggleChipDefaults.SwitchUncheckedIconColor
        ),
        onCheckedChange = { switchChecked = it; dataInteraction(switchChecked, context, name, "") },
        checked = switchChecked,
        toggleControl = {
            Switch(
                checked = switchChecked,
                enabled = true,
                modifier = Modifier.semantics {
                    this.contentDescription =
                        if (switchChecked) "On" else "Off"
                }
            )
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
