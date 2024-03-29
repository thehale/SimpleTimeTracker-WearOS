/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.example.util.simpletimetracker.presentation.screens.activities

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.util.simpletimetracker.presentation.components.ActivitiesList
import com.example.util.simpletimetracker.presentation.screens.activities.ActivitiesViewModel.Effect
import com.example.util.simpletimetracker.utils.collectEffects

@Composable
fun ActivitiesScreen(
    onRequestTagSelection: (activityId: Long) -> Unit,
) {
    val viewModel = hiltViewModel<ActivitiesViewModel>()
    viewModel.init()
    val state by viewModel.state.collectAsState()

    viewModel.effects.collectEffects(key = viewModel) {
        when (it) {
            is Effect.OnRequestTagSelection -> onRequestTagSelection(it.activityId)
        }
    }

    ActivitiesList(
        state = state,
        onSelectActivity = viewModel::onSelectActivity,
        onEnableActivity = viewModel::startActivityWithoutTags,
        onDisableActivity = viewModel::stopActivity,
        onRefresh = viewModel::loadData,
    )
}
