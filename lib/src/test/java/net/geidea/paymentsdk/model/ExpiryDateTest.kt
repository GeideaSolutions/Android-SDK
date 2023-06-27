package net.geidea.paymentsdk.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class ExpiryDateTest {

    @Test
    fun isValid_withValidMonthAndYear_returnsTrue() {
        assertTrue(ExpiryDate(5, 55).isValid)
        assertTrue(ExpiryDate(12, 99).isValid)
    }

    // Invalid month

    @Test
    fun isValid_withNegativeMonth_returnsFalse() {
        assertFalse(ExpiryDate(-1, 44).isValid)
    }

    @Test
    fun isValid_withZeroMonth_returnsFalse() {
        assertFalse(ExpiryDate(0, 44).isValid)
    }

    @Test
    fun isValid_withMonth13_returnsFalse() {
        assertFalse(ExpiryDate(13, 44).isValid)
    }

    // Invalid year

    @Test
    fun isValid_withNegativeYear_returnsFalse() {
        assertFalse(ExpiryDate(5, -1).isValid)
    }

    @Test
    fun isValid_withZeroYear_returnsFalse() {
        assertFalse(ExpiryDate(5, 0).isValid)
    }

    @Test
    fun isValid_withYear100_returnsFalse() {
        assertFalse(ExpiryDate(5, 100).isValid)
    }

    @Test
    fun isValid_with4digitYear_returnsFalse() {
        assertFalse(ExpiryDate(5, 2020).isValid)
    }

    // Expired

    @Test
    fun isValid_withValidButInPast_returnsFalse() {
        assertFalse(ExpiryDate(1, 1).isValid)
    }

    // Expires this month

    @Test
    fun isValid_withValidButExpiringThisMonth_returnsTrue() {
        val calendarNow = Calendar.getInstance()
        val yearNow = calendarNow.get(Calendar.YEAR) - 2000
        val monthNow = calendarNow.get(Calendar.MONTH) + 1

        assertTrue(ExpiryDate(monthNow, yearNow).isValid)
    }

    @Test
    fun isValid_withValidButExpiredPreviousMonth_returnsFalse() {
        val calendarNow = Calendar.getInstance()
        val yearNow = calendarNow.get(Calendar.YEAR) - 2000
        val monthNow = calendarNow.get(Calendar.MONTH)

        assertFalse(ExpiryDate(monthNow, yearNow).isValid)
    }
}