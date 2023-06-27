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
class UpdateEInvoiceRequest private constructor(
    override var language: String? = null,
    val paymentIntentId: String,
    val amount: BigDecimal? = null,
    val currency: String? = null,
    val customer: CustomerRequest? = null,
    val expiryDate: String? = null,
    val activationDate: String? = null,
    val status: String? = null,
    val eInvoiceDetails: EInvoiceDetails? = null,
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UpdateEInvoiceRequest

        if (language != other.language) return false
        if (paymentIntentId != other.paymentIntentId) return false
        if (amount != other.amount) return false
        if (currency != other.currency) return false
        if (customer != other.customer) return false
        if (expiryDate != other.expiryDate) return false
        if (activationDate != other.activationDate) return false
        if (status != other.status) return false
        if (eInvoiceDetails != other.eInvoiceDetails) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                paymentIntentId,
                amount,
                currency,
                customer,
                expiryDate,
                activationDate,
                status,
                eInvoiceDetails,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "UpdateEInvoiceRequest(language='$language', paymentIntentId=$paymentIntentId, amount=$amount, currency='$currency', customer=$customer, expiryDate=$expiryDate, activationDate=$activationDate, status=$status, eInvoiceDetails=$eInvoiceDetails)"
    }

    /**
     * Builder for [UpdateEInvoiceRequest]
     */
    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentIntentId: String? = null

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
        var status: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var eInvoiceDetails: EInvoiceDetails? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setPaymentIntentId(paymentIntentId: String): Builder = apply { this.paymentIntentId = paymentIntentId }
        fun setAmount(amount: BigDecimal?): Builder = apply { this.amount = amount }
        fun setCurrency(currency: String?): Builder = apply { this.currency = currency }
        fun setCustomer(customer: CustomerRequest?): Builder = apply { this.customer = customer }
        fun setExpiryDate(expiryDate: String?): Builder = apply { this.expiryDate = expiryDate }
        fun setActivationDate(activationDate: String?): Builder = apply { this.activationDate = activationDate }
        fun setStatus(status: String?): Builder = apply { this.status = status }
        fun setEInvoiceDetails(eInvoiceDetails: EInvoiceDetails?): Builder = apply { this.eInvoiceDetails = eInvoiceDetails }

        fun build(): UpdateEInvoiceRequest {
            return UpdateEInvoiceRequest(
                    language = this.language,
                    paymentIntentId = requireNotNull(this.paymentIntentId) { "Missing paymentIntentId" },
                    amount = this.amount,
                    currency = this.currency,
                    customer = this.customer,
                    expiryDate = this.expiryDate,
                    activationDate = this.activationDate,
                    status = this.status,
                    eInvoiceDetails = this.eInvoiceDetails,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): UpdateEInvoiceRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun UpdateEInvoiceRequest(initializer: UpdateEInvoiceRequest.Builder.() -> Unit): UpdateEInvoiceRequest {
    return UpdateEInvoiceRequest.Builder().apply(initializer).build()
}