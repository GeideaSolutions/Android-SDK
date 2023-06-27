package net.geidea.paymentsdk.internal.serialization

import net.geidea.paymentsdk.model.ExpiryDate
import org.junit.Assert.assertEquals
import org.junit.Test

class SerializationTest {

    @Test
    fun fromJson() {
        val expected = ExpiryDate(11, 22)
        //language=JSON
        val actual = ExpiryDate.fromJson("""{"month":11,"year":22}""")
        assertEquals(expected, actual)
    }

    @Test
    fun testToJson() {
        val expiry = ExpiryDate(11, 22)
        //language=JSON
        assertEquals("""{"month":11,"year":22}""", expiry.toJson())
    }

    @Test
    fun testToJsonPretty() {
        val expiry = ExpiryDate(11, 22)
        val expected = """
        {
            "month": 11,
            "year": 22
        }
        """.trimIndent()
        assertEquals(expected, expiry.toJson(pretty = true))
    }
}