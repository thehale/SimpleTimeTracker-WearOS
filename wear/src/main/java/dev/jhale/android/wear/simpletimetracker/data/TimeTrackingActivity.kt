package dev.jhale.android.wear.simpletimetracker.data

import androidx.compose.ui.graphics.Color
import dev.jhale.android.wear.simpletimetracker.R

class TimeTrackingActivity(
    val name: String,
    val tags: List<String>,
    val color: Color,
    val iconId: Int,
)

fun getTimeTrackingActivities(): List<TimeTrackingActivity> {
    return listOf<TimeTrackingActivity> (
        activity("TODOs"),
        activity("Travel"),
        activity("Planning"),
        activity("Learning", listOf<String>("Classes", "Hobbies", "Homework", "Music", "Thesis")),
        activity("Professional", listOf<String>("Anva", "Networking", "Trainings", "Volunteer")),
        activity("Activities", listOf<String>("Family", "Friends", "Personal")),
        activity("Basic Needs", listOf<String>("Exercise", "Hygiene", "Meals", "Sleep")),
        activity("Church", listOf<String>("Service", "Study", "Worship")),
        activity("Dates"),
        activity("Other"),
    )
}

fun activity(name: String, tags: List<String> = listOf()): TimeTrackingActivity {
    return TimeTrackingActivity(
        name = name,
        tags = tags,
        color = colorForActivity(name),
        iconId = iconForActivity(name),
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
