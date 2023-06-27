@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.bnpl.souhoola

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.math.BigDecimal
import java.util.*

@Serializable
class InstallmentPlansRequest private constructor(
        override var language: String? = null,
        val customerIdentifier: String,
        val customerPin: String,
        val totalAmount: BigDecimal,
        val downPayment: BigDecimal,
        val currency: String,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstallmentPlansRequest

        if (language != other.language) return false
        if (customerIdentifier != other.customerIdentifier) return false
        if (customerPin != other.customerPin) return false
        if (totalAmount != other.totalAmount) return false
        if (downPayment != other.downPayment) return false
        if (currency != other.currency) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(language, customerIdentifier, customerPin, totalAmount, downPayment, currency)
    }

    // GENERATED
    override fun toString(): String {
        return "InstallmentPlansRequest(language=$language, customerIdentifier='$customerIdentifier', customerPin='$customerPin', totalAmount=$totalAmount, downPayment=$downPayment, currency='$currency')"
    }

    /**
     * Builder for [InstallmentPlansRequest]
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerIdentifier: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customerPin: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var totalAmount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var downPayment: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setCustomerIdentifier(customerIdentifier: String?): Builder = apply { this.customerIdentifier = customerIdentifier }
        fun setCustomerPin(customerPin: String?): Builder = apply { this.customerPin = customerPin }
        fun setTotalAmount(totalAmount: BigDecimal?): Builder = apply { this.totalAmount = totalAmount }
        fun setDownPayment(downPayment: BigDecimal?): Builder = apply { this.downPayment = downPayment }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }

        fun build(): InstallmentPlansRequest = InstallmentPlansRequest(
            language = this.language,
            customerIdentifier = requireNotNull(this.customerIdentifier) { "Missing customerIdentifier" },
            customerPin = requireNotNull(this.customerPin) { "Missing customerPin" },
            totalAmount = requireNotNull(this.totalAmount) { "Missing totalAmount" },
            downPayment = requireNotNull(this.downPayment) { "Missing downPayment" },
            currency = requireNotNull(this.currency) { "Missing currency" },
        )
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): InstallmentPlansRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun InstallmentPlansRequest(initializer: InstallmentPlansRequest.Builder.() -> Unit): InstallmentPlansRequest {
    return InstallmentPlansRequest.Builder().apply(initializer).build()
}