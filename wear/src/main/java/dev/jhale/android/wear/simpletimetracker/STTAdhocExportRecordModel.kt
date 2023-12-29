package dev.jhale.android.wear.simpletimetracker

import androidx.annotation.ColorInt

data class STTAdhocExportRecordModel constructor(
    val id: Long = 0,
    val name: String,
    val icon: String,
    @ColorInt val colorInt: Int,
    val hidden: Boolean = false,
    var timeStarted: Long = -1,
    var comment: String = "",
)