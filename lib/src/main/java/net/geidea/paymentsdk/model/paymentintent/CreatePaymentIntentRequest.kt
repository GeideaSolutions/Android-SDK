@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.paymentintent

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.math.BigDecimal
import java.util.*

@Serializable
class CreatePaymentIntentRequest private constructor(
    override var language: String? = null,
    val amount: BigDecimal,
    val currency: String,
    val customer: CustomerRequest? = null,
    val expiryDate: String? = null,
    val activationDate: String? = null,
    val eInvoiceDetails: EInvoiceDetails? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CreatePaymentIntentRequest

        if (language != other.language) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (customer != other.customer) return false
        if (expiryDate != other.expiryDate) return false
        if (activationDate != other.activationDate) return false
        if (eInvoiceDetails != other.eInvoiceDetails) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                amount,
                currency,
                customer,
                expiryDate,
                activationDate,
                eInvoiceDetails,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "CreatePaymentIntentRequest(amount=$amount, currency='$currency', customer=$customer, expiryDate=$expiryDate, activationDate=$activationDate, eInvoiceDetails=$eInvoiceDetails)"
    }

    /**
     * Builder for [CreatePaymentIntentRequest]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var amount: BigDecimal? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var currency: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var customer: CustomerRequest? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var expiryDate: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var activationDate: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var eInvoiceDetails: EInvoiceDetails? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setAmount(amount: BigDecimal?): Builder = apply { this.amount = amount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setCustomer(customer: CustomerRequest?): Builder = apply { this.customer = customer }
        fun setExpiryDate(expiryDate: String?): Builder = apply { this.expiryDate = expiryDate }
        fun setActivationDate(activationDate: String?): Builder = apply { this.activationDate = activationDate }
        fun setEInvoiceDetails(eInvoiceDetails: EInvoiceDetails?): Builder = apply { this.eInvoiceDetails = eInvoiceDetails }

        fun build(): CreatePaymentIntentRequest {
            return CreatePaymentIntentRequest(
                    language = this.language,
                    amount = requireNotNull(this.amount) { "Missing amount" },
                    currency = requireNotNull(this.currency) { "Missing currency" },
                    customer = this.customer,
                    expiryDate = this.expiryDate,
                    activationDate = this.activationDate,
                    eInvoiceDetails = this.eInvoiceDetails,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): CreatePaymentIntentRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun CreateEInvoiceRequest(initializer: CreatePaymentIntentRequest.Builder.() -> Unit): CreatePaymentIntentRequest {
    return CreatePaymentIntentRequest.Builder().apply(initializer).build()
}