package com.etiennelawlor.eggday

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EggDayTest {

    @Test
    fun formatsWithThreeDigits() {
        assertEquals("001", formatDayOfYear(LocalDate.of(2026, 1, 1).dayOfYear))
        assertEquals("042", formatDayOfYear(42))
        assertEquals("293", formatDayOfYear(LocalDate.of(2026, 10, 20).dayOfYear))
        assertEquals("365", formatDayOfYear(LocalDate.of(2026, 12, 31).dayOfYear))
        assertEquals("366", formatDayOfYear(LocalDate.of(2028, 12, 31).dayOfYear))
    }

    @Test
    fun packDateEarlierThisYear() {
        val today = LocalDate.of(2026, 10, 20) // day 293
        assertEquals(3, daysSincePack(290, today))
        assertEquals(LocalDate.of(2026, 10, 17), packDate(290, today))
    }

    @Test
    fun packDateToday() {
        val today = LocalDate.of(2026, 10, 20)
        assertEquals(0, daysSincePack(293, today))
    }

    @Test
    fun packDateLaterThanTodayIsFromLastYear() {
        val today = LocalDate.of(2026, 1, 5)
        assertEquals(LocalDate.of(2025, 12, 16), packDate(350, today))
        assertEquals(20, daysSincePack(350, today))
    }

    @Test
    fun leapDay() {
        assertEquals(LocalDate.of(2028, 2, 29), packDate(60, LocalDate.of(2028, 3, 1)))
        assertEquals(1, daysSincePack(60, LocalDate.of(2028, 3, 1)))
    }

    @Test
    fun day366OnlyInLeapYears() {
        // Last year (2024) was a leap year, so 366 is Dec 31, 2024.
        assertEquals(LocalDate.of(2024, 12, 31), packDate(366, LocalDate.of(2025, 1, 5)))
        assertEquals(5, daysSincePack(366, LocalDate.of(2025, 1, 5)))
        // Neither 2025 nor 2026 has a day 366.
        assertNull(packDate(366, LocalDate.of(2026, 10, 20)))
        // Dec 31 of a leap year is itself day 366.
        assertEquals(0, daysSincePack(366, LocalDate.of(2028, 12, 31)))
    }

    @Test
    fun rejectsOutOfRange() {
        val today = LocalDate.of(2026, 10, 20)
        assertNull(daysSincePack(0, today))
        assertNull(daysSincePack(367, today))
        assertNull(daysSincePack(-5, today))
    }
}
