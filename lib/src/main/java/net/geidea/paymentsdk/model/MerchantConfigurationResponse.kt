@file:UseSerializers(BigDecimalSerializer::class)

package net.geidea.paymentsdk.model

import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.serialization.BigDecimalSerializer
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.common.GeideaResponse
import java.math.BigDecimal
import java.util.*

@Parcelize
@Serializable
class MerchantConfigurationResponse

@GeideaSdkInternal
internal constructor(
        override val responseCode: String? = null,
        override val responseMessage: String? = null,
        override val detailedResponseCode: String? = null,
        override val detailedResponseMessage: String? = null,
        override val language: String? = null,
        val isActive: Boolean? = null,
        val isTest: Boolean? = null,
        val merchantName: String? = null,
        val merchantNameAr: String? = null,
        val merchantLogoUrl: String? = null,
        val merchantCountryTwoLetterCode: String? = null,
        val paymentMethods: Set<String>? = emptySet(),
        val hppDefaultTimeout: Int? = null,
        val is3dsV2Enabled: Boolean? = null,
        val isApplePaySupported: Boolean? = null,
        val isLuhnCheckActive: Boolean? = null,
        val isTokenizationEnabled: Boolean? = null,
        val isCvvRequiredForTokenPayments: Boolean? = null,
        val is3dsRequiredForTokenPayments: Boolean? = null,
        val isCallbackEnabled: Boolean? = null,
        val isPaymentMethodSelectionEnabled: Boolean? = null,
        val isTransactionReceiptEnabled: Boolean? = null,
        val isMeezaQrEnabled: Boolean? = null,
        val isValuBnplEnabled: Boolean? = null,
        val isShahryCnpBnplEnabled: Boolean? = null,
        val isSouhoolaCnpBnplEnabled: Boolean? = null,
        val useMpgsApiV60: Boolean? = null,
        val countries: Set<Country>? = null,
        val currencies: Set<String>? = null,
        val allowedInitiatedByValues: Set<String>? = emptySet(),
        val valUMinimumAmount: BigDecimal? = null,
        val souhoolaMinimumAmount: BigDecimal? = null,
        val allowCashOnDeliveryValu: Boolean? = null,
        val allowCashOnDeliveryShahry: Boolean? = null,
        val allowCashOnDeliverySouhoola: Boolean? = null,
) : GeideaJsonObject, GeideaResponse {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MerchantConfigurationResponse

        if (responseCode != other.responseCode) return false
        if (responseMessage != other.responseMessage) return false
        if (detailedResponseCode != other.detailedResponseCode) return false
        if (detailedResponseMessage != other.detailedResponseMessage) return false
        if (isActive != other.isActive) return false
        if (isTest != other.isTest) return false
        if (merchantName != other.merchantName) return false
        if (merchantNameAr != other.merchantNameAr) return false
        if (merchantLogoUrl != other.merchantLogoUrl) return false
        if (merchantCountryTwoLetterCode != other.merchantCountryTwoLetterCode) return false
        if (paymentMethods != other.paymentMethods) return false
        if (hppDefaultTimeout != other.hppDefaultTimeout) return false
        if (is3dsV2Enabled != other.is3dsV2Enabled) return false
        if (isApplePaySupported != other.isApplePaySupported) return false
        if (isLuhnCheckActive != other.isLuhnCheckActive) return false
        if (isTokenizationEnabled != other.isTokenizationEnabled) return false
        if (isCvvRequiredForTokenPayments != other.isCvvRequiredForTokenPayments) return false
        if (is3dsRequiredForTokenPayments != other.is3dsRequiredForTokenPayments) return false
        if (isCallbackEnabled != other.isCallbackEnabled) return false
        if (isPaymentMethodSelectionEnabled != other.isPaymentMethodSelectionEnabled) return false
        if (isTransactionReceiptEnabled != other.isTransactionReceiptEnabled) return false
        if (isMeezaQrEnabled != other.isMeezaQrEnabled) return false
        if (isValuBnplEnabled != other.isValuBnplEnabled) return false
        if (isShahryCnpBnplEnabled != other.isShahryCnpBnplEnabled) return false
        if (isSouhoolaCnpBnplEnabled != other.isSouhoolaCnpBnplEnabled) return false
        if (useMpgsApiV60 != other.useMpgsApiV60) return false
        if (countries != other.countries) return false
        if (currencies != other.currencies) return false
        if (allowedInitiatedByValues != other.allowedInitiatedByValues) return false
        if (language != other.language) return false
        if (valUMinimumAmount != other.valUMinimumAmount) return false
        if (souhoolaMinimumAmount != other.souhoolaMinimumAmount) return false
        if (allowCashOnDeliveryValu != other.allowCashOnDeliveryValu) return false
        if (allowCashOnDeliveryShahry != other.allowCashOnDeliveryShahry) return false
        if (allowCashOnDeliverySouhoola != other.allowCashOnDeliverySouhoola) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                responseCode,
                responseMessage,
                detailedResponseCode,
                detailedResponseMessage,
                isActive,
                isTest,
                merchantName,
                merchantNameAr,
                merchantLogoUrl,
                merchantCountryTwoLetterCode,
                paymentMethods,
                hppDefaultTimeout,
                is3dsV2Enabled,
                isApplePaySupported,
                isLuhnCheckActive,
                isTokenizationEnabled,
                isCvvRequiredForTokenPayments,
                is3dsRequiredForTokenPayments,
                isCallbackEnabled,
                isPaymentMethodSelectionEnabled,
                isTransactionReceiptEnabled,
                isMeezaQrEnabled,
                isValuBnplEnabled,
                isShahryCnpBnplEnabled,
                isSouhoolaCnpBnplEnabled,
                useMpgsApiV60,
                countries,
                currencies,
                allowedInitiatedByValues,
                language,
                valUMinimumAmount,
                souhoolaMinimumAmount,
                allowCashOnDeliveryValu,
                allowCashOnDeliveryShahry,
                allowCashOnDeliverySouhoola,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "MerchantConfigurationResponse(responseCode=$responseCode, responseMessage=$responseMessage, detailedResponseCode=$detailedResponseCode, detailedResponseMessage=$detailedResponseMessage, language=$language, isActive=$isActive, isTest=$isTest, merchantName=$merchantName, merchantNameAr=$merchantNameAr, merchantLogoUrl=$merchantLogoUrl, merchantCountryTwoLetterCode=$merchantCountryTwoLetterCode, paymentMethods=$paymentMethods, hppDefaultTimeout=$hppDefaultTimeout, is3dsV2Enabled=$is3dsV2Enabled, isApplePaySupported=$isApplePaySupported, isTokenizationEnabled=$isTokenizationEnabled, isCvvRequiredForTokenPayments=$isCvvRequiredForTokenPayments, is3dsRequiredForTokenPayments=$is3dsRequiredForTokenPayments, isCallbackEnabled=$isCallbackEnabled, isPaymentMethodSelectionEnabled=$isPaymentMethodSelectionEnabled, isTransactionReceiptEnabled=$isTransactionReceiptEnabled, isMeezaQrEnabled=$isMeezaQrEnabled, isValuBnplEnabled=$isValuBnplEnabled, isShahryCnpBnplEnabled=$isShahryCnpBnplEnabled, isSouhoolaCnpBnplEnabled=$isSouhoolaCnpBnplEnabled, useMpgsApiV60=$useMpgsApiV60, countries=$countries, currencies=$currencies, allowedInitiatedByValues=$allowedInitiatedByValues, valUMinimumAmount=$valUMinimumAmount, souhoolaMinimumAmount=$souhoolaMinimumAmount, allowCashOnDeliveryValu=$allowCashOnDeliveryValu, allowCashOnDeliveryShahry=$allowCashOnDeliveryShahry, allowCashOnDeliverySouhoola=$allowCashOnDeliverySouhoola)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): MerchantConfigurationResponse = decodeFromJson(json)
    }
}