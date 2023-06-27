package net.geidea.paymentsdk.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AddressTest {

    @Test
    fun isNullOrEmpty_withNull_returnsTrue() {
        assertTrue(null.isNullOrEmpty())
    }

    @Test
    fun isNullOrEmpty_withAllNulls_returnsTrue() {
        assertTrue(Address(null, null, null, null).isNullOrEmpty())
    }

    @Test
    fun isNullOrEmpty_withAllEmptys_returnsTrue() {
        assertTrue(Address("", "", "", "").isNullOrEmpty())
    }

    @Test
    fun isNullOrEmpty_withOneFieldNull_returnsFalse() {
        assertFalse(Address(null, "city", "street", "postcode").isNullOrEmpty())
        assertFalse(Address("SAU", null, "street", "postcode").isNullOrEmpty())
        assertFalse(Address("SAU", "city", null, "postcode").isNullOrEmpty())
        assertFalse(Address("SAU", "city", "street", null).isNullOrEmpty())
    }

    @Test
    fun isNullOrEmpty_withOneFieldEmpty_returnsFalse() {
        assertFalse(Address("", "city", "street", "postcode").isNullOrEmpty())
        assertFalse(Address("SAU", "", "street", "postcode").isNullOrEmpty())
        assertFalse(Address("SAU", "city", "", "postcode").isNullOrEmpty())
        assertFalse(Address("SAU", "city", "street", "").isNullOrEmpty())
    }

    @Test
    fun isNullOrEmpty_withSomeNullSomeEmpty_returnsTrue() {
        assertTrue(Address("", null, null, null).isNullOrEmpty())
        assertTrue(Address(null, "", null, null).isNullOrEmpty())
        assertTrue(Address(null, null, "", null).isNullOrEmpty())
        assertTrue(Address(null, null, null, "").isNullOrEmpty())
    }

    @Test
    fun equalsIgnoreCase_withNulls_returnsTrue() {
        val a1: Address? = null
        val a2: Address? = null
        assertTrue(a1.equalsIgnoreCase(a2))
    }

    @Test
    fun equalsIgnoreCase_withOneNull_returnsFalse() {
        val nullAddress: Address? = null
        assertFalse(Address("SAU").equalsIgnoreCase(nullAddress))
        assertFalse(nullAddress.equalsIgnoreCase(Address("SAU")))
    }

    @Test
    fun equalsIgnoreCase_withLetterWithDifferentCase_returnsTrue() {
        assertTrue(Address(countryCode = "SAU").equalsIgnoreCase(Address(countryCode = "sAU")))
        assertTrue(Address(street = "street").equalsIgnoreCase(Address(street = "Street")))
        assertTrue(Address(city = "city").equalsIgnoreCase(Address(city = "City")))
        assertTrue(Address(postCode = "postCode").equalsIgnoreCase(Address(postCode = "PostCode")))
    }

    @Test
    fun equalsIgnoreCase_withSameFieldEmptyAndNull_returnsTrue() {
        assertTrue(Address(countryCode = "").equalsIgnoreCase(Address(countryCode = null)))
        assertTrue(Address(street = "").equalsIgnoreCase(Address(street = null)))
        assertTrue(Address(city = "").equalsIgnoreCase(Address(city = null)))
        assertTrue(Address(postCode = "").equalsIgnoreCase(Address(postCode = null)))

        assertTrue(Address(countryCode = null).equalsIgnoreCase(Address(countryCode = "")))
        assertTrue(Address(street = null).equalsIgnoreCase(Address(street = "")))
        assertTrue(Address(city = null).equalsIgnoreCase(Address(city = "")))
        assertTrue(Address(postCode = null).equalsIgnoreCase(Address(postCode = "")))
    }
}