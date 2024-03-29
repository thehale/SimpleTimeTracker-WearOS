/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.example.util.simpletimetracker.wear

import com.example.util.simpletimetracker.domain.StartActivityMediator
import com.example.util.simpletimetracker.wear_api.WearActivity
import com.example.util.simpletimetracker.wear_api.MockWearCommunicationAPI
import com.example.util.simpletimetracker.wear_api.WearSettings
import com.example.util.simpletimetracker.wear_api.WearTag
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

// TODO use mockito
class StartActivityMediatorTest {
    private val api = MockWearCommunicationAPI()
    private val startCallback = MockMediatorCallback()
    private val requestTagCallback = MockMediatorCallback()
    private val mediator = StartActivityMediator(
        api = api,
        onRequestStartActivity = startCallback,
        onRequestTagSelection = requestTagCallback,
    )

    private val sampleActivity = WearActivity(id = 1, name = "Sleep", icon = "🛏️", color = 0xFF123456)
    private val sampleGeneralTag =
        WearTag(id = 13, name = "Sleep", isGeneral = true, color = 0xFF654321)
    private val sampleNonGeneralTag =
        WearTag(id = 14, name = "Work", isGeneral = false, color = 0xFF654321)
    private val settings = WearSettings(
        allowMultitasking = false,
        showRecordTagSelection = false,
        recordTagSelectionCloseAfterOne = false,
        recordTagSelectionEvenForGeneralTags = false,
    )

    private val sampleSettings = settings.copy(showRecordTagSelection = true)

    @Before
    fun setup() {
        api.mockReset()
        startCallback.reset()
        requestTagCallback.reset()
    }

    @Test
    fun `tag selection disabled`() = runTest {
        api.mock_querySettings(settings.copy(showRecordTagSelection = false))
        mediator.requestStart(sampleActivity)
        `assert only start callback invoked`()
    }

    @Test
    fun `tag selection enabled and activity has no tags`() = runTest {
        api.mock_querySettings(settings.copy(showRecordTagSelection = true))
        api.mock_queryTagsForActivity(mapOf())
        mediator.requestStart(sampleActivity)
        `assert only start callback invoked`()
    }

    @Test
    fun `tag selection enabled, but not for generals alone, and activity has only general tags`() =
        runTest {
            api.mock_querySettings(
                settings.copy(
                    showRecordTagSelection = true,
                    recordTagSelectionEvenForGeneralTags = false,
                ),
            )
            api.mock_queryTagsForActivity(mapOf(sampleActivity.id to listOf(sampleGeneralTag)))
            mediator.requestStart(sampleActivity)
            `assert only start callback invoked`()
        }

    private fun `assert only start callback invoked`() {
        startCallback.assertCalledWith(sampleActivity)
        startCallback.assertCallsMade(1)
        requestTagCallback.assertCallsMade(0)
    }

    @Test
    fun `activity has non-general tags`() = runTest {
        api.mock_querySettings(sampleSettings)
        api.mock_queryTagsForActivity(mapOf(sampleActivity.id to listOf(sampleNonGeneralTag)))
        mediator.requestStart(sampleActivity)
        `assert only tag callback invoked`()
    }

    @Test
    fun `activity has only general tags and tag selection enabled for only generals`() = runTest {
        api.mock_querySettings(sampleSettings.copy(recordTagSelectionEvenForGeneralTags = true))
        api.mock_queryTagsForActivity(mapOf(sampleActivity.id to listOf(sampleGeneralTag)))
        mediator.requestStart(sampleActivity)
        `assert only tag callback invoked`()
    }

    private fun `assert only tag callback invoked`() {
        requestTagCallback.assertCalledWith(sampleActivity)
        requestTagCallback.assertCallsMade(1)
        startCallback.assertCallsMade(0)
    }

    class MockMediatorCallback : (WearActivity) -> Unit {
        private var calledWith: WearActivity? = null
        private var callCount: Int = 0
        override fun invoke(activity: WearActivity) {
            calledWith = activity
            callCount++
        }

        fun assertCalledWith(activity: WearActivity) {
            assertEquals(activity, calledWith)
        }

        fun assertCallsMade(count: Int) {
            assertEquals(count, callCount)
        }

        fun reset() {
            calledWith = null
            callCount = 0
        }
    }
}