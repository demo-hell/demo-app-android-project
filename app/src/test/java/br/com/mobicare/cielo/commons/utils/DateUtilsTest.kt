package br.com.mobicare.cielo.commons.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DateUtilsTest {
    @Test
    fun `isToday returns true when date is today`() {
        val today = LocalDate.now()
        assertTrue(today.isToday())
    }

    @Test
    fun `isToday returns false when date is not today`() {
        val notToday = LocalDate.now().minusDays(1)
        assertFalse(notToday.isToday())
    }

    @Test
    fun `getNumberOfDaysBetweenDates returns correct days when endDate is after startDate`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 22)
        val result = getNumberOfDaysBetweenDates(startDate, endDate)
        assertEquals(21L, result)
    }

    @Test
    fun `getNumberOfDaysBetweenDates returns zero when startDate and endDate are the same`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 1)
        val result = getNumberOfDaysBetweenDates(startDate, endDate)
        assertEquals(0L, result)
    }

    @Test
    fun `getNumberOfDaysBetweenDates returns null when endDate is before startDate`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2023, 12, 31)
        val result = getNumberOfDaysBetweenDates(startDate, endDate)
        assertNull(result)
    }

    @Test
    fun `getNumberOfWeeksBetweenDates returns correct weeks when endDate is after startDate`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 22)
        val result = getNumberOfWeeksBetweenDates(startDate, endDate)
        assertEquals(3L, result)
    }

    @Test
    fun `getNumberOfWeeksBetweenDates returns zero when startDate and endDate are the same`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 1)
        val result = getNumberOfWeeksBetweenDates(startDate, endDate)
        assertEquals(0L, result)
    }

    @Test
    fun `getNumberOfWeeksBetweenDates returns null when endDate is before startDate`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2023, 12, 31)
        val result = getNumberOfWeeksBetweenDates(startDate, endDate)
        assertNull(result)
    }

    @Test
    fun `getNumberOfMonthsBetweenDates returns correct months when endDate is after startDate`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 4, 1)
        val result = getNumberOfMonthsBetweenDates(startDate, endDate)
        assertEquals(3L, result)
    }

    @Test
    fun `getNumberOfMonthsBetweenDates returns zero when startDate and endDate are the same`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2024, 1, 1)
        val result = getNumberOfMonthsBetweenDates(startDate, endDate)
        assertEquals(0L, result)
    }

    @Test
    fun `getNumberOfMonthsBetweenDates returns null when endDate is before startDate`() {
        val startDate = LocalDate.of(2024, 1, 1)
        val endDate = LocalDate.of(2023, 12, 31)
        val result = getNumberOfMonthsBetweenDates(startDate, endDate)
        assertNull(result)
    }
}
