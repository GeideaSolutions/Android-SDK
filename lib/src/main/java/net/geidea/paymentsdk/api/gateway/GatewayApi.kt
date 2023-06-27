package net.geidea.paymentsdk.api.gateway

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.*
import net.geidea.paymentsdk.internal.service.receipt.ReceiptService
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
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

public object GatewayApi {

    private val scope: CoroutineScope get() = SdkComponent.supervisorScope

    private val merchantsService: MerchantsService get() = SdkComponent.merchantsService
    private val authenticationV1Service: AuthenticationV1Service get() = SdkComponent.authenticationV1Service
    private val authenticationV3Service: AuthenticationV3Service get() = SdkComponent.authenticationV3Service
    private val authenticationV4Service: AuthenticationV4Service get() = SdkComponent.authenticationV4Service
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
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     */
    suspend fun getMerchantConfiguration(): GeideaResult<MerchantConfigurationResponse> {
        return responseAsResult(::asIs) {
            merchantsService.getMerchantConfiguration(GeideaPaymentSdk.merchantKey)
        }
    }

    /**
     * Perform 3DSv1 authentication of a payment. If successful a new order is created.
     *
     * On success a new order is created, new 3DS transaction
     * is opened and an [AuthenticationResponse.htmlBodyContent] is returned containing
     * authentication HTML body of the issuer bank. It is your (as a Merchant) responsibility to
     * open it in a web view or browser to allow the customer to authenticate.
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param authenticationRequest request containing order, customer and payment data
     */
    suspend fun authenticateV1(authenticationRequest: AuthenticationRequest): GeideaResult<AuthenticationResponse> {
        return responseAsResult(::asIs) {
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
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     */
    suspend fun initiateAuthenticationV4(initiateAuthenticationRequest: InitiateAuthenticationRequest): GeideaResult<InitiateAuthenticationResponse> {
        return responseAsResult(::asIs) {
            authenticationV4Service.postInitiateAuthentication(initiateAuthenticationRequest)
        }
    }

    /**
     * Perform payer authentication as part of the 3DSv2 payment flow.
     * This call should be next after a call to [initiateAuthenticationV4].
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     */
    suspend fun authenticatePayerV4(authenticatePayerRequest: AuthenticatePayerRequest): GeideaResult<AuthenticationResponse> {
        return responseAsResult(::asIs) {
            authenticationV4Service.postAuthenticatePayer(authenticatePayerRequest)
        }
    }

    /**
     * Perform a payment transaction for a given [order id][PaymentRequest.orderId].
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param paymentRequest request containing the order id and the payment data
     */
    suspend fun pay(paymentRequest: PaymentRequest): GeideaResult<Order> {
        return responseAsResult({ it.order!! }) {
            paymentService.postPay(paymentRequest)
        }
    }

    /**
     * Perform a payment transaction with [token Id][TokenPaymentRequest.tokenId].
     *
     * You might check if tokenization is currently enabled for you by checking
     * [MerchantConfigurationResponse.isTokenizationEnabled] flag returned by
     * [getMerchantConfiguration].
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param tokenPaymentRequest request containing the payment data
     */
    suspend fun payWithToken(tokenPaymentRequest: TokenPaymentRequest): GeideaResult<Order> {
        return responseAsResult({ it.order!! }) {
            tokenPaymentService.postPayWithToken(tokenPaymentRequest)
        }
    }

    /**
     * Captures the amount of an [Order] that is already authorized ([Order.detailedStatus] is
     * [OrderStatus.AUTHORIZED].
     *
     * To pre-authorize a payment set [PaymentData.paymentOperation]
     * (or [AuthenticationRequest.paymentOperation]) to
     * [PaymentOperation.PREAUTHORIZE][net.geidea.paymentsdk.model.transaction.PaymentOperation].
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param captureRequest request containing an order id
     */
    suspend fun captureOrder(captureRequest: CaptureRequest): GeideaResult<Order> {
        return responseAsResult({ it.order!! }) {
            captureService.postCapture(captureRequest)
        }
    }

    /**
     * Refund an order.
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param refundRequest request containing an order id
     */
    suspend fun refundOrder(refundRequest: RefundRequest): GeideaResult<Order> {
        return responseAsResult({ it.order!! }) {
            refundService.postRefund(refundRequest)
        }
    }

    /**
     * Cancel an order.
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param cancelRequest request containing an order id
     */
    suspend fun cancelOrder(cancelRequest: CancelRequest): GeideaResult<PaymentResponse> {
        return responseAsResult(::asIs) {
            cancellationService.postCancel(cancelRequest)
        }
    }

    /**
     * Get an existing order by id.
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param orderId the id
     */
    suspend fun getOrder(orderId: String): GeideaResult<Order> {
        return responseAsResult({ it.order!! }) {
            orderService.getOrder(orderId)
        }
    }

    /**
     * Get a list of orders matching given search filters.
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     *
     * @param orderSearchRequest search filters
     */
    suspend fun getOrders(orderSearchRequest: OrderSearchRequest): GeideaResult<OrderSearchResponse> {
        return responseAsResult(::asIs) {
            orderService.getOrders(orderSearchRequest)
        }
    }

    /**
     * Get a transaction receipt for a completed order. The [receipt][ReceiptResponse.receipt]
     * contains payment method-specific details.
     *
     * Note: This is Kotlin suspend function which performs any network operations on
     * [Dispatchers.IO] but can be called from any coroutine context.
     */
    suspend fun getOrderReceipt(orderId: String): GeideaResult<ReceiptResponse> {
        return responseAsResult(::asIs) {
            receiptService.getOrderReceipt(orderId)
        }
    }
}