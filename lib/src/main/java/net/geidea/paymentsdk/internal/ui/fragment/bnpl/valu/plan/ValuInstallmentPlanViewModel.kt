package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.bnpl.valu.ValuService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.ValuSharedViewModel
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.internal.util.emptyText
import net.geidea.paymentsdk.internal.util.formatAmount
import net.geidea.paymentsdk.internal.util.templateText
import net.geidea.paymentsdk.model.bnpl.valu.*
import net.geidea.paymentsdk.model.bnpl.valu.SelectInstallmentPlanResponse.Companion.NextStepProceedWithBnpl
import net.geidea.paymentsdk.model.bnpl.valu.SelectInstallmentPlanResponse.Companion.NextStepProceedWithDownPayment
import net.geidea.paymentsdk.model.common.Source
import net.geidea.paymentsdk.model.error.ErrorCodes
import java.math.BigDecimal

@GeideaSdkInternal
internal class ValuInstallmentPlanViewModel(
        private val paymentViewModel: PaymentViewModel,
        private val valuService: ValuService,
        private val connectivity: NetworkConnectivity,
        private val valuSharedViewModel: ValuSharedViewModel,
        val phoneNumber: String,
        val step: Step,
) : BaseViewModel() {

    private val paymentData = paymentViewModel.initialPaymentData

    private val _downPaymentStateFlow = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    //val downPaymentStateFlow: StateFlow<BigDecimal> = _downPaymentStateFlow

    private val _giftCardAmountStateFlow = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    //val giftCardAmountStateFlow: StateFlow<BigDecimal> = _giftCardAmountStateFlow

    private val _campaignAmountStateFlow = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    //val campaignAmountStateFlow: StateFlow<BigDecimal> = _campaignAmountStateFlow

    private val _nextButtonClickFlow = MutableSharedFlow<Unit>()
    val nextButtonClickFlow: SharedFlow<Unit> = _nextButtonClickFlow

    private var cashOnDelivery: Boolean? = null

    fun onDownPaymentTextChanged(text: String) {
        viewModelScope.launch {
            _downPaymentStateFlow.emit(text.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }
    }

    fun onGiftCardAmountChanged(text: String) {
        viewModelScope.launch {
            _giftCardAmountStateFlow.emit(text.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }
    }

    fun onCampaignAmountChanged(text: String) {
        viewModelScope.launch {
            _campaignAmountStateFlow.emit(text.toBigDecimalOrNull() ?: BigDecimal.ZERO)
        }
    }

    private val _stateFlow = MutableStateFlow<ValuInstallmentPlanState>(ValuInstallmentPlanState.Initial)

    private val installmentPlansStateFlow = combine(
            _downPaymentStateFlow.debounce(INPUT_DEBOUNCE_TIMEOUT),
            _giftCardAmountStateFlow.debounce(INPUT_DEBOUNCE_TIMEOUT),
            _campaignAmountStateFlow.debounce(INPUT_DEBOUNCE_TIMEOUT)
    ) { downPaymentAmount, giftCardAmount, campaignAmount ->

        InstallmentPlansRequest.Builder()
                .setTotalAmount(paymentData.amount)
                .setCurrency(paymentData.currency)
                .setCustomerIdentifier(phoneNumber)
                .setDownPayment(downPaymentAmount)
                .setGiftCardAmount(giftCardAmount)
                .setCampaignAmount(campaignAmount)
                .build()
    }
            .flatMapLatest { request ->
                flow {
                    if (connectivity.isConnected) {
                        emit(ValuInstallmentPlanState.Loading)
                        val result: GeideaResult<InstallmentPlansResponse> = responseAsResult {
                            valuService.postInstallmentPlans(request)
                        }

                        if (result is GeideaResult.Error) {
                            paymentViewModel.setResult(result)
                        }

                        when (result) {
                            is GeideaResult.Success -> {
                                val stepCount = defineStepCount(installmentPlan = null)
                                emit(ValuInstallmentPlanState.Success(
                                    response = result.data,
                                    stepCount = stepCount,
                                    downPaymentOptionsVisible = false
                                ))
                            }
                            is GeideaResult.NetworkError -> {
                                if (result.responseCode == ErrorCodes.BnplErrorGroup.code) {
                                    when (result.detailedResponseCode) {
                                        ErrorCodes.BnplErrorGroup.SouhoolaTechnicalFailure,
                                        ErrorCodes.BnplErrorGroup.MerchantCredentialsNotFound,
                                        ErrorCodes.BnplErrorGroup.MerchantCredentialsNotConfiguredInTheGateway -> {
                                            paymentViewModel.navigateToReceiptOrFinish()
                                        }
                                        else -> {
                                            emit(ValuInstallmentPlanState.ProcessingError(result.detailedResponseMessage))
                                            showSnack(errorSnack(result))
                                        }
                                    }
                                } else {
                                    if (!result.errors.isNullOrEmpty()) {
                                        // Field validation error(s)
                                        emitValidationErrorsState(result.errors)
                                    } else {
                                        // Undefined network error
                                        emit(ValuInstallmentPlanState.ProcessingError(result.detailedResponseMessage))
                                        showSnack(errorSnack(result))
                                    }
                                }
                            }
                            is GeideaResult.Error -> {
                                paymentViewModel.setResult(result)
                                emit(ValuInstallmentPlanState.ProcessingError("SDK error"))
                            }
                            is GeideaResult.Cancelled -> {
                                // Nothing to do
                            }
                        }

                    } else {
                        showSnack(noInternetSnack)
                    }
                }
            }

    private fun defineStepCount(installmentPlan: InstallmentPlan?): Int {
        return if (_downPaymentStateFlow.value > BigDecimal.ZERO ||
                installmentPlan?.adminFees != null && installmentPlan.adminFees > BigDecimal.ZERO) {
            4   // 1:Phone, 2:Plan, 3:Select payment method and Pay, 4:OTP
        } else {
            3   // 1:Phone, 2:Plan, 3:OTP
        }
    }

    val stateLiveData: LiveData<ValuInstallmentPlanState> = _stateFlow.asLiveData()

    val financedAmountLiveData: LiveData<BigDecimal> = _stateFlow
            .map {
                if (it is ValuInstallmentPlanState.Success) {
                    it.response.financedAmount
                } else {
                    BigDecimal.ZERO
                }
            }
            .asLiveData()

    init {
        viewModelScope.launch {
            installmentPlansStateFlow.collect { _stateFlow.value = it }
        }
    }

    fun onInstallmentPlanSelected(selectedPlan: InstallmentPlan?) {
        val state = _stateFlow.value
        if (state is ValuInstallmentPlanState.Success) {
            viewModelScope.launch {
                val amountAndCurrencyText: CharSequence? = if (selectedPlan != null) {
                    formatAmount(
                        selectedPlan.downPayment + selectedPlan.adminFees,
                        paymentViewModel.initialPaymentData.currency
                    )
                } else null

                val showDownPaymentOptions = selectedPlan?.let(::shouldShowDownPaymentOptions) ?: false
                _stateFlow.emit(state.copy(
                        selectedInstallmentPlan = selectedPlan,
                        nextButtonEnabled = selectedPlan != null
                                && if (showDownPaymentOptions) { cashOnDelivery != null } else true,
                        downPaymentOptionsVisible = showDownPaymentOptions,
                        downPaymentNowText = if (amountAndCurrencyText != null) {
                            templateText(R.string.gd_bnpl_pay_now, amountAndCurrencyText)
                        } else {
                            emptyText()
                        },
                        downPaymentOnDeliveryText = if (amountAndCurrencyText != null) {
                            templateText(R.string.gd_bnpl_pay_on_delivery, amountAndCurrencyText)
                        } else {
                            emptyText()
                        },
                        stepCount = defineStepCount(selectedPlan),
                ))
            }
        }
    }

    fun onNextButtonClicked() {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        val state = _stateFlow.value
        if (state is ValuInstallmentPlanState.Success) {
            val selectedInstallmentPlan = state.selectedInstallmentPlan
                    ?: return
            val response = (stateLiveData.value as ValuInstallmentPlanState.Success).response

            viewModelScope.launch {
                val result: GeideaResult<SelectInstallmentPlanResponse> = responseAsResult {
                    val selectPlanRequest = SelectInstallmentPlanRequest {
                        totalAmount = paymentData.amount
                        currency = paymentData.currency
                        orderId = paymentViewModel.orderId
                        merchantReferenceId = paymentData.merchantReferenceId
                        callbackUrl = paymentData.callbackUrl
                        billingAddress = paymentData.billingAddress
                        shippingAddress = paymentData.shippingAddress
                        customerEmail = paymentData.customerEmail
                        source = Source.MOBILE_APP
                        paymentMethods = paymentViewModel.acceptedCardBrandNames
                        restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null
                        //platform = paymentData.platform
                        //statementDescriptor = statementDescriptor
                        customerIdentifier = phoneNumber
                        bnplOrderId = response.bnplOrderId!!
                        downPayment = _downPaymentStateFlow.value
                        giftCardAmount = response.giftCardAmount
                        campaignAmount = response.campaignAmount
                        adminFees = selectedInstallmentPlan.adminFees
                        tenure = selectedInstallmentPlan.tenorMonth
                        cashOnDelivery = this@ValuInstallmentPlanViewModel.cashOnDelivery
                    }
                    valuService.postSelectInstallmentPlan(selectPlanRequest)
                }

                if (result is GeideaResult.Error) {
                    paymentViewModel.setResult(result)
                }

                when (result) {
                    is GeideaResult.Success -> {
                        val selectPlanResponse = result.data
                        // Keep ValU purchase data as a state shared between screen ViewModels
                        paymentViewModel.orderId = selectPlanResponse.orderId
                        valuSharedViewModel.startSession(
                                customerIdentifier = phoneNumber,
                                installmentPlansResponse = state.response,
                                selectedInstallmentPlan = state.selectedInstallmentPlan
                        )

                        when (selectPlanResponse.nextStep) {
                            NextStepProceedWithBnpl -> {
                                // Without down payment - proceed to OTP screen
                                navigate(ValuInstallmentPlanFragmentDirections.gdActionGdValuinstallmentplanfragmentToGdValuotpfragment(
                                        step = Step(current = step.current + 1, stepCount = state.stepCount)
                                ))
                            }
                            NextStepProceedWithDownPayment -> {
                                // With down payment - proceed to payment options screen
                                navigate(
                                        ValuInstallmentPlanFragmentDirections.gdActionGdValuinstallmentplanfragmentToGdPaymentoptionsfragment(
                                            downPaymentAmount = selectedInstallmentPlan.downPayment + selectedInstallmentPlan.adminFees,
                                            step = Step(
                                                    current = step.current + 1,
                                                    stepCount = state.stepCount,
                                                    textResId = R.string.gd_bnpl_process_down_payment
                                            )
                                        )
                                )
                            }
                            else -> {
                                // Must not reach here
                            }
                        }
                    }
                    is GeideaResult.NetworkError -> {
                        if (!result.errors.isNullOrEmpty()) {
                            emitValidationErrorsState(result.errors)
                        } else {
                            showSnack(errorSnack(result))
                            _stateFlow.value = ValuInstallmentPlanState.ProcessingError(result.detailedResponseMessage)
                        }
                    }
                    is GeideaResult.Error -> {
                        showSnack(errorSnack(result))
                        _stateFlow.value = ValuInstallmentPlanState.ProcessingError("SDK error")
                    }
                    is GeideaResult.Cancelled -> {
                        // Must not reach here
                    }
                }
            }
        }
    }

    private fun emitValidationErrorsState(errors: Map<String, List<String>>) {
        // empty map is a trick to simulate a change of the LiveData value.
        // It is necessary in some cases to force refresh.
        _stateFlow.value = ValuInstallmentPlanState.ValidationErrors(emptyMap())
        _stateFlow.value = ValuInstallmentPlanState.ValidationErrors(errors)
    }

    private fun shouldShowDownPaymentOptions(plan: InstallmentPlan): Boolean {
        return (paymentViewModel.merchantConfiguration.allowCashOnDeliveryValu ?: false)
                && (plan.downPayment + plan.adminFees) > BigDecimal.ZERO
    }

    internal fun onCashOnDeliverySelected(cashOnDelivery: Boolean) {
        this.cashOnDelivery = cashOnDelivery
        val currentState = _stateFlow.value
        if (currentState is ValuInstallmentPlanState.Success) {
            _stateFlow.value = currentState.copy(
                nextButtonEnabled = currentState.selectedInstallmentPlan != null
            )
        }
    }
}

private const val INPUT_DEBOUNCE_TIMEOUT = 300L  // milliseconds