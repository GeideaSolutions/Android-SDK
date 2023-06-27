package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.OrderService
import net.geidea.paymentsdk.internal.service.bnpl.souhoola.SouhoolaService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.SouhoolaSharedViewModel
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.internal.util.templateText
import net.geidea.paymentsdk.model.bnpl.souhoola.ConfirmRequest
import net.geidea.paymentsdk.model.bnpl.souhoola.GenerateOtpRequest
import net.geidea.paymentsdk.model.bnpl.souhoola.ResendOtpRequest
import net.geidea.paymentsdk.model.error.ErrorCodes
import net.geidea.paymentsdk.model.error.getReason

@GeideaSdkInternal
internal class SouhoolaOtpViewModel(
    private val paymentViewModel: PaymentViewModel,
    private val souhoolaSharedViewModel: SouhoolaSharedViewModel,
    private val souhoolaService: SouhoolaService,
    private val orderService: OrderService,
    private val connectivity: NetworkConnectivity,
) : BaseViewModel() {

    private val _stateLiveData = MutableLiveData<SouhoolaOtpState>(Initial)
    val stateLiveData: LiveData<SouhoolaOtpState> = _stateLiveData

    private val _timeRemainingLiveData = MutableLiveData<NativeText>()
    val timeRemainingLiveData: LiveData<NativeText> = _timeRemainingLiveData

    private val _codesLeftLiveData = MutableLiveData<Int>(MAX_ATTEMPTS)
    val codesLeftLiveData: LiveData<NativeText> = _codesLeftLiveData.map { codesLeft ->
        templateText(R.string.gd_souhoola_n_codes_left, codesLeft)
    }

    init {
        generateOtp()
    }

    fun onPurchaseButtonClicked(otp: String) {
        confirm(otp)
    }

    private fun generateOtp() {
        if (_stateLiveData.value!!.progressVisible) {
            return
        }

        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = _stateLiveData.value!!.otpGenerating()

            when (val result = responseAsResult {
                val request = GenerateOtpRequest {
                    customerIdentifier = souhoolaSharedViewModel.customerIdentifier
                    customerPin = souhoolaSharedViewModel.customerPin
                    orderId = souhoolaSharedViewModel.orderId
                    souhoolaTransactionId = souhoolaSharedViewModel.souhoolaTransactionId!!
                }
                souhoolaService.postGenerateOtp(request)
            }) {
                is GeideaResult.Success -> {
                    _stateLiveData.value = _stateLiveData.value!!.otpGenerated()
                    startTimer()
                }
                is GeideaResult.NetworkError -> {
                    paymentViewModel.setResult(result)
                    _stateLiveData.value = _stateLiveData.value!!.withError(getReason(result))
                }
                is GeideaResult.Error -> {
                    paymentViewModel.setResult(result)
                    paymentViewModel.navigateToReceiptOrFinish()
                }
                is GeideaResult.Cancelled -> {
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            var now = System.currentTimeMillis()
            val endTime = now + TIMEOUT_SECONDS * 1_000
            do {
                val remainingSeconds: Long = (endTime - now) / 1_000
                val timeRemaining: CharSequence = String.format("%02d:%02d", remainingSeconds / 60, remainingSeconds % 60)
                _timeRemainingLiveData.value = NativeText.Template.of(R.string.gd_souhoola_time_remaining_s, timeRemaining)
                now = System.currentTimeMillis()

                delay(1_000)
            } while (now < endTime)

            _stateLiveData.value = _stateLiveData.value!!.withResendButtonEnabled()
        }
    }

    private fun confirm(otp: String) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        if (_stateLiveData.value?.purchaseButtonProgressVisible == true) {
            // Prevent double-click
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = _stateLiveData.value!!.otpConfirming()
            val result = responseAsResult {
                val confirmRequest = with(souhoolaSharedViewModel) {
                    ConfirmRequest.Builder()
                        .setCustomerIdentifier(customerIdentifier)
                        .setCustomerPin(customerPin)
                        .setSouhoolaTransactionId(souhoolaTransactionId)
                        .setOrderId(orderId)
                        .setOtp(otp)
                        .build()
                }
                souhoolaService.postConfirm(confirmRequest)
            }

            if (result is GeideaResult.Error) {
                // Error to show as "reason" in the Receipt screen
                paymentViewModel.setResult(result)
            }

            when (result) {
                is GeideaResult.Success -> {
                    loadOrder(souhoolaSharedViewModel.orderId!!)

                    // Mark as success and not cancel it on ViewModel dismiss
                    souhoolaSharedViewModel.mustCancel = false

                    // Show success receipt
                    paymentViewModel.navigateToReceiptOrFinish()
                }
                is GeideaResult.Error -> {
                    if (result is GeideaResult.NetworkError
                        && result.hasCode(ErrorCodes.BnplErrorGroup.code, ErrorCodes.BnplErrorGroup.SouhoolaTransactionFailed)
                    ) {
                        _stateLiveData.value = _stateLiveData.value!!.withError(getReason(result))
                    } else {
                        paymentViewModel.navigateToReceiptOrFinish()
                    }
                }
                is GeideaResult.Cancelled -> {
                    // Non-recoverable error - Show failure receipt screen
                    paymentViewModel.navigateToReceiptOrFinish()
                }
            }
        }
    }

    fun onResendButtonClicked() {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = _stateLiveData.value!!.otpGenerating()

            when (val result = responseAsResult {
                val request = ResendOtpRequest(
                    customerIdentifier = souhoolaSharedViewModel.customerIdentifier!!,
                    customerPin = souhoolaSharedViewModel.customerPin!!
                )
                souhoolaService.postResendOtp(request)
            }) {
                is GeideaResult.Success -> {
                    _stateLiveData.value = _stateLiveData.value!!.otpGenerated()
                }
                is GeideaResult.NetworkError -> {
                    paymentViewModel.setResult(result)
                    paymentViewModel.setResult(result)
                    _stateLiveData.value = _stateLiveData.value!!.withError(getReason(result))
                }
                is GeideaResult.Error -> {
                    paymentViewModel.setResult(result)
                    paymentViewModel.navigateToReceiptOrFinish()
                }
                is GeideaResult.Cancelled -> {
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

    companion object {
        const val TIMEOUT_SECONDS = 60
        const val MAX_ATTEMPTS = 1
    }
}