package net.geidea.paymentsdk.internal.ui.fragment.card

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.service.AuthenticationV1Service
import net.geidea.paymentsdk.internal.service.AuthenticationV4Service
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.PaymentService
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.DeviceInfo
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.ReturnUrlParams
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.UserAgent
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.auth.GatewayDecision
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v4.AuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v4.DeviceIdentificationRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationResponse
import net.geidea.paymentsdk.model.common.Source
import java.math.BigDecimal


internal class CardPaymentViewModel3dsV2(
    // Dependencies
    paymentViewModel: PaymentViewModel,
    authenticationV1Service: AuthenticationV1Service,
    private val authenticationV4Service: AuthenticationV4Service,
    paymentService: PaymentService,
    cancellationService: CancellationService,
    // Runtime arguments
    merchantConfiguration: MerchantConfigurationResponse,
    downPaymentAmount: BigDecimal?,
    step: Step?,
) : CardPaymentViewModel3dsV1(
    paymentViewModel = paymentViewModel,
    paymentService = paymentService,
    authenticationV1Service = authenticationV1Service,
    cancellationService = cancellationService,
    merchantConfiguration = merchantConfiguration,
    downPaymentAmount = downPaymentAmount,
    step = step,
) {
    internal var initiateAuthResponse: InitiateAuthenticationResponse? = null
        private set

    internal fun onCardNumberEntered(cardNumber: String, paymentData: PaymentData, userAgent: UserAgent) {
        val scope = paymentViewModel.viewModelScope
        scope.launch {
            finishOnCatch {
                initiateAuthentication3dsV2(
                    cardNumber,
                    paymentData,
                    userAgent = userAgent,
                    showProgress = false,
                )
            }
        }
    }

    override fun end3dsSession() {
        super.end3dsSession()
        initiateAuthResponse = null
    }

    override fun onPayerAuthenticationStarted(viewModelScope: CoroutineScope, userAgent: UserAgent) {
        viewModelScope.launch {
            finishOnCatch {
                authenticate3DSv2(userAgent)
            }
        }
    }

    private suspend fun authenticate3DSv2(userAgent: UserAgent) {
        // If authentication is not yet initiated make sure to do it before payer auth
        val initiateAuthResponse = if (this.initiateAuthResponse == null || this.initiateAuthResponse?.isSuccess != true) {
            val response = initiateAuthentication3dsV2(
                finalPaymentData.paymentMethod!!.cardNumber!!,
                finalPaymentData,
                userAgent,
                showProgress = true
            )
            if (!response.isSuccess) {
                handleFailureResponse(response, response.orderId)

                return
            }

            response
        } else {
            this.initiateAuthResponse!!
        }

        when (initiateAuthResponse.gatewayDecision) {
            GatewayDecision.ContinueToPayer -> {
                val authPayerResponse = authenticatePayer(userAgent.deviceInfo)
                if (!authPayerResponse.isSuccess) {
                    handleFailureResponse(authPayerResponse, authPayerResponse.orderId)
                    return
                }

                threeDSecureId = authPayerResponse.threeDSecureId
                authPayerResponse.htmlBodyContent?.let(userAgent::loadHtml)

                if (authPayerResponse.gatewayDecision == GatewayDecision.Reject) {
                    handleFailureResponse(authPayerResponse, initiateAuthResponse.orderId)
                }
            }
            GatewayDecision.ContinueToPayWithNotEnrolledCard -> {
                pay(
                    orderId = initiateAuthResponse.orderId!!,
                    threeDSecureId = initiateAuthResponse.threeDSecureId!!,
                    ::handlePaySuccess,
                    ::handleFailureResponse,
                )
            }
            GatewayDecision.Reject -> {
                handleFailureResponse(initiateAuthResponse, initiateAuthResponse.orderId)
            }
        }
    }

    private suspend fun initiateAuthentication3dsV2(
        cardNumber: String,
        paymentData: PaymentData,
        userAgent: UserAgent,
        showProgress: Boolean
    ): InitiateAuthenticationResponse {
        return try {
            if (showProgress) {
                paymentViewModel.processing = true
            }

            val initiateAuthenticationRequest = InitiateAuthenticationRequest {
                this.cardNumber = cardNumber
                this.orderId = this@CardPaymentViewModel3dsV2.orderId
                this.amount = paymentData.amount
                this.currency = paymentData.currency
                this.paymentOperation = paymentData.paymentOperation
                this.merchantReferenceId = paymentData.merchantReferenceId
                this.billingAddress = paymentData.billingAddress
                this.shippingAddress = paymentData.shippingAddress
                this.customerEmail = paymentData.customerEmail
                this.callbackUrl = paymentData.callbackUrl
                this.returnUrl = ReturnUrlParams.RETURN_URL
                this.cardOnFile = paymentData.cardOnFile
                this.initiatedBy = paymentData.initiatedBy
                this.paymentIntentId = paymentData.paymentIntentId
                this.agreementId = paymentData.agreementId
                this.agreementType = paymentData.agreementType
                this.source = Source.MOBILE_APP
                this.device = makeDeviceIdentificationRequest(userAgent.deviceInfo)
                this.paymentMethods = paymentViewModel.acceptedCardBrandNames
                this.restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null
            }
            val response: InitiateAuthenticationResponse =
                authenticationV4Service.postInitiateAuthentication(initiateAuthenticationRequest)
            initiateAuthResponse = response
            orderId = response.orderId
            threeDSecureId = response.threeDSecureId

            if (response.isSuccess) {
                response.redirectHtml?.let(userAgent::loadHtml)
            }

            response
        } finally {
            if (showProgress) {
                paymentViewModel.processing = false
            }
        }
    }

    internal val isInitiateAuthenticationSuccessful: Boolean get() = initiateAuthResponse?.isSuccess ?: false

    private suspend fun authenticatePayer(deviceInfo: DeviceInfo): AuthenticationResponse {
        return try {
            paymentViewModel.processing = true
            val authenticatePayerRequest = AuthenticatePayerRequest {
                this.paymentMethod = finalPaymentData.paymentMethod
                this.billingAddress = finalPaymentData.billingAddress
                this.shippingAddress = finalPaymentData.shippingAddress
                this.customerEmail = finalPaymentData.customerEmail
                this.javaScriptEnabled = true
                this.timeZone = 273

                this.orderId = this@CardPaymentViewModel3dsV2.orderId
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
                this.device = makeDeviceIdentificationRequest(deviceInfo)
                this.paymentMethods = paymentViewModel.acceptedCardBrandNames
                this.restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null
            }
            authenticationV4Service.postAuthenticatePayer(authenticatePayerRequest)
        } finally {
            paymentViewModel.processing = false
        }
    }

    private fun makeDeviceIdentificationRequest(deviceInfo: DeviceInfo): DeviceIdentificationRequest {
        return DeviceIdentificationRequest {
            this.browser = deviceInfo.browser
            this.language = deviceInfo.language
            this.colorDepth = deviceInfo.colorDepth
            this.acceptHeaders = deviceInfo.acceptHeaders
            this.javaEnabled = deviceInfo.javaEnabled
            this.javaScriptEnabled = deviceInfo.javascriptEnabled
            this.screenWidth = deviceInfo.screenWidth
            this.screenHeight = deviceInfo.screenHeight
            this.timezoneOffset = deviceInfo.timezoneOffset
            this.threeDSecureChallengeWindowSize = deviceInfo.threeDSecureChallengeWindowSize
        }
    }
}