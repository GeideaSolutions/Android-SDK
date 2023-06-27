package net.geidea.paymentsdk.api.paymentintent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.GeideaPaymentSdk.setCredentials
import net.geidea.paymentsdk.flow.GeideaResultCallback
import net.geidea.paymentsdk.flow.asIs
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

public object PaymentIntentCallbackApi {

    private val scope: CoroutineScope get() = SdkComponent.supervisorScope

    private val eInvoiceService: EInvoiceService get() = SdkComponent.eInvoiceService
    private val paymentIntentService: PaymentIntentService get() = SdkComponent.paymentIntentService
    private val meezaService: MeezaService get() = SdkComponent.meezaService

    /**
     * Create a new e-invoice.
     *
     * @param createPaymentIntentRequest request containing amount, currency, expiry date and
     * customer data. Either customer phone or email must be provided. If expiry date is not defined
     * a default expiry date is set which is 1 month after.
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun createEInvoice(createPaymentIntentRequest: CreatePaymentIntentRequest, resultCallback: GeideaResultCallback<EInvoiceOrdersResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            eInvoiceService.postEInvoice(createPaymentIntentRequest)
        }
    }

    /**
     * Get an existing e-invoice by id.
     *
     * @param paymentIntentId the id
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun getEInvoice(paymentIntentId: String, resultCallback: GeideaResultCallback<EInvoiceOrdersResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            eInvoiceService.getEInvoice(paymentIntentId)
        }
    }

    /**
     * Update an existing e-invoice.
     *
     * @param updateEInvoiceRequest request containing amount, currency, expiry date and
     * customer data. Either customer phone or email must be provided.
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun updateEInvoice(updateEInvoiceRequest: UpdateEInvoiceRequest, resultCallback: GeideaResultCallback<EInvoiceOrdersResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            eInvoiceService.putEInvoice(updateEInvoiceRequest)
        }
    }

    /**
     * Delete an existing e-invoice by id.
     *
     * @param paymentIntentId the id
     * @param resultCallback callback to receive the result on the Main thread. Successful
     * response will contain null eInvoice.
     */
    @JvmStatic
    fun deletePaymentIntent(paymentIntentId: String, resultCallback: GeideaResultCallback<EInvoiceOrdersResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            eInvoiceService.deleteEInvoice(paymentIntentId)
        }
    }

    /**
     * Get a payment intent by id.
     *
     * @param paymentIntentId the id
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun getPaymentIntent(paymentIntentId: String, resultCallback: GeideaResultCallback<PaymentIntentResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            paymentIntentService.getPaymentIntent(paymentIntentId)
        }
    }

    /**
     * Send an existing e-invoice as an SMS to customer's mobile phone. A mobile phone must be set
     * in the e-invoice customer data before calling this method.
     *
     * @param eInvoiceId the id of e-invoice to be sent
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun sendEInvoiceBySms(eInvoiceId: String, resultCallback: GeideaResultCallback<EInvoiceOrdersResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            eInvoiceService.sendEInvoiceBySms(eInvoiceId)
        }
    }

    /**
     * Send an existing e-invoice as an email to the customer. An email must be set
     * in the e-invoice customer data before calling this method.
     *
     * @param eInvoiceId the id of e-invoice to be sent
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun sendEInvoiceByEmail(eInvoiceId: String, resultCallback: GeideaResultCallback<EInvoiceOrdersResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            eInvoiceService.sendEInvoiceByEmail(eInvoiceId)
        }
    }

    /**
     * Create a new Meeza payment QR code image.
     *
     * @param createPaymentIntentRequest request containing amount, currency, expiry date,
     * customer data and merchant public key. Either customer phone or email must be provided.
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun createMeezaPaymentQrCode(createPaymentIntentRequest: CreateMeezaPaymentIntentRequest, resultCallback: GeideaResultCallback<MeezaQrImageResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            val requestWithPubKey =
                createPaymentIntentRequest.takeIf { it.merchantPublicKey != null }
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
     * phone of the customer. If [MeezaPaymentRequest.merchantPublicKey] is not supplied the one set
     * with [setCredentials] is used. At least one of the two must be set.
     * @param resultCallback callback to receive the result on the Main thread.
     *
     * @see createMeezaPaymentQrCode
     */
    @JvmStatic
    fun sendMeezaRequestToPay(meezaPaymentRequest: MeezaPaymentRequest, resultCallback: GeideaResultCallback<MeezaPaymentResponse>) {
        scope.launch(SdkComponent.dispatchersProvider.main) {
            resultCallback.onResult(PaymentIntentApi.sendMeezaRequestToPay(meezaPaymentRequest))
        }
    }
}