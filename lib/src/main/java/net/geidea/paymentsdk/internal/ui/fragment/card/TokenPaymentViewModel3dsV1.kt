package net.geidea.paymentsdk.internal.ui.fragment.card

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent.tokenPaymentService
import net.geidea.paymentsdk.internal.service.AuthenticationV1Service
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.PaymentService
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.common.Source
import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.pay.TokenPaymentRequest


internal open class TokenPaymentViewModel3dsV1(
    // Dependencies
    paymentViewModel: PaymentViewModel,
    paymentService: PaymentService,
    authenticationV1Service: AuthenticationV1Service,
    cancellationService: CancellationService,
    // Runtime arguments
    merchantConfiguration: MerchantConfigurationResponse,
) : CardPaymentViewModel3dsV1(
    paymentViewModel = paymentViewModel,
    authenticationV1Service = authenticationV1Service,
    paymentService = paymentService,
    cancellationService = cancellationService,
    merchantConfiguration = merchantConfiguration,
    downPaymentAmount = null,
    step = null,
) {
    protected val initialPaymentData: PaymentData = paymentViewModel.initialPaymentData

    private val tokenId: String = requireNotNull(initialPaymentData.tokenId) { "Missing or null tokenId" }

    open fun start() {
        payWithToken(
            cvv = null,
            threeDSecureId = null,
            orderId = null,
            onSuccess = ::handlePaySuccess,
            onFailure = ::handleFailureResponse
        )
    }

    internal fun payWithToken(
        cvv: String?,
        orderId: String?,
        threeDSecureId: String?
    ) {
        payWithToken(
            cvv = cvv,
            orderId = orderId,
            threeDSecureId = threeDSecureId,
            this::handlePaySuccess,
            this::handleFailureResponse
        )
    }

    protected fun payWithToken(
        cvv: String?,
        orderId: String?,
        threeDSecureId: String?,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (failureResponse: GeideaResponse, orderId: String?) -> Unit
    ) {
        viewModelScope.launch {
            finishOnCatch {
                paymentViewModel.processing = true

                initialPaymentData.orderItems?.forEach { item -> item.currency = initialPaymentData.currency }

                val orderResponse: OrderResponse = try {
                    val paymentRequest = TokenPaymentRequest {
                        this.cvv = cvv
                        this.orderId = orderId
                        this.threeDSecureId = threeDSecureId
                        this.amount = initialPaymentData.amount
                        this.currency = initialPaymentData.currency
                        this.tokenId = this@TokenPaymentViewModel3dsV1.tokenId
                        this.callbackUrl = initialPaymentData.callbackUrl
                        this.agreementId = initialPaymentData.agreementId
                        this.billingAddress = initialPaymentData.billingAddress
                        this.shippingAddress = initialPaymentData.shippingAddress
                        this.customerEmail = initialPaymentData.customerEmail
                        this.initiatedBy = initialPaymentData.initiatedBy
                        this.merchantReferenceId = initialPaymentData.merchantReferenceId
                        this.paymentOperation = initialPaymentData.paymentOperation
                        this.items = initialPaymentData.orderItems
                        this.source = Source.MOBILE_APP
                    }
                    tokenPaymentService.postPayWithToken(paymentRequest)
                } finally {
                    paymentViewModel.processing = false
                }

                if (orderResponse.isSuccess) {
                    onSuccess(orderResponse)
                } else {
                    onFailure(orderResponse, orderResponse.order?.orderId)
                }
            }
        }
    }

    // 3DS v1 token payment do not require user CVV input
    override fun getInputFormDestinationId(): Int = 0
}