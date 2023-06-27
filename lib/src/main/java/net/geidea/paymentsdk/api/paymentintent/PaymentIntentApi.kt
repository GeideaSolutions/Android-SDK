package net.geidea.paymentsdk.api.paymentintent

import kotlinx.coroutines.CoroutineScope
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.GeideaPaymentSdk.setCredentials
import net.geidea.paymentsdk.flow.*
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.EInvoiceService
import net.geidea.paymentsdk.internal.service.MeezaService
import net.geidea.paymentsdk.internal.service.PaymentIntentService
import net.geidea.paymentsdk.internal.util.launchWithCallback
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.meezaqr.CreateMeezaPaymentIntentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentResponse
import net.geidea.paymentsdk.model.meezaqr.MeezaQrImageResponse
import net.geidea.paymentsdk.model.paymentintent.CreatePaymentIntentRequest
import net.geidea.paymentsdk.model.paymentintent.EInvoiceOrdersResponse
import net.geidea.paymentsdk.model.paymentintent.PaymentIntentResponse
import net.geidea.paymentsdk.model.paymentintent.UpdateEInvoiceRequest

public object PaymentIntentApi {

    private val scope: CoroutineScope get() = SdkComponent.supervisorScope

    private val eInvoiceService: EInvoiceService get() = SdkComponent.eInvoiceService
    private val paymentIntentService: PaymentIntentService get() = SdkComponent.paymentIntentService
    private val meezaService: MeezaService get() = SdkComponent.meezaService

    /**
     * Create a new e-invoice.
     *
     * @param createPaymentIntentRequest request containing amount, currency, expiry date and
     * customer data. Either customer phone or email must be provided.
     */
    suspend fun createEInvoice(createPaymentIntentRequest: CreatePaymentIntentRequest): GeideaResult<EInvoiceOrdersResponse> {
        return responseAsResult(::asIs) {
            eInvoiceService.postEInvoice(createPaymentIntentRequest)
        }
    }

    /**
     * Get an existing e-invoice by id.
     *
     * @param paymentIntentId the id
     */
    suspend fun getEInvoice(paymentIntentId: String): GeideaResult<EInvoiceOrdersResponse> {
        return responseAsResult(::asIs) {
            eInvoiceService.getEInvoice(paymentIntentId)
        }
    }

    /**
     * Update an existing e-invoice.
     *
     * @param updateEInvoiceRequest request containing amount, currency, expiry date and
     * customer data. Either customer phone or email must be provided.
     */
    suspend fun updateEInvoice(updateEInvoiceRequest: UpdateEInvoiceRequest): GeideaResult<EInvoiceOrdersResponse> {
        return responseAsResult(::asIs) {
            eInvoiceService.putEInvoice(updateEInvoiceRequest)
        }
    }

    /**
     * Delete an existing e-invoice by id.
     *
     * @param paymentIntentId the id
     *
     * @return successful response will contain null eInvoice.
     */
    suspend fun deletePaymentIntent(paymentIntentId: String): GeideaResult<EInvoiceOrdersResponse> {
        return responseAsResult(::asIs) {
            eInvoiceService.deleteEInvoice(paymentIntentId)
        }
    }

    /**
     * Get a payment intent by id.
     *
     * @param paymentIntentId the id
     */
    suspend fun getPaymentIntent(paymentIntentId: String): GeideaResult<PaymentIntentResponse> {
        return responseAsResult(::asIs) {
            paymentIntentService.getPaymentIntent(paymentIntentId)
        }
    }

    /**
     * Send an existing e-invoice as an SMS to customer's mobile phone. A mobile phone must be set
     * in the e-invoice customer data before calling this method.
     *
     * @param eInvoiceId the id of e-invoice to be sent
     */
    suspend fun sendEInvoiceBySms(eInvoiceId: String): GeideaResult<EInvoiceOrdersResponse> {
        return responseAsResult(::asIs) {
            eInvoiceService.sendEInvoiceBySms(eInvoiceId)
        }
    }

    /**
     * Send an existing e-invoice as an email to the customer. An email must be set
     * in the e-invoice customer data before calling this method.
     *
     * @param eInvoiceId the id of e-invoice to be sent
     */
    suspend fun sendEInvoiceByEmail(eInvoiceId: String): GeideaResult<EInvoiceOrdersResponse> {
        return responseAsResult(::asIs) {
            eInvoiceService.sendEInvoiceByEmail(eInvoiceId)
        }
    }

    /**
     * Send an existing e-invoice as an email to the customer. An email must be set
     * in the e-invoice customer data before calling this method.
     *
     * @param eInvoiceId the id of e-invoice to be sent
     * @param resultCallback callback to receive the result on the Main thread.
     */
    fun sendEInvoiceByEmail(eInvoiceId: String, resultCallback: GeideaResultCallback<EInvoiceOrdersResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            eInvoiceService.sendEInvoiceByEmail(eInvoiceId)
        }
    }

    /**
     * Create a new Meeza payment QR code image.
     *
     * @param createPaymentIntentRequest request containing amount, currency, expiry date,
     * customer data and merchant public key. If merchantPublicKey is not supplied the one set with
     * [setCredentials] is used.
     * Either customer phone or email must be provided.
     */
    suspend fun createMeezaPaymentQrCode(createPaymentIntentRequest: CreateMeezaPaymentIntentRequest): GeideaResult<MeezaQrImageResponse> {
        return responseAsResult(::asIs) {
            val requestWithPubKey = createPaymentIntentRequest.takeIf { it.merchantPublicKey != null }
                ?: createPaymentIntentRequest.copy(merchantPublicKey = GeideaPaymentSdk.merchantKey)
            meezaService.postCreateQrCodeImageBase64(requestWithPubKey)
        }
    }

    /**
     * Send a Meeza request to pay. The request will trigger a mobile notification that leads the
     * customer to a payment UI in the Meeza wallet app.
     *
     * **Note**: Before using this method it might be necessary to verify the Meeza QR payments are
     * currently activated by checking the [MerchantConfigurationResponse.isMeezaQrEnabled] flag.
     *
     * @param meezaPaymentRequest request containing the QR code payload (message) and the mobile
     * phone of the customer. If merchantPublicKey is not supplied the one set
     * with [setCredentials] is used. At least one of the two must be set.
     *
     * @see sendMeezaRequestToPay
     */
    suspend fun sendMeezaRequestToPay(meezaPaymentRequest: MeezaPaymentRequest): GeideaResult<MeezaPaymentResponse> {
        return try {
            val requestWithPubKey = meezaPaymentRequest.takeIf { it.merchantPublicKey != null }
                ?: meezaPaymentRequest.copy(merchantPublicKey = GeideaPaymentSdk.merchantKey)
            val response = meezaService.postRequestToPay(requestWithPubKey)

            if (response.isSuccess) {
                GeideaResult.Success(response)
            } else {
                GeideaResult.NetworkError(
                    responseCode = response.responseCode,
                    responseMessage = response.responseDescription,
                )
            }
        } catch (t: Throwable) {
            // TODO treat cancellation exceptions as Cancelled result?
            t.toGeideaResult()
        }
    }
}