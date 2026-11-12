package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.DailyLog
import com.example.model.SessionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Affirmation 369", appName)
  }

  @Test
  fun `verify daily log 369 calculation and completion`() {
    val log = DailyLog(
      date = "2026-10-26",
      morningCount = 3,
      afternoonCount = 6,
      eveningCount = 9
    )
    assertEquals(18, log.totalRepetitions)
    assertTrue(log.isMorningComplete)
    assertTrue(log.isAfternoonComplete)
    assertTrue(log.isEveningComplete)
    assertTrue(log.isDayComplete)
    assertEquals(1.0f, log.progressFraction, 0.001f)
  }
}
