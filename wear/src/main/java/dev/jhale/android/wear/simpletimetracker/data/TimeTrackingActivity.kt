/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package dev.jhale.android.wear.simpletimetracker.data

import androidx.compose.ui.graphics.Color
import dev.jhale.android.wear.simpletimetracker.R

class TimeTrackingActivity(
    val name: String,
    val tags: List<String> = listOf(),
    val color: Color = Color(96, 125, 139, 255),
    val iconId: Int = R.drawable.baseline_question_mark_24,
    val timeStarted: Long = -1,
)

fun getTimeTrackingActivities(): List<TimeTrackingActivity> {
    return listOf<TimeTrackingActivity> (
        TimeTrackingActivity("Work", color = Color(255, 87, 34, 255), iconId = R.drawable.baseline_computer_24),
        TimeTrackingActivity("Micro Goal", color = Color(205, 220, 57, 255), iconId = R.drawable.baseline_computer_24),
    )
}
