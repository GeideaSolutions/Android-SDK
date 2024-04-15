package net.geidea.paymentsdk.internal.ui.fragment.card

import androidx.navigation.navOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.service.AuthenticationV1Service
import net.geidea.paymentsdk.internal.service.AuthenticationV3Service
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.PaymentService
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.ReturnUrlParams
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.UserAgent
import net.geidea.paymentsdk.internal.ui.fragment.card.cvv.CvvInputFragmentDirections
import net.geidea.paymentsdk.internal.ui.fragment.card.start.CardPaymentStartFragmentDirections
import net.geidea.paymentsdk.internal.util.Logger
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.auth.GatewayDecision
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v3.TokenAuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v3.TokenInitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationResponse
import net.geidea.paymentsdk.model.common.Source


internal class TokenPaymentViewModel3dsV2(
    // Dependencies
    paymentViewModel: PaymentViewModel,
    authenticationV1Service: AuthenticationV1Service,
    private val authenticationV3Service: AuthenticationV3Service,
    paymentService: PaymentService,
    cancellationService: CancellationService,
    // Runtime arguments
    merchantConfiguration: MerchantConfigurationResponse,
    initialPaymentData: PaymentData,
) : TokenPaymentViewModel3dsV1(
    paymentViewModel = paymentViewModel,
    authenticationV1Service = authenticationV1Service,
    paymentService = paymentService,
    cancellationService = cancellationService,
    merchantConfiguration = merchantConfiguration,
) {
    private val tokenId: String = requireNotNull(initialPaymentData.tokenId) { "Missing or null tokenId" }

    private var cvv: String? = null

    override fun start() {
        when {
             isCvvRequired() -> {

                // CVV screen

                paymentViewModel.navigate(
                    navDirections = CardPaymentStartFragmentDirections.gdActionGdCardflowstartfragmentToGdCvvinputfragment(
                        tokenId = tokenId
                    ),
                    navOptions = navOptions { popUpTo(R.id.gd_cardflowstartfragment) { inclusive = true } }
                )
            }
            is3dsAuthenticationRequired() -> {

                finalPaymentData = initialPaymentData

                // Authentication screen
                paymentViewModel.navigate(
                    navDirections = CardPaymentStartFragmentDirections.gdActionGdCardflowstartfragmentToGdCardauthfragment(
                        cvv = null,   // TODO cvv could come from merchant as PaymentData property
                        finalPaymentData = initialPaymentData
                    ),
                    navOptions = navOptions { popUpTo(R.id.gd_cardflowstartfragment) { inclusive = true } }
                )
            } else -> {

                finalPaymentData = initialPaymentData

                // CVV not required, authentication not required, directly pay
                payWithToken(
                    cvv = null,
                    orderId = null,
                    threeDSecureId = threeDSecureId,
                    onSuccess = ::handlePaySuccess,
                    onFailure = ::handleFailureResponse
                )
            }
        }
    }

    fun onCvvEntered(cvv: String) {
        finalPaymentData = initialPaymentData
        this.cvv = cvv

        if (is3dsAuthenticationRequired()) {

            // Authentication screen

            paymentViewModel.navigate(
                navDirections = CvvInputFragmentDirections.gdActionGdCvvinputfragmentToGdCardauthfragment(
                    finalPaymentData = initialPaymentData,
                    cvv = cvv,
                    step = step,
                ),
                navOptions = navOptions { popUpTo(R.id.gd_cardflowstartfragment) { inclusive = true } }
            )
        } else {
            // Authentication not required

            payWithToken(
                cvv = cvv,
                orderId = null,
                threeDSecureId = threeDSecureId,
                onSuccess = ::handlePaySuccess,
                onFailure = ::handleFailureResponse
            )
        }
    }

    override fun onPayerAuthenticationStarted(viewModelScope: CoroutineScope, userAgent: UserAgent) {
        viewModelScope.launch {
            finishOnCatch {
                authenticate3DSv2(userAgent)
            }
        }
    }

    override fun getInputFormDestinationId(): Int = if (isCvvRequired()) R.id.gd_cvvinputfragment else 0

    private fun isCvvRequired() = paymentViewModel.merchantConfiguration.isCvvRequiredForTokenPayments == true
            || paymentViewModel.merchantConfiguration.merchantCountryTwoLetterCode.equals("EG", ignoreCase = true)

    private fun is3dsAuthenticationRequired() = paymentViewModel.merchantConfiguration.is3dsRequiredForTokenPayments == true

    private suspend fun authenticate3DSv2(userAgent: UserAgent) {
        val initAuthResponse = initiateAuthentication3DSv2()
        if (initAuthResponse.isSuccess) {
            orderId = initAuthResponse.orderId
            threeDSecureId = initAuthResponse.threeDSecureId
            initAuthResponse.redirectHtml?.let(userAgent::loadHtml)

            when (initAuthResponse.gatewayDecision) {
                GatewayDecision.ContinueToPayer -> {
                    val authPayerResponse = authenticatePayer(userAgent.deviceInfo.browser)
                    if (!authPayerResponse.isSuccess) {
                        handleFailureResponse(authPayerResponse, authPayerResponse.orderId)
                        return
                    }

                    threeDSecureId = authPayerResponse.threeDSecureId
                    authPayerResponse.htmlBodyContent?.let(userAgent::loadHtml)

                    if (authPayerResponse.gatewayDecision == GatewayDecision.Reject) {
                        handleFailureResponse(authPayerResponse, initAuthResponse.orderId)
                    }
                }
                GatewayDecision.ContinueToPayWithNotEnrolledCard -> {
                    payWithToken(
                        cvv = cvv,
                        orderId = initAuthResponse.orderId!!,
                        threeDSecureId = initAuthResponse.threeDSecureId!!
                    )
                }
                GatewayDecision.Reject -> {
                    handleFailureResponse(initAuthResponse, initAuthResponse.orderId)
                }
            }

        } else {
            handleFailureResponse(initAuthResponse, initAuthResponse.orderId)
            return
        }
    }

    private suspend fun initiateAuthentication3DSv2(): InitiateAuthenticationResponse {
        return try {
            paymentViewModel.processing = true

            val initiateAuthenticationRequest = TokenInitiateAuthenticationRequest {
                this.orderId = null
                this.amount = finalPaymentData.amount
                this.currency = finalPaymentData.currency
                this.tokenId = finalPaymentData.tokenId
                this.paymentOperation = finalPaymentData.paymentOperation
                this.merchantReferenceId = finalPaymentData.merchantReferenceId
                this.billingAddress = finalPaymentData.billingAddress
                this.shippingAddress = finalPaymentData.shippingAddress
                this.customerEmail = finalPaymentData.customerEmail
                this.callbackUrl = finalPaymentData.callbackUrl
                this.returnUrl = ReturnUrlParams.RETURN_URL
                this.cardOnFile = finalPaymentData.cardOnFile
                this.initiatedBy = finalPaymentData.initiatedBy
                this.paymentIntentId = finalPaymentData.paymentIntentId
                this.agreementId = finalPaymentData.agreementId
                this.agreementType = finalPaymentData.agreementType
                this.source = Source.MOBILE_APP
                this.paymentMethods = paymentViewModel.acceptedCardBrandNames
                this.restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null
            }
            val response: InitiateAuthenticationResponse =
                authenticationV3Service.postTokenInitiateAuthentication(initiateAuthenticationRequest)
            orderId = response.orderId
            threeDSecureId = response.threeDSecureId

            response

        } finally {
            paymentViewModel.processing = false
        }
    }

    private suspend fun authenticatePayer(userAgent: String): AuthenticationResponse {
        return try {
            paymentViewModel.processing = true
            val authenticatePayerRequest = TokenAuthenticatePayerRequest {
                this.tokenId = finalPaymentData.tokenId
                this.cvv = this@TokenPaymentViewModel3dsV2.cvv
                this.billingAddress = finalPaymentData.billingAddress
                this.shippingAddress = finalPaymentData.shippingAddress
                this.customerEmail = finalPaymentData.customerEmail
                this.browser = userAgent
                this.javaScriptEnabled = true
                this.timeZone = 273

                this.orderId = this@TokenPaymentViewModel3dsV2.orderId
                this.amount = finalPaymentData.amount
                this.currency = finalPaymentData.currency
                this.paymentOperation = finalPaymentData.paymentOperation
                this.merchantReferenceId = finalPaymentData.merchantReferenceId
                this.callbackUrl = finalPaymentData.callbackUrl
                this.returnUrl = ReturnUrlParams.RETURN_URL
                this.cardOnFile = finalPaymentData.cardOnFile
                this.initiatedBy = finalPaymentData.initiatedBy
                this.paymentIntentId = finalPaymentData.paymentIntentId
                this.agreementId = finalPaymentData.agreementId
                this.agreementType = finalPaymentData.agreementType
                this.source = Source.MOBILE_APP
                this.paymentMethods = paymentViewModel.acceptedCardBrandNames
                this.restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null
            }
            authenticationV3Service.postTokenAuthenticatePayer(authenticatePayerRequest)
        } finally {
            paymentViewModel.processing = false
        }
    }

    override fun handleSuccessReturnUrl(
        orderId: String,
        sessionId: String?,
        urlParams: ReturnUrlParams
    ) {
        Logger.logi("handleSuccessReturnUrl($orderId)")
        payWithToken(cvv, orderId, threeDSecureId!!, ::handlePaySuccess, ::handleFailureResponse)
    }
}