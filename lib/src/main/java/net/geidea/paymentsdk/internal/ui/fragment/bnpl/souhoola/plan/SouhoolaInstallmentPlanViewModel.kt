package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.plan

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.bnpl.souhoola.SouhoolaService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.SouhoolaSharedViewModel
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.bnpl.souhoola.InstallmentPlan
import net.geidea.paymentsdk.model.bnpl.souhoola.InstallmentPlansRequest
import net.geidea.paymentsdk.model.bnpl.souhoola.InstallmentPlansResponse
import net.geidea.paymentsdk.model.error.ErrorCodes
import net.geidea.paymentsdk.model.error.getReason
import java.math.BigDecimal

@OptIn(FlowPreview::class)
@GeideaSdkInternal
internal class SouhoolaInstallmentPlanViewModel(
    private val paymentViewModel: PaymentViewModel,
    private val souhoolaService: SouhoolaService,
    private val connectivity: NetworkConnectivity,
    private val souhoolaSharedViewModel: SouhoolaSharedViewModel,
) : BaseViewModel() {

    val paymentData = paymentViewModel.initialPaymentData

    private val _downPaymentStateFlow = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)

    private var isDownPaymentTouched = false

    fun onDownPaymentTextChanged(text: String) {
        isDownPaymentTouched = true
        viewModelScope.launch {
            _downPaymentStateFlow.emit(text.toBigDecimalOrNull().orZero())
        }
    }

    private val _stateFlow = MutableStateFlow(SouhoolaInstallmentPlanState.initial(paymentData.amount))
    val stateLiveData: LiveData<SouhoolaInstallmentPlanState> = _stateFlow.asLiveData()

    // Once a plan is selected for first time, the Down Payment field is pre-populated with
    // the plan's minDownPayment.
    private val _minDownPaymentLiveData = MutableLiveEvent<BigDecimal>()
    val minDownPaymentLiveData: LiveEvent<BigDecimal> = _minDownPaymentLiveData

    private val souhoolaMinimumAmount = paymentViewModel.merchantConfiguration.souhoolaMinimumAmount.orZero()

    init {
        viewModelScope.launch {
            _downPaymentStateFlow
                .debounce(INPUT_DEBOUNCE_TIMEOUT)
                .map { downPaymentAmount ->
                    emitState { recalculate(downPayment = downPaymentAmount) }


                    val request = createInstallmentPlansRequest(downPaymentAmount)

                    if (connectivity.isConnected) {
                        emitState { loading() }

                        val result: GeideaResult<InstallmentPlansResponse> = responseAsResult {
                            souhoolaService.postInstallmentPlans(request)
                        }

                        if (result is GeideaResult.Error) {
                            paymentViewModel.setResult(result)
                        }

                        when (result) {
                            is GeideaResult.Success -> {
                                emitState { plansLoaded(installmentPlans = result.data.installmentPlans) }

                                val oldSelectedPlan = _stateFlow.value.selectedInstallmentPlan
                                if (oldSelectedPlan != null) {

                                    // Find the same plan but with recalculated installment amount
                                    val newSelectedPlan = result.data.installmentPlans.first {
                                        it.tenorMonth == oldSelectedPlan.tenorMonth
                                    }

                                    selectPlan(newSelectedPlan)
                                }
                            }
                            is GeideaResult.NetworkError -> {
                                if (result.responseCode == ErrorCodes.BnplErrorGroup.code) {
                                    if (result.detailedResponseCode == ErrorCodes.BnplErrorGroup.SouhoolaTechnicalFailure ||
                                        result.detailedResponseCode == ErrorCodes.BnplErrorGroup.DownPaymentLimitsViolated
                                    ) {
                                        emitState { error(getReason(result)) }
                                        showSnack(errorSnack(result))
                                    } else {
                                        paymentViewModel.navigateToReceiptOrFinish()
                                    }
                                } else {
                                    val downPaymentValidationErrors: List<String> =
                                        result.errors?.get("DownPayment").orEmpty()
                                    if (downPaymentValidationErrors.isNotEmpty()) {
                                        emitState {
                                            downPaymentOutOfRange(
                                                downPaymentError = plainText(
                                                    downPaymentValidationErrors.first()
                                                )
                                            )
                                        }
                                    } else {
                                        // Undefined network error
                                        paymentViewModel.navigateToReceiptOrFinish()
                                    }
                                }
                            }
                            is GeideaResult.Error -> {
                                paymentViewModel.navigateToReceiptOrFinish()
                            }
                            is GeideaResult.Cancelled -> {
                                // Nothing to do
                            }
                        }
                    } else {
                        showSnack(noInternetSnack)
                    }

                    if (_stateFlow.value.financedAmount < souhoolaMinimumAmount) {
                        emitState { financedAmountOutOfRange(
                            financedAmountError = templateText(
                                R.string.gd_souhoola_financed_amount_below_limit,
                                formatAmount(souhoolaMinimumAmount, currency = paymentData.currency),
                            )
                        )}
                    }
                }
                .collect {}
        }
    }

    private fun createInstallmentPlansRequest(downPaymentAmount: BigDecimal): InstallmentPlansRequest =
        InstallmentPlansRequest.Builder()
            .setTotalAmount(paymentData.amount)
            .setCurrency(paymentData.currency)
            .setCustomerIdentifier(souhoolaSharedViewModel.customerIdentifier)
            .setCustomerPin(souhoolaSharedViewModel.customerPin)
            .setDownPayment(downPaymentAmount)
            .build()

    private suspend fun emitState(mutate: SouhoolaInstallmentPlanState.() -> SouhoolaInstallmentPlanState) {
        val oldState = _stateFlow.value
        val newState = oldState.mutate()
        _stateFlow.emit(newState)
    }

    fun onInstallmentPlanSelected(selectedPlan: InstallmentPlan?) {
        if (selectedPlan == null) {
            return
        }

        viewModelScope.launch {
            selectPlan(selectedPlan)
        }
    }

    fun onNextButtonClicked() {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        val state = _stateFlow.value
        if (state.internalState == InternalState.Selected) {
            val nextStep = Step(
                isBackButtonVisible = true,
                current = 3,
                stepCount = state.stepCount,
            )
            navigate(
                SouhoolaInstallmentPlanFragmentDirections.gdActionGdSouhoolainstallmentplanfragmentToGdSouhoolareviewfragment(
                    nextStep
                )
            )
        }
    }

    private suspend fun selectPlan(plan: InstallmentPlan) {
        souhoolaSharedViewModel.selectedInstallmentPlan = plan

        val downPaymentRange = plan.calculateDownPaymentRange(paymentData.amount)

        emitState {
            planSelected(
                selectedInstallmentPlan = plan,
                downPaymentHelperText = plan
                    .calculateDownPaymentRange(paymentData.amount)
                    .toHelperText(),
            )
        }

        if (!isDownPaymentTouched) {
            //_minDownPaymentLiveData.value = Event(selectedPlan.minDownPayment)    // Backend returns zero always
            _minDownPaymentLiveData.value = Event(min(downPaymentRange.start, downPaymentRange.endInclusive))
            emitState { recalculate(downPayment = plan.downPayment) }
        } else {
            if (plan.downPayment in downPaymentRange) {
                emitState { clearDownPaymentError() }
                emitState { recalculate(downPayment = plan.downPayment) }
            } else {
                emitState { downPaymentOutOfRange(downPaymentError = downPaymentRange.toHelperText()) }
            }
        }
    }

    private fun InstallmentPlan.calculateDownPaymentRange(totalAmount: BigDecimal): ClosedRange<BigDecimal> {
        val availableLimit = souhoolaSharedViewModel.verifyResponse?.availableLimit.orZero()
        val a: BigDecimal = max(minDownPayment, totalAmount - availableLimit)
        val b = totalAmount - max(BigDecimal.ZERO, paymentViewModel.merchantConfiguration.souhoolaMinimumAmount.or(1_000))

        return a..b
    }

    private fun ClosedRange<BigDecimal>.toHelperText() =
        NativeText.Template.of(
            R.string.gd_souhoola_down_payment_helper_text,
            formatAmount(start, currency = paymentData.currency),
            formatAmount(endInclusive, currency = paymentData.currency)
        )
}

private const val INPUT_DEBOUNCE_TIMEOUT = 300L  // milliseconds