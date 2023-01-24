package dev.jhale.android.wear.simpletimetracker

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun parse_startTimeTrackingActivity_message() {
        val message = "Activity|".split("|")
        val activity = message[0]
        val tag = message[1]

        assertEquals("Activity", activity)
        assertEquals("", tag)
    }
}