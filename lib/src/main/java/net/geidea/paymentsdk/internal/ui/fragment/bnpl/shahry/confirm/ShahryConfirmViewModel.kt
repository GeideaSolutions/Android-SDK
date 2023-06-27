package net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.confirm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.bnpl.shahry.ShahryService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.confirm.ShahryInstallmentPlan.Companion.makeShahryPlan
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.bnpl.BnplDetails
import net.geidea.paymentsdk.model.bnpl.shahry.CashOnDeliveryRequest
import net.geidea.paymentsdk.model.bnpl.shahry.ConfirmRequest
import net.geidea.paymentsdk.model.bnpl.shahry.ConfirmResponse
import net.geidea.paymentsdk.model.error.ErrorCodes.BnplErrorGroup.InvalidOtp
import net.geidea.paymentsdk.model.error.getReason
import net.geidea.paymentsdk.model.transaction.TransactionType
import java.math.BigDecimal

@GeideaSdkInternal
internal class ShahryConfirmViewModel(
    private val shahryService: ShahryService,
    private val connectivity: NetworkConnectivity,
    private val paymentViewModel: PaymentViewModel,
    val merchantName: String,
    val customerIdentifier: String,
) : BaseViewModel() {

    val paymentData: PaymentData get() = paymentViewModel.initialPaymentData

    private var shahryInstallmentPlan: ShahryInstallmentPlan? = null
    private var cashOnDelivery: Boolean? = null

    private val _stateLiveData = MutableLiveData<ShahryConfirmState>(ShahryConfirmState.Initial)
    val stateLiveData: LiveData<ShahryConfirmState> = _stateLiveData

    init {
        shahryInstallmentPlan?.let {
            // Restore state
            _stateLiveData.value = makeStateFromPlan(it, paymentData.currency)
        }
    }

    fun onButtonClicked(orderToken: String) {
        when (val state = _stateLiveData.value) {
            ShahryConfirmState.Initial,
            is ShahryConfirmState.Error -> confirm(orderToken)
            is ShahryConfirmState.AwaitingToProceed -> {
                if (state.plan.requiresDownPayment && this.cashOnDelivery != true) {
                    navigateToDownPayment(state.plan)
                } else {
                    markCashOnDelivery()
                }
            }
            else -> { /* Must not reach here */
            }
        }
    }

    private fun confirm(orderToken: String) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = ShahryConfirmState.Confirming

            lateinit var bnplDetails: BnplDetails

            val confirmResult = responseAsResult {
                shahryService.postConfirm(
                    ConfirmRequest(
                        orderId = paymentViewModel.orderId,
                        orderToken = orderToken
                    )
                ).also { bnplDetails = getBnplDetails(it) }
            }

            when (confirmResult) {
                is GeideaResult.Success -> {
                    val orderResult = GeideaResult.Success(confirmResult.data.order!!)
                    paymentViewModel.setResult(orderResult)

                    shahryInstallmentPlan = makeShahryPlan(bnplDetails)

                    _stateLiveData.value = ShahryConfirmState.Confirmed
                    if (shahryInstallmentPlan!!.requiresDownPayment) {
                        _stateLiveData.value = makeStateFromPlan(shahryInstallmentPlan!!, paymentData.currency)
                    } else {
                        paymentViewModel.navigateToReceiptOrFinish()
                    }
                }
                is GeideaResult.NetworkError -> {
                    paymentViewModel.setResult(confirmResult)
                    if (confirmResult.detailedResponseCode == InvalidOtp) {
                        showSnack(errorSnack(confirmResult))
                        _stateLiveData.value = ShahryConfirmState.Error(NativeText.Empty)
                        _stateLiveData.value = ShahryConfirmState.Error(getReason(confirmResult))
                    } else {
                        paymentViewModel.navigateToReceiptOrFinish()
                    }
                }
                is GeideaResult.Error -> {
                    paymentViewModel.setResult(confirmResult)
                    showSnack(errorSnack(confirmResult))
                    _stateLiveData.value = ShahryConfirmState.Error(NativeText.Empty)
                    _stateLiveData.value = ShahryConfirmState.Error(getReason(confirmResult))
                }
                is GeideaResult.Cancelled -> {
                    // Nothing to do
                }
            }
        }
    }

    private fun navigateToDownPayment(shahryInstallmentPlan: ShahryInstallmentPlan) {
        navigate(
            ShahryConfirmFragmentDirections.gdActionGdConfirmfragmentToGdPaymentoptionsfragment(
                downPaymentAmount = shahryInstallmentPlan.totalUpfront,
                step = Step(
                    current = 3,
                    stepCount = 3,
                    textResId = R.string.gd_bnpl_process_down_payment
                )
            )
        )
    }

    internal fun makeStateFromPlan(plan: ShahryInstallmentPlan, currency: String): ShahryConfirmState {
        return if (plan.requiresDownPayment) {
            val amountAndCurrencyText: CharSequence = formatAmount(
                amount = plan.downPayment + plan.purchaseFees,
                currency = currency
            )

            val showDownPaymentOptions = shouldShowDownPaymentOptions(plan)

            return ShahryConfirmState.AwaitingToProceed(
                purchaseFeesText = makeAmountText(plan.purchaseFees, currency),
                downPaymentText = makeAmountText(plan.downPayment, currency),
                totalUpfrontText = makeAmountText(plan.totalUpfront, currency),
                downPaymentOptionsVisible = showDownPaymentOptions,
                downPaymentNowText = templateText(R.string.gd_bnpl_pay_now, amountAndCurrencyText),
                downPaymentOnDeliveryText = templateText(
                    R.string.gd_bnpl_pay_on_delivery,
                    amountAndCurrencyText
                ),
                buttonEnabled = if (showDownPaymentOptions) { cashOnDelivery != null } else true,
                buttonTitle = if (this.cashOnDelivery == true) {
                    NativeText.Resource(R.string.gd_shahry_btn_proceed)
                } else {
                    NativeText.Resource(R.string.gd_shahry_btn_proceed_to_down_payment)
                },
                plan = plan,
                stepCount = if (plan.requiresDownPayment && cashOnDelivery != true) 3 else 2
            )
        } else {
            ShahryConfirmState.Confirmed
        }
    }

    private fun markCashOnDelivery() {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = ShahryConfirmState.Confirming

            val result = responseAsResult {
                shahryService.postCashOnDelivery(CashOnDeliveryRequest(orderId = paymentViewModel.orderId))
            }

            when (result) {
                is GeideaResult.Success -> {
                    val orderResult = GeideaResult.Success(result.data.order!!)
                    paymentViewModel.setResult(orderResult)
                    paymentViewModel.navigateToReceiptOrFinish()
                }
                is GeideaResult.Error -> {
                    paymentViewModel.setResult(result)
                    paymentViewModel.navigateToReceiptOrFinish()
                }
                is GeideaResult.Cancelled -> {
                    // Nothing to do
                }
            }
        }
    }

    private fun shouldShowDownPaymentOptions(plan: ShahryInstallmentPlan): Boolean {
        return (paymentViewModel.merchantConfiguration.allowCashOnDeliveryShahry ?: false)
                && (plan.downPayment + plan.purchaseFees) > BigDecimal.ZERO
    }

    private fun getBnplDetails(response: ConfirmResponse): BnplDetails {
        val bnplDetails = response.order
            ?.transactions
            ?.firstOrNull { it.type == TransactionType.INSTALLMENT }
            ?.bnplDetails

        return requireNotNull(bnplDetails) { "Failed to find Installment transaction with bnplDetails in order ${paymentViewModel.orderId}" }
    }

    fun onCashOnDeliverySelected(cashOnDelivery: Boolean) {
        if (this.cashOnDelivery != cashOnDelivery) {
            this.cashOnDelivery = cashOnDelivery
            viewModelScope.launch {
                _stateLiveData.value =
                    makeStateFromPlan(shahryInstallmentPlan!!, paymentData.currency)
            }
        }
    }
}