package net.geidea.paymentsdk.internal.ui.fragment.qr.r2p

import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.di.SdkComponent.meezaService
import net.geidea.paymentsdk.model.common.Source
import net.geidea.paymentsdk.model.meezaqr.CreateMeezaPaymentIntentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaQrImageResponse
import net.geidea.paymentsdk.model.paymentintent.CustomerRequest
import java.math.BigDecimal

/**
 * A use-case for sending a payment request to customer. Seen as a notification in their wallet app.
 */
@GeideaSdkInternal
internal class MeezaQrRequestToPay(
    val orderId: String?,
    val amount: BigDecimal,
    val currency: String,
    val phoneNumber: String,
    val customerEmail: String?,
    val callbackUrl: String?,
) {
    private fun createMeezaPaymentIntentRequest(): CreateMeezaPaymentIntentRequest =
        CreateMeezaPaymentIntentRequest {
            orderId = this@MeezaQrRequestToPay.orderId
            merchantPublicKey = GeideaPaymentSdk.merchantKey
            amount = this@MeezaQrRequestToPay.amount
            currency = this@MeezaQrRequestToPay.currency
            if (!this@MeezaQrRequestToPay.customerEmail.isNullOrBlank()) {
                customer = CustomerRequest {
                    email = this@MeezaQrRequestToPay.customerEmail
                }
            }
            source = Source.MOBILE_APP
            callbackUrl = this@MeezaQrRequestToPay.callbackUrl
        }

    private fun createMeezaPaymentRequest(qrCodeMessage: String): MeezaPaymentRequest =
        MeezaPaymentRequest {
            this.merchantPublicKey = GeideaPaymentSdk.merchantKey
            this.receiverId = "0$phoneNumber"
            this.qrCodeMessage = qrCodeMessage
        }

    suspend fun perform(): GeideaResult<MeezaQrImageResponse> {
        val requestQr = createMeezaPaymentIntentRequest()
        val resultQr = responseAsResult { meezaService.postCreateQrCodeImageBase64(requestQr) }
        return if (resultQr is GeideaResult.Success) {
            val requestSend = createMeezaPaymentRequest(resultQr.data.message!!)
            val resultSend = responseAsResult { meezaService.postRequestToPay(requestSend) }
            if (resultSend is GeideaResult.Success) {
                resultQr
            } else {
                resultSend as GeideaResult.Error
            }
        } else {
            resultQr
        }
    }
}