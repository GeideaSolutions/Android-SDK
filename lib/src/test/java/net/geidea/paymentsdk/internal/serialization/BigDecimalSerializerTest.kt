package net.geidea.paymentsdk.internal.serialization

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class BigDecimalSerializerTest {

    @MockK
    internal lateinit var mockEncoder: Encoder

    @MockK
    internal lateinit var mockDecoder: Decoder

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { mockEncoder.encodeDouble(any()) } just Runs
    }

    // serialize

    @Test
    fun serialize_100_11() {
        BigDecimalSerializer.serialize(mockEncoder, BigDecimal("100.11"))
        verify { mockEncoder.encodeDouble(eq(100.11)) }
    }

    @Test
    fun serialize_100_499() {
        BigDecimalSerializer.serialize(mockEncoder, BigDecimal("100.499"))
        verify { mockEncoder.encodeDouble(eq(100.499)) }
    }

    @Test
    fun serialize_233_45() {
        BigDecimalSerializer.serialize(mockEncoder, BigDecimal("233.45"))
        verify { mockEncoder.encodeDouble(eq(233.45)) }
    }

    @Test
    fun serialize_0_99999999999999999999999999() {
        BigDecimalSerializer.serialize(mockEncoder, BigDecimal("0.99999999999999999999999999"))
        verify { mockEncoder.encodeDouble(eq(0.99999999999999999999999999)) }
    }

    @Test
    fun serialize_minus100() {
        BigDecimalSerializer.serialize(mockEncoder, BigDecimal("-100"))
        verify { mockEncoder.encodeDouble(eq(-100.0)) }
    }

    // deserialize

    @Test
    fun deserialize_100_11() {
        every { mockDecoder.decodeDouble() } returns 100.11
        assertEquals(BigDecimal("100.11"), BigDecimalSerializer.deserialize(mockDecoder))
    }

    @Test
    fun deserialize_100_499() {
        every { mockDecoder.decodeDouble() } returns 100.499
        assertEquals(BigDecimal("100.499"), BigDecimalSerializer.deserialize(mockDecoder))
    }

    @Test
    fun deserialize_233_45() {
        every { mockDecoder.decodeDouble() } returns 233.45
        assertEquals(BigDecimal("233.45"), BigDecimalSerializer.deserialize(mockDecoder))
    }

    @Test
    fun deserialize_0_99999999999999999999999999() {
        // // Double supports max 16 digits after decimal point, if it is longer then rounding is applied
        every { mockDecoder.decodeDouble() } returns 1.0
        assertEquals(BigDecimal("1.0"), BigDecimalSerializer.deserialize(mockDecoder))
    }

    @Test
    fun deserialize_0_9999999999999999() {
        // Double supports max 16 digits after decimal point
        every { mockDecoder.decodeDouble() } returns 0.9999999999999999
        assertEquals(BigDecimal("0.9999999999999999"), BigDecimalSerializer.deserialize(mockDecoder))
    }

    @Test
    fun deserialize_minus100() {
        every { mockDecoder.decodeDouble() } returns -100.0
        assertEquals(BigDecimal("-100.0"), BigDecimalSerializer.deserialize(mockDecoder))
    }
}