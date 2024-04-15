package net.geidea.paymentsdk.internal.ui.fragment.card

import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.*
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.service.AuthenticationV1Service
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.PaymentService
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.ReturnUrlParams
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.UserAgent
import net.geidea.paymentsdk.internal.ui.fragment.card.form.CardInputFragmentDirections
import net.geidea.paymentsdk.internal.ui.fragment.card.start.CardPaymentStartFragmentDirections
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.Logger.logi
import net.geidea.paymentsdk.internal.util.sdkCheckNotNull
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.PaymentMethod
import net.geidea.paymentsdk.model.auth.v1.AuthenticationRequest
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.common.Source
import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.pay.PaymentRequest
import java.math.BigDecimal

@GeideaSdkInternal
internal open class CardPaymentViewModel3dsV1(
    // Dependencies
    paymentViewModel: PaymentViewModel,
    private val authenticationV1Service: AuthenticationV1Service,
    private val paymentService: PaymentService,
    private val cancellationService: CancellationService,
    // Runtime arguments
    merchantConfiguration: MerchantConfigurationResponse,
    downPaymentAmount: BigDecimal?,
    step: Step?,
) : BaseCardPaymentViewModel(
    paymentViewModel = paymentViewModel,
    paymentService = paymentService,
    cancellationService = cancellationService,
    merchantConfiguration = merchantConfiguration,
    downPaymentAmount = downPaymentAmount,
    step = step,
) {
    init {
        var initialPaymentData = paymentViewModel.initialPaymentData
        if (downPaymentAmount != null) {
            initialPaymentData = initialPaymentData.copy(amount = downPaymentAmount)
        }

        val merchantProvidedPayment = initialPaymentData.paymentMethod
        if (merchantProvidedPayment != null) {
            // Start 3DS authentication flow with the card data given by merchant's app
            finalPaymentData = initialPaymentData
            paymentViewModel.navigate(
                navDirections = CardPaymentStartFragmentDirections.gdActionGdCardflowstartfragmentToGdCardauthfragment(
                    finalPaymentData = finalPaymentData,
                    step = step
                ),
                navOptions = navOptions { popUpTo(R.id.gd_cardflowstartfragment) { inclusive = true } }
            )
        } else {
            paymentViewModel.navigate(
                navDirections = CardPaymentStartFragmentDirections.gdActionGdCardflowstartfragmentToGdCardinputfragment(
                    downPaymentAmount = downPaymentAmount,
                    step = step
                ),
                navOptions = navOptions { popUpTo(R.id.gd_cardflowstartfragment) { inclusive = true } }
            )
        }
    }

    override fun onPayerAuthenticationStarted(viewModelScope: CoroutineScope, userAgent: UserAgent) {
        viewModelScope.launch {
            finishOnCatch {
                val authResponse = authenticate3DSv1(
                    orderId = orderId,
                    payment = finalPaymentData.paymentMethod!!,
                    customerEmail = finalPaymentData.customerEmail,
                    billingAddress = finalPaymentData.billingAddress,
                    shippingAddress = finalPaymentData.shippingAddress,
                )
                if (authResponse.isSuccess) {
                    userAgent.loadHtml(authResponse.htmlBodyContent!!)
                }
            }
        }
    }

    private suspend fun authenticate3DSv1(
        orderId: String?,
        payment: PaymentMethod,
        customerEmail: String?,
        billingAddress: Address?,
        shippingAddress: Address?,
    ): AuthenticationResponse {
        return try {
            paymentViewModel.processing = true
            val paymentData = paymentViewModel.initialPaymentData
            val authRequest = AuthenticationRequest {
                this.orderId = orderId
                this.amount = downPaymentAmount ?: paymentData.amount
                this.currency = paymentData.currency
                this.paymentMethod = payment
                this.paymentOperation = paymentData.paymentOperation
                this.merchantReferenceId = paymentData.merchantReferenceId
                this.billingAddress = billingAddress ?: paymentData.billingAddress
                this.shippingAddress = shippingAddress ?: paymentData.shippingAddress
                this.customerEmail = customerEmail ?: paymentData.customerEmail
                this.callbackUrl = paymentData.callbackUrl
                this.returnUrl = ReturnUrlParams.RETURN_URL
                this.cardOnFile = paymentData.cardOnFile
                this.initiatedBy = paymentData.initiatedBy
                this.paymentIntentId = paymentData.paymentIntentId
                this.agreementId = paymentData.agreementId
                this.agreementType = paymentData.agreementType
                this.source = Source.MOBILE_APP
                this.paymentMethods = paymentViewModel.acceptedCardBrandNames
                this.restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null
            }
            val authResponse = authenticationV1Service.postAuthenticate(authRequest)
            this@CardPaymentViewModel3dsV1.orderId = authResponse.orderId

            if (authResponse.isSuccess) {
                sdkCheckNotNull(authResponse.threeDSecureId, ERR_AUTH_V1_SUCCESS_BUT_NULL_THREEDSECUREID)
                sdkCheckNotNull(authResponse.htmlBodyContent, ERR_AUTH_V1_SUCCESS_BUT_NULL_HTMLBODYCONTENT)
                threeDSecureId = authResponse.threeDSecureId
            } else {
                handleFailureResponse(authResponse, authResponse.orderId)
            }
            authResponse

        } finally {
            paymentViewModel.processing = false
        }
    }

    override fun onPayButtonClicked(finalPaymentData: PaymentData) {
        this.finalPaymentData = finalPaymentData
        navigate(CardInputFragmentDirections.gdActionGdCardinputfragmentToGdCardauthfragment(
            finalPaymentData = finalPaymentData,
            step = step,
        ))
    }

    override fun handleSuccessReturnUrl(
        orderId: String,
        sessionId: String?,
        urlParams: ReturnUrlParams
    ) {
        logi("handleSuccessReturnUrl($orderId)")
        pay(orderId, this.threeDSecureId!!, sessionId, ::handlePaySuccess, ::handleFailureResponse)
    }

    open fun pay(
        orderId: String,
        threeDSecureId: String,
        sessionId: String?,
        onSuccess: (OrderResponse) -> Unit,
        onFailure: (failureResponse: GeideaResponse, orderId: String?) -> Unit
    ) {
        viewModelScope.launch {
            finishOnCatch {
                paymentViewModel.processing = true

                val orderResponse: OrderResponse = try {
                    val paymentRequest = PaymentRequest {
                        this.orderId = orderId
                        this.threeDSecureId = threeDSecureId
                        this.sessionId =sessionId
                        paymentMethod = sdkCheckNotNull(finalPaymentData.paymentMethod, ERR_PAY_WITH_PAYMENT_METHOD_NULL)
                        source = Source.MOBILE_APP
                    }
                    paymentService.postPay(paymentRequest)
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

    protected fun handlePaySuccess(orderResponse: OrderResponse) {
        sdkCheckNotNull(orderResponse.order, ERR_PAY_V1_SUCCESS_BUT_NULL_ORDER)
        end3dsSession()
        paymentViewModel.onPaymentFinished(GeideaResult.Success(orderResponse.order))
    }

    override fun getInputFormDestinationId(): Int
        = if (paymentViewModel.initialPaymentData.paymentMethod == null) R.id.gd_cardinputfragment else 0
}