package net.geidea.paymentsdk.internal.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

internal object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            serialName = "net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer",
            kind = PrimitiveKind.DOUBLE
    )
    override fun serialize(encoder: Encoder, value: BigDecimal) = encoder.encodeDouble(value.toDouble())
    override fun deserialize(decoder: Decoder): BigDecimal = BigDecimal(decoder.decodeDouble().toString())
}