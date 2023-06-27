package net.geidea.paymentsdk.api.gateway

import kotlinx.coroutines.CoroutineScope
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.flow.GeideaResultCallback
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.di.SdkComponent.authenticationV4Service
import net.geidea.paymentsdk.internal.service.*
import net.geidea.paymentsdk.internal.service.receipt.ReceiptService
import net.geidea.paymentsdk.internal.util.launchWithCallback
import net.geidea.paymentsdk.model.*
import net.geidea.paymentsdk.model.auth.v1.AuthenticationRequest
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v4.AuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationResponse
import net.geidea.paymentsdk.model.order.*
import net.geidea.paymentsdk.model.pay.PaymentRequest
import net.geidea.paymentsdk.model.pay.PaymentResponse
import net.geidea.paymentsdk.model.pay.TokenPaymentRequest
import net.geidea.paymentsdk.model.receipt.ReceiptResponse

public object GatewayCallbackApi {

    private val scope: CoroutineScope get() = SdkComponent.supervisorScope

    private val merchantsService: MerchantsService get() = SdkComponent.merchantsService
    private val authenticationV1Service: AuthenticationV1Service get() = SdkComponent.authenticationV1Service
    private val authenticationV3Service: AuthenticationV3Service get() = SdkComponent.authenticationV3Service
    private val paymentService: PaymentService get() = SdkComponent.paymentService
    private val tokenPaymentService: TokenPaymentService get() = SdkComponent.tokenPaymentService
    private val captureService: CaptureService get() = SdkComponent.captureService
    private val refundService: RefundService get() = SdkComponent.refundService
    private val cancellationService: CancellationService get() = SdkComponent.cancellationService
    private val orderService: OrderService get() = SdkComponent.orderService
    private val receiptService: ReceiptService get() = SdkComponent.receiptService

    /**
     * Get your Merchant configuration.
     *
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun getMerchantConfiguration(resultCallback: GeideaResultCallback<MerchantConfigurationResponse>) {
        scope.launchWithCallback(resultCallback, ::asIs) {
            merchantsService.getMerchantConfiguration(GeideaPaymentSdk.merchantKey)
        }
    }

    /**
     * Perform 3DSv1 authentication of a payment with callback.
     *
     * On success a new order is created, new 3DS transaction
     * is opened and an [AuthenticationResponse.htmlBodyContent] is returned containing
     * authentication HTML body of the issuer bank. It is your (as a Merchant) responsibility to
     * open it in a WebView or browser to allow the customer to authenticate.
     *
     * @param authenticationRequest request containing order, customer and payment data
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun authenticateV1(authenticationRequest: AuthenticationRequest, resultCallback: GeideaResultCallback<AuthenticationResponse>) {
        scope.launchWithCallback(resultCallback, ::asIs) {
            authenticationV1Service.postAuthenticate(authenticationRequest)
        }
    }

    /**
     * Initiates a 3DSv2 authentication which basically checks the 3DS enrollment of the card
     * [InitiateAuthenticationRequest.cardNumber]. If successful a new order is created.
     * A subsequent call to [authenticatePayerV4] must be performed to continue the 3DSv2 authentication.
     *
     * On success a new order is created, new 3DS transaction
     * is opened and an [InitiateAuthenticationResponse.redirectHtml] is returned.
     * It is your (Merchant) responsibility to open it in a WebView or browser.
     *
     * @param initiateAuthenticationRequest request containing order, customer and payment data
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun initiateAuthenticationV4(initiateAuthenticationRequest: InitiateAuthenticationRequest, resultCallback: GeideaResultCallback<InitiateAuthenticationResponse>) {
        scope.launchWithCallback(resultCallback, ::asIs) {
            authenticationV4Service.postInitiateAuthentication(
                initiateAuthenticationRequest
            )
        }
    }

    /**
     * Perform payer authentication as part of the 3DSv2 payment flow.
     * This call should be next after a call to [initiateAuthenticationV4].
     *
     * @param authenticatePayerRequest payer authentication request
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun authenticatePayerV4(authenticatePayerRequest: AuthenticatePayerRequest, resultCallback: GeideaResultCallback<AuthenticationResponse>) {
        scope.launchWithCallback(resultCallback, ::asIs) {
            authenticationV4Service.postAuthenticatePayer(authenticatePayerRequest)
        }
    }

    /**
     * Perform a payment transaction for a given [order id][PaymentRequest.orderId] with callback.
     *
     * @param paymentRequest request containing the order id and the payment data
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun pay(paymentRequest: PaymentRequest, resultCallback: GeideaResultCallback<Order>) {
        scope.launchWithCallback(resultCallback, outputTransform = { it.order!! }) {
            paymentService.postPay(paymentRequest)
        }
    }

    /**
     * Perform a payment transaction with [token Id][TokenPaymentRequest.tokenId] with callback.
     *
     * You might check if tokenization is currently enabled for you by checking
     * [MerchantConfigurationResponse.isTokenizationEnabled] flag returned by
     * [getMerchantConfiguration].
     *
     * @param tokenPaymentRequest request containing the payment data
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun payWithToken(tokenPaymentRequest: TokenPaymentRequest, resultCallback: GeideaResultCallback<Order>) {
        scope.launchWithCallback(resultCallback, outputTransform = { it.order!! }) {
            tokenPaymentService.postPayWithToken(tokenPaymentRequest)
        }
    }

    /**
     * Captures the amount of an [Order] that is already authorized ([Order.detailedStatus] is
     * [OrderStatus.AUTHORIZED].
     *
     * To pre-authorize a payment set [PaymentData.paymentOperation]
     * (or [AuthenticationRequest.paymentOperation]) to [PaymentOperation.PREAUTHORIZE].
     *
     * @param captureRequest request containing an order id
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun captureOrder(captureRequest: CaptureRequest, resultCallback: GeideaResultCallback<Order>) {
        scope.launchWithCallback(resultCallback, outputTransform = { it.order!! }) {
            captureService.postCapture(captureRequest)
        }
    }

    /**
     * Refund an order.
     *
     * @param refundRequest request containing an order id
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun refundOrder(refundRequest: RefundRequest, resultCallback: GeideaResultCallback<Order>) {
        scope.launchWithCallback(resultCallback, outputTransform = { it.order!! }) {
            refundService.postRefund(refundRequest)
        }
    }

    /**
     * Cancel an order.
     *
     * @param cancelRequest request containing an order id
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun cancelOrder(cancelRequest: CancelRequest, resultCallback: GeideaResultCallback<PaymentResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            cancellationService.postCancel(cancelRequest)
        }
    }

    /**
     * Get an existing order by id.
     *
     * @param orderId the id
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun getOrder(orderId: String, resultCallback: GeideaResultCallback<Order>) {
        scope.launchWithCallback(resultCallback, outputTransform = { it.order!! }) {
            orderService.getOrder(orderId)
        }
    }

    /**
     * Get a list of orders matching given search filters.
     *
     * @param orderSearchRequest search filters
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun getOrders(orderSearchRequest: OrderSearchRequest, resultCallback: GeideaResultCallback<OrderSearchResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            orderService.getOrders(orderSearchRequest)
        }
    }

    /**
     * Get a transaction receipt for a completed order. The [receipt][ReceiptResponse.receipt]
     * contains payment method-specific details.
     */
    @JvmStatic
    fun getOrderReceipt(orderId: String, resultCallback: GeideaResultCallback<ReceiptResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            receiptService.getOrderReceipt(orderId)
        }
    }
}