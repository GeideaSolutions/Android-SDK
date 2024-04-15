package net.geidea.paymentsdk.model.auth.v6

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import java.util.Objects

@Serializable
class InitiateAuthenticationRequest private constructor(
    override var language: String? = null,
    val sessionId: String? = null,
    val cardNumber: String? = null,
    val returnUrl: String? = null,
    val merchantName: String? = null,
    val callbackUrl: String? = null,
    val isSetPaymentMethodEnabled: Boolean? = false,
    val isCreateCustomerEnabled: Boolean? = false,
    val paymentOperation: String? = null,
    val cardOnFile: Boolean? = false,
    val restrictPaymentMethods: Boolean? = false,
    val deviceIdentification: DeviceIdentificationRequest? = null,
    val orderId: String? = null,
    val source: String? = null
) :
    LocalizableRequest {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InitiateAuthenticationRequest

        if (language != other.language) return false
        if (sessionId != other.sessionId) return false
        if (cardNumber != other.cardNumber) return false
        if (returnUrl != other.returnUrl) return false
        if (merchantName != other.merchantName) return false
        if (callbackUrl != other.callbackUrl) return false
        if (isSetPaymentMethodEnabled != other.isSetPaymentMethodEnabled) return false
        if (isCreateCustomerEnabled != other.isCreateCustomerEnabled) return false
        if (paymentOperation != other.paymentOperation) return false
        if (cardOnFile != other.cardOnFile) return false
        if (restrictPaymentMethods != other.restrictPaymentMethods) return false
        if (deviceIdentification != other.deviceIdentification) return false
        if (orderId != other.orderId) return false
        return source == other.source
    }

    override fun hashCode(): Int {
        return Objects.hash(
            language,
            sessionId,
            cardNumber,
            returnUrl,
            merchantName,
            callbackUrl,
            isSetPaymentMethodEnabled,
            isCreateCustomerEnabled,
            paymentOperation,
            cardOnFile,
            restrictPaymentMethods,
            deviceIdentification,
            orderId,
            source
        )
    }

    override fun toString(): String {
        return "InitiateAuthenticationRequest(language=$language, sessionId=$sessionId, cardNumber=$cardNumber, returnUrl=$returnUrl, merchantName=$merchantName, callbackUrl=$callbackUrl, isSetPaymentMethodEnabled=$isSetPaymentMethodEnabled, isCreateCustomerEnabled=$isCreateCustomerEnabled, paymentOperation=$paymentOperation, cardOnFile=$cardOnFile, restrictPaymentMethods=$restrictPaymentMethods, deviceIdentification=$deviceIdentification, orderId=$orderId, source=$source)"
    }

    class Builder() {

        @JvmSynthetic // Hide 'void' setter from Java
        var sessionId: String? = null

        @JvmSynthetic
        var returnUrl: String? = null

        @JvmSynthetic
        var cardNumber: String? = null

        @JvmSynthetic
        var merchantName: String? = null

        @JvmSynthetic
        var callbackUrl: String? = null

        @JvmSynthetic
        var isSetPaymentMethodEnabled: Boolean? = false

        @JvmSynthetic
        var isCreateCustomerEnabled: Boolean? = false

        @JvmSynthetic
        var paymentOperation: String? = null

        @JvmSynthetic
        var cardOnFile: Boolean? = false

        @JvmSynthetic
        var restrictPaymentMethods: Boolean? = false

        @JvmSynthetic
        var deviceIdentificationRequest: DeviceIdentificationRequest? = null

        @JvmSynthetic
        var orderId: String? = null

        @JvmSynthetic
        var source: String? =
            null // Intentionally without default b/c serialization omits the defaults

        @JvmSynthetic
        var language: String? = null

        fun setSessionId(sessionId: String?): Builder = apply { this.sessionId = sessionId }

        fun setReturnUrl(returnUrl: String?): Builder = apply { this.returnUrl = returnUrl }

        fun setCardNumber(cardNumber: String?): Builder = apply { this.cardNumber = cardNumber }

        fun setMerchantName(merchantName: String?): Builder =
            apply { this.merchantName = merchantName }

        fun setCallbackUrl(callbackUrl: String?): Builder = apply { this.callbackUrl = callbackUrl }

        fun setIsSetPaymentMethodEnabled(isSetPaymentMethodEnabled: Boolean?): Builder =
            apply { this.isSetPaymentMethodEnabled = isSetPaymentMethodEnabled }

        fun setIsCreateCustomerEnabled(isCreateCustomerEnabled: Boolean?): Builder =
            apply { this.isCreateCustomerEnabled = isCreateCustomerEnabled }

        fun setPaymentOperation(paymentOperation: String?): Builder =
            apply { this.paymentOperation = paymentOperation }

        fun setCardOnFile(cardOnFile: Boolean?): Builder = apply { this.cardOnFile = cardOnFile }

        fun setRestrictPaymentMethods(restrictPaymentMethods: Boolean?): Builder =
            apply { this.restrictPaymentMethods = restrictPaymentMethods }

        fun setDeviceIdentificationRequest(device: DeviceIdentificationRequest?): Builder =
            apply { this.deviceIdentificationRequest = device }

        fun setOrderId(orderId: String?): Builder = apply { this.orderId = orderId }

        fun setSource(source: String?): Builder = apply { this.source = source }

        fun setLanguage(language: String?): Builder = apply { this.language = language }

        fun build(): InitiateAuthenticationRequest {
            return InitiateAuthenticationRequest(
                language = this.language,
                sessionId = requireNotNull(this.sessionId) { "Required parameters missing" },
                cardNumber = requireNotNull(this.cardNumber) { "Missing Card Number" },
                returnUrl = requireNotNull(this.returnUrl) { "Missing Return URL" },
                merchantName = this.merchantName,
                callbackUrl = this.callbackUrl,
                isSetPaymentMethodEnabled = this.isSetPaymentMethodEnabled,
                isCreateCustomerEnabled = this.isCreateCustomerEnabled,
                paymentOperation = this.paymentOperation,
                cardOnFile = this.cardOnFile,
                restrictPaymentMethods = this.restrictPaymentMethods,
                deviceIdentification = this.deviceIdentificationRequest,
                orderId = this.orderId,
                source = this.source
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): InitiateAuthenticationRequest = decodeFromJson(json)
    }
}

@JvmSynthetic // Hide from Java callers who should use Builder.
fun InitiateAuthenticationRequest(initializer: InitiateAuthenticationRequest.Builder.() -> Unit): InitiateAuthenticationRequest {
    return InitiateAuthenticationRequest.Builder().apply(initializer).build()
}