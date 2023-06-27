package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.OrderService
import net.geidea.paymentsdk.internal.service.bnpl.valu.ValuService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.model.bnpl.valu.ConfirmRequest
import net.geidea.paymentsdk.model.bnpl.valu.GenerateOtpRequest
import net.geidea.paymentsdk.model.error.ErrorCodes
import net.geidea.paymentsdk.model.error.ErrorCodes.BnplErrorGroup.InvalidOtp
import net.geidea.paymentsdk.model.error.ErrorCodes.BnplErrorGroup.MaximumInvalidOtpAttemptsConsumed
import net.geidea.paymentsdk.model.error.ErrorCodes.BnplErrorGroup.OtpExpired
import java.math.BigDecimal


internal class ValuOtpViewModel(
        private val paymentViewModel: PaymentViewModel,
        private val valuService: ValuService,
        private val orderService: OrderService,
        private val connectivity: NetworkConnectivity,
        private val customerIdentifier: String,
        private val bnplOrderId: String,
        private val currency: String,
        private val totalAmount: BigDecimal,
        private val adminFees: BigDecimal,
        private val downPaymentAmount: BigDecimal,
        private val giftCardAmount: BigDecimal,
        private val campaignAmount: BigDecimal,
        private val tenure: Int,
) : BaseViewModel() {

    private val _stateLiveData = MutableLiveData<ValuOtpState>(ValuOtpState.Initial)
    val stateLiveData: LiveData<ValuOtpState> = _stateLiveData

    init {
        sendOtp()
    }

    fun onPurchaseButtonClicked(otp: String) {
        purchase(otp)
    }

    fun onResendButtonClicked() {
        sendOtp()
    }

    private fun sendOtp() {
        if (_stateLiveData.value == ValuOtpState.Loading) {
            return
        }

        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            val request = GenerateOtpRequest(
                    customerIdentifier = customerIdentifier,
                    bnplOrderId = bnplOrderId,
            )
            _stateLiveData.value = ValuOtpState.Loading
            when (val result = responseAsResult { valuService.postGenerateOtp(request) }) {
                is GeideaResult.Success -> {
                    _stateLiveData.value = ValuOtpState.OtpSent(result.data)
                }
                is GeideaResult.NetworkError -> {
                    paymentViewModel.setResult(result)
                    _stateLiveData.value = ValuOtpState.Error(result.detailedResponseMessage)
                }
                is GeideaResult.Error -> {
                    paymentViewModel.setResult(result)
                    _stateLiveData.value = ValuOtpState.Error("SDK error")
                }
                is GeideaResult.Cancelled -> {
                }
            }
        }
    }

    private fun purchase(otp: String) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = ValuOtpState.Loading
            val orderId = paymentViewModel.orderId
            val result = responseAsResult {
                val confirmRequest = ConfirmRequest.Builder()
                        .setCustomerIdentifier(customerIdentifier)
                        .setBnplOrderId(bnplOrderId)
                        .setOrderId(orderId)
                        .setCurrency(currency)
                        .setTotalAmount(totalAmount)
                        .setAdminFees(adminFees)
                        .setDownPayment(downPaymentAmount)
                        .setGiftCardAmount(giftCardAmount)
                        .setCampaignAmount(campaignAmount)
                        .setTenure(tenure)
                        .setOtp(otp)
                        .build()
                valuService.postConfirm(confirmRequest)
            }

            if (result is GeideaResult.Error) {
                // Error to show as "reason" in the Receipt screen
                paymentViewModel.setResult(result)
            }

            when (result) {
                is GeideaResult.Success -> {
                    _stateLiveData.value = ValuOtpState.PurchaseSuccess(result.data)

                    loadOrder(result.data.orderId!!)

                    // Show success receipt
                    paymentViewModel.navigateToReceiptOrFinish()
                }
                is GeideaResult.NetworkError -> {
                    val otpValidationErrorCodes = setOf(OtpExpired, InvalidOtp, MaximumInvalidOtpAttemptsConsumed)
                    if (result.responseCode == ErrorCodes.BnplErrorGroup.code && result.detailedResponseCode in otpValidationErrorCodes) {
                        // Recoverable error - user must retype OTP or send a new one
                        _stateLiveData.value = ValuOtpState.Error(result.detailedResponseMessage)
                    } else {
                        // Non-recoverable error - Show failure receipt screen
                        paymentViewModel.navigateToReceiptOrFinish()
                    }
                }
                is GeideaResult.Error,
                is GeideaResult.Cancelled -> {
                    // Non-recoverable error - Show failure receipt screen
                    paymentViewModel.navigateToReceiptOrFinish()
                }
            }
        }
    }

    private suspend fun loadOrder(orderId: String) {
        val orderResult = responseAsResult(outputTransform = { it.order!! }) {
            orderService.getOrder(orderId)
        }
        when (orderResult) {
            is GeideaResult.Success -> {
                paymentViewModel.setResult(orderResult)
            }
            is GeideaResult.Error -> {
                paymentViewModel.setResult(orderResult)
            }
            is GeideaResult.Cancelled -> {
                // Must not reach here
            }
        }
    }
}