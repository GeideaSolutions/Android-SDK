package net.geidea.paymentsdk.internal.util

import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal

private typealias StringPair = Pair<String, String>

class UtilsTest {

    @Test
    fun parseQueryParams_normalUrl() {
        var expected = listOf(Pair("param1", "value1"), Pair("param2", "value2"))
        assertEquals(expected, parseQueryParams("scheme://url?param1=value1&param2=value2"))

        expected = listOf(Pair("param1", "value1"))
        assertEquals(expected, parseQueryParams("scheme://url?param1=value1"))

        expected = emptyList()
        assertEquals(expected, parseQueryParams("scheme://url"))

        expected = listOf(Pair("param1", "value1"), Pair("param2", ""))
        assertEquals(expected, parseQueryParams("scheme://url?param1=value1&param2="))
    }

    @Test
    fun parseQueryParams_missingEqualSign() {
        assertEquals(listOf(Pair("param1", "")), parseQueryParams("scheme://url?param1"))
        assertEquals(listOf(Pair("param1", ""), Pair("param2", "")), parseQueryParams("scheme://url?param1&param2"))
    }

    @Test
    fun parseQueryParams_trailingEqualSign() {
        assertEquals(listOf(Pair("param1", "")), parseQueryParams("scheme://url?param1="))
    }

    @Test
    fun parseQueryParams_trailingAmpersand() {
        assertEquals(listOf(Pair("param1", "value1")), parseQueryParams("scheme://url?param1=value1&"))
    }

    @Test
    fun parseQueryParams_withMalformed_returnsEmptyList() {
        assertEquals(emptyList<StringPair>(), parseQueryParams("scheme://url?"))
        assertEquals(emptyList<StringPair>(), parseQueryParams("scheme://url?&"))
        assertEquals(emptyList<StringPair>(), parseQueryParams("scheme://url&"))
        assertEquals(emptyList<StringPair>(), parseQueryParams("scheme://url="))
        assertEquals(emptyList<StringPair>(), parseQueryParams("scheme://url?&="))
        assertEquals(emptyList<StringPair>(), parseQueryParams("scheme://url?=abc"))
    }

    @Test
    fun has2orLessFractionalDigits_withInteger_returnsTrue() {
        assertTrue(BigDecimal("1.0").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_with1FractionalDigit_returnsTrue() {
        assertTrue(BigDecimal("1.1").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_with2FractionalDigit_returnsTrue() {
        assertTrue(BigDecimal("1.11").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_with3FractionalDigit_returnsFalse() {
        assertFalse(BigDecimal("1.111").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_withZero_returnsTrue() {
        assertTrue(BigDecimal("0.0").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_withPosExponent_returnsTrue() {
        assertTrue(BigDecimal("10E12").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_withNegExponent_returnsFalse() {
        assertFalse(BigDecimal("10E-12").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_withLongFracAndNegExponent_returnsFalse() {
        assertFalse(BigDecimal("1.23456789E-3").has2orLessFractionalDigits())
    }

    @Test
    fun has2orLessFractionalDigits_withLongFracAndPostExponent_returnsFalse() {
        assertFalse(BigDecimal("1.23456789123456789E7").has2orLessFractionalDigits())
    }
}