@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model.auth.v6

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.PaymentMethod
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.Objects

@Serializable
class AuthenticatePayerRequest private constructor(
    override var language: String? = null,
    val sessionId: String? = null,
    val orderId: String? = null,
    val callbackUrl: String? = null,
    val cardOnFile: Boolean = false,
    val paymentOperation: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val deviceIdentification: DeviceIdentificationRequest? = null,
    val merchantName: String? = null,
    val restrictPaymentMethods: Boolean? = false,
    val isCreateCustomerEnabled: Boolean? = false,
    val isSetPaymentMethodEnabled: Boolean? = false,
    val javaEnabled: Boolean? = false,
    val javaScriptEnabled: Boolean? = false,
    val timeZone: String? = null,
    val source: String?   // Intentionally without default b/c serialization omits the defaults
) : LocalizableRequest {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthenticatePayerRequest

        if (language != other.language) return false
        if (sessionId != other.sessionId) return false
        if (orderId != other.orderId) return false
        if (callbackUrl != other.callbackUrl) return false
        if (cardOnFile != other.cardOnFile) return false
        if (paymentOperation != other.paymentOperation) return false
        if (paymentMethod != other.paymentMethod) return false
        if (deviceIdentification != other.deviceIdentification) return false
        if (merchantName != other.merchantName) return false
        if (restrictPaymentMethods != other.restrictPaymentMethods) return false
        if (isCreateCustomerEnabled != other.isCreateCustomerEnabled) return false
        if (isSetPaymentMethodEnabled != other.isSetPaymentMethodEnabled) return false
        if (javaEnabled != other.javaEnabled) return false
        if (javaScriptEnabled != other.javaScriptEnabled) return false
        if (timeZone != other.timeZone) return false
        return source == other.source
    }

    override fun hashCode(): Int {
        return Objects.hash(
            language,
            sessionId,
            orderId,
            callbackUrl,
            cardOnFile,
            paymentOperation,
            paymentMethod,
            deviceIdentification,
            merchantName,
            restrictPaymentMethods,
            isCreateCustomerEnabled,
            isSetPaymentMethodEnabled,
            javaEnabled,
            javaScriptEnabled,
            timeZone,
            source
        )
    }

    /**
     * Builder for [AuthenticatePayerRequest]
     */
    class Builder() {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var sessionId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var orderId: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var callbackUrl: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var cardOnFile: Boolean = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentOperation: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var paymentMethod: PaymentMethod? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var deviceIdentification: DeviceIdentificationRequest? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var merchantName: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var restrictPaymentMethods: Boolean? = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var isCreateCustomerEnabled: Boolean? = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var isSetPaymentMethodEnabled: Boolean? = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var javaEnabled: Boolean? = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var javaScriptEnabled: Boolean? = false

        @set:JvmSynthetic // Hide 'void' setter from Java
        var timeZone: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var source: String? = null


        fun setLanguage(language: String?): Builder = apply { this.language = language }

        fun setSessionId(sessionId: String?): Builder = apply { this.sessionId = sessionId }

        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }

        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }

        fun setCardOnFile(cardOnFile: Boolean): Builder = apply { this.cardOnFile = cardOnFile }

        fun setPaymentOperation(paymentOperation: String?): Builder =
            apply { this.paymentOperation = paymentOperation }

        fun setPaymentMethod(paymentMethod: PaymentMethod?): Builder =
            apply { this.paymentMethod = paymentMethod }

        fun setDeviceIdentification(deviceIdentification: DeviceIdentificationRequest?): Builder =
            apply { this.deviceIdentification = deviceIdentification }

        fun setMerchantName(merchantName: String?): Builder =
            apply { this.merchantName = merchantName }

        fun setRestrictPaymentMethods(restrictPaymentMethods: Boolean?): Builder =
            apply { this.restrictPaymentMethods = restrictPaymentMethods }

        fun setIsCreateCustomerEnabled(isCreateCustomerEnabled: Boolean?): Builder =
            apply { this.isCreateCustomerEnabled = isCreateCustomerEnabled }

        fun setIsSetPaymentMethodEnabled(isSetPaymentMethodEnabled: Boolean?): Builder =
            apply { this.isSetPaymentMethodEnabled = isSetPaymentMethodEnabled }

        fun setJavaEnabled(javaEnabled: Boolean?): Builder =
            apply { this.javaEnabled = javaEnabled }

        fun setJavaScriptEnabled(javaScriptEnabled: Boolean?): Builder =
            apply { this.javaScriptEnabled = javaScriptEnabled }

        fun setTimeZone(timeZone: String?): Builder = apply { this.timeZone = timeZone }

        fun setSource(source: String?): Builder = apply { this.source = source }

        fun build(): AuthenticatePayerRequest {
            return AuthenticatePayerRequest(
                language = language,
                sessionId = sessionId,
                orderId = orderId,
                callbackUrl = callbackUrl,
                cardOnFile = cardOnFile,
                paymentOperation = paymentOperation,
                paymentMethod = paymentMethod,
                deviceIdentification = deviceIdentification,
                merchantName = merchantName,
                restrictPaymentMethods = restrictPaymentMethods,
                isCreateCustomerEnabled = isCreateCustomerEnabled,
                isSetPaymentMethodEnabled = isSetPaymentMethodEnabled,
                javaEnabled = javaEnabled,
                javaScriptEnabled = javaScriptEnabled,
                timeZone = timeZone,
                source = source
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): AuthenticatePayerRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun AuthenticatePayerRequest(initializer: AuthenticatePayerRequest.Builder.() -> Unit): AuthenticatePayerRequest {
    return AuthenticatePayerRequest.Builder().apply(initializer).build()
}