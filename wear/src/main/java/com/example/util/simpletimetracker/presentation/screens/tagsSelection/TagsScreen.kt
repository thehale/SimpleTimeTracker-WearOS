/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.example.util.simpletimetracker.presentation.screens.tagsSelection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.util.simpletimetracker.presentation.components.TagList
import com.example.util.simpletimetracker.presentation.screens.tagsSelection.TagsViewModel.Effect
import com.example.util.simpletimetracker.utils.collectEffects

@Composable
fun TagsScreen(
    activityId: Long,
    onComplete: () -> Unit,
) {
    val viewModel = hiltViewModel<TagsViewModel>()
    viewModel.init(activityId)
    val state by viewModel.state.collectAsState()

    viewModel.effects.collectEffects(key = viewModel) {
        when (it) {
            is Effect.OnComplete -> onComplete()
        }
    }

    TagList(
        state = state,
        onButtonClick = viewModel::onButtonClick,
        onToggleClick = viewModel::onToggleClick
    )
}
