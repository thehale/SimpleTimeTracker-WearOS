package com.example.util.simpletimetracker.domain.interactor

import com.example.util.simpletimetracker.domain.model.RecordTypeGoal

interface NotificationGoalTimeInteractor {

    suspend fun checkAndReschedule(typeIds: List<Long> = emptyList())

    fun cancel(idData: RecordTypeGoal.IdData)

    suspend fun show(
        idData: RecordTypeGoal.IdData,
        goalRange: RecordTypeGoal.Range,
    )
}