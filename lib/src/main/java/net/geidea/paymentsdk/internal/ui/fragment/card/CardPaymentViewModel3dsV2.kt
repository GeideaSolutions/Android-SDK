package net.geidea.paymentsdk.internal.ui.fragment.card

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.AuthenticationV1Service
import net.geidea.paymentsdk.internal.service.AuthenticationV6Service
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.internal.service.PaymentService
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.DeviceInfo
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.ReturnUrlParams
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.UserAgent
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.generateSignature
import net.geidea.paymentsdk.internal.util.getCurrentTimestamp
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.auth.GatewayDecision
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v6.AppearanceRequest
import net.geidea.paymentsdk.model.auth.v6.AuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v6.CreateSessionRequest
import net.geidea.paymentsdk.model.auth.v6.CreateSessionResponse
import net.geidea.paymentsdk.model.auth.v6.DeviceIdentificationRequest
import net.geidea.paymentsdk.model.auth.v6.InitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v6.InitiateAuthenticationResponse
import net.geidea.paymentsdk.model.auth.v6.StylesRequest
import net.geidea.paymentsdk.model.common.Source
import java.math.BigDecimal


internal class CardPaymentViewModel3dsV2(
    // Dependencies
    paymentViewModel: PaymentViewModel,
    authenticationV1Service: AuthenticationV1Service,
    private val authenticationV6Service: AuthenticationV6Service,
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
    internal var createSessionResponse: CreateSessionResponse? = null
        private set

    internal fun onCardNumberEntered(
        cardNumber: String,
        paymentData: PaymentData,
        userAgent: UserAgent
    ) {
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

    override fun onPayerAuthenticationStarted(
        viewModelScope: CoroutineScope,
        userAgent: UserAgent
    ) {
        viewModelScope.launch {
            finishOnCatch {
                authenticate3DSv2(userAgent)
            }
        }
    }

    private suspend fun authenticate3DSv2(userAgent: UserAgent) {
        // If authentication is not yet initiated make sure to do it before payer auth
        val initiateAuthResponse =
            if (this.initiateAuthResponse == null || this.initiateAuthResponse?.isSuccess != true) {
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
                    sessionId = createSessionResponse?.session?.id,
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

            createSessionResponse = createSession(paymentData)

            val initiateAuthenticationRequest = InitiateAuthenticationRequest {
                this.sessionId = createSessionResponse?.session?.id
                this.cardNumber = cardNumber
                this.returnUrl = ReturnUrlParams.RETURN_URL

                this.orderId = this@CardPaymentViewModel3dsV2.orderId
                this.paymentOperation = paymentData.paymentOperation
                this.callbackUrl = paymentData.callbackUrl
                this.cardOnFile = paymentData.cardOnFile
                this.source = Source.MOBILE_APP
                this.restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null
                this.deviceIdentificationRequest = makeDeviceIdentificationRequest(userAgent.deviceInfo)
            }
            val response: InitiateAuthenticationResponse =
                SdkComponent.authenticationV6Service.postInitiateAuthentication(initiateAuthenticationRequest)
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

    private suspend fun createSession(paymentData: PaymentData) : CreateSessionResponse {
        val timestamp = getCurrentTimestamp()
        val signature = generateSignature(publicKey = GeideaPaymentSdk.merchantKey,
            orderAmount = paymentData.amount,
            orderCurrency = paymentData.currency,
            merchantRefId = paymentData.merchantReferenceId,
            apiPass = GeideaPaymentSdk.merchantPassword,
            timestamp = timestamp)

        val initiateSessionRequest = CreateSessionRequest {
            this.amount = paymentData.amount
            this.currency = paymentData.currency
            this.timestamp = timestamp
            this.merchantReferenceId = paymentData.merchantReferenceId
            this.signature = signature
            this.callbackUrl = paymentData.callbackUrl
            this.paymentIntentId = paymentData.paymentIntentId
            this.paymentOperation = paymentData.paymentOperation
            this.cardOnFile = paymentData.cardOnFile == true
            this.appearanceRequest = AppearanceRequest {
                this.showEmail = paymentData.showCustomerEmail
                this.showAddress = paymentData.showAddress
                this.receiptPage = paymentData.showReceipt
                this.styles = StylesRequest {
                    this.hideGeideaLogo = false
                }
            }
        }
        val response = SdkComponent.sessionV2Service.createSession(initiateSessionRequest)
        if(response.isSuccess){
            sessionId = response.session?.id
        }
        return response
    }

    internal val isInitiateAuthenticationSuccessful: Boolean
        get() = initiateAuthResponse?.isSuccess ?: false

    private suspend fun authenticatePayer(deviceInfo: DeviceInfo): AuthenticationResponse {
        return try {
            paymentViewModel.processing = true
            val authenticatePayerRequest = AuthenticatePayerRequest {
                this.sessionId = createSessionResponse?.session?.id
                this.orderId = this@CardPaymentViewModel3dsV2.orderId
                this.callbackUrl = finalPaymentData.callbackUrl
                this.cardOnFile = finalPaymentData.cardOnFile
                this.paymentOperation = finalPaymentData.paymentOperation
                this.paymentMethod = finalPaymentData.paymentMethod
                this.deviceIdentification = makeDeviceIdentificationRequest(deviceInfo)
                this.restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null

                this.source = Source.MOBILE_APP
                this.timeZone = "273"
                this.javaScriptEnabled = true
            }
            authenticationV6Service.postAuthenticatePayer(authenticatePayerRequest)
        } finally {
            paymentViewModel.processing = false
        }
    }

    private fun makeDeviceIdentificationRequest(deviceInfo: DeviceInfo): DeviceIdentificationRequest {
        return DeviceIdentificationRequest {
            this.language = deviceInfo.language
            this.providerDeviceId = deviceInfo.providerDeviceId
            this.userAgent = deviceInfo.browser
        }
    }
}