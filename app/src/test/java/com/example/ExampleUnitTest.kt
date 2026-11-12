package com.example

import com.example.model.DailyLog
import com.example.model.SessionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun `verify 369 session repetition counts`() {
    assertEquals(3, SessionType.MORNING.count)
    assertEquals(6, SessionType.AFTERNOON.count)
    assertEquals(9, SessionType.EVENING.count)
    val totalExpected = SessionType.MORNING.count + SessionType.AFTERNOON.count + SessionType.EVENING.count
    assertEquals(18, totalExpected)
  }

  @Test
  fun `verify daily log progress fraction`() {
    val partialLog = DailyLog(
      date = "2026-10-26",
      morningCount = 3,
      afternoonCount = 3,
      eveningCount = 0
    )
    assertEquals(6, partialLog.totalRepetitions)
    assertTrue(partialLog.isMorningComplete)
    assertFalse(partialLog.isAfternoonComplete)
    assertFalse(partialLog.isEveningComplete)
    assertFalse(partialLog.isDayComplete)
    assertEquals(6f / 18f, partialLog.progressFraction, 0.001f)
  }
}
