package net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.bnpl.shahry.ShahryService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.model.bnpl.shahry.SelectInstallmentPlanRequest
import net.geidea.paymentsdk.model.bnpl.shahry.toShahryOrderItems
import net.geidea.paymentsdk.model.error.ErrorCodes
import net.geidea.paymentsdk.model.error.getReason

@GeideaSdkInternal
internal class ShahryVerifyViewModel(
        private val paymentViewModel: PaymentViewModel,
        private val shahryService: ShahryService,
        private val connectivity: NetworkConnectivity,
) : BaseViewModel() {

    private val paymentData: PaymentData get() = paymentViewModel.initialPaymentData

    private val _stateLiveData = MutableLiveData<ShahryVerifyState>(ShahryVerifyState.Initial)
    val stateLiveData: LiveData<ShahryVerifyState> = _stateLiveData

    fun onNextButtonClicked(customerIdentifier: String) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = ShahryVerifyState.Loading

            val result = responseAsResult {
                val request = SelectInstallmentPlanRequest {
                    orderId = paymentViewModel.orderId
                    totalAmount = paymentData.amount
                    currency = paymentData.currency
                    merchantReferenceId = paymentData.merchantReferenceId
                    callbackUrl = paymentData.callbackUrl
                    billingAddress = paymentData.billingAddress
                    shippingAddress = paymentData.shippingAddress
                    customerEmail = paymentData.customerEmail
                    paymentOperation = paymentData.paymentOperation
                    paymentMethods = paymentViewModel.acceptedCardBrandNames
                    restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null

                    this.customerIdentifier = customerIdentifier
                    items = paymentData.orderItems?.toShahryOrderItems(paymentData.currency)

                }
                shahryService.postSelectInstallmentPlan(request)
            }
            when (result) {
                is GeideaResult.Success -> {
                    paymentViewModel.orderId = result.data.orderId!!
                    navigate(ShahryVerifyFragmentDirections.gdActionGdInputidfragmentToGdConfirmfragment(
                            customerIdentifier = customerIdentifier,
                    ))
                    _stateLiveData.value = ShahryVerifyState.Success
                }
                is GeideaResult.NetworkError -> {
                    if (result.detailedResponseCode in listOf(
                                    ErrorCodes.BnplErrorGroup.CustomerNotEnrolledWithBnplProvider,
                                    ErrorCodes.BnplErrorGroup.CustomerNotEligibleForPaymentsWithBnplProvider,
                                    ErrorCodes.BnplErrorGroup.PurchaseAmountMoreThanCustomerLimit,
                            )) {
                        emitErrorState(result)
                    } else {
                        paymentViewModel.setResult(result)
                        paymentViewModel.navigateToReceiptOrFinish()
                    }
                }
                is GeideaResult.Error -> emitErrorState(result)
                is GeideaResult.Cancelled -> {
                    // Nothing to do
                }
            }
        }
    }

    private fun emitErrorState(errorResult: GeideaResult.Error) {
        paymentViewModel.setResult(errorResult)
        showSnack(errorSnack(errorResult))
        _stateLiveData.value = ShahryVerifyState.Error(
                nextButtonEnabled = true,
                message = NativeText.Plain("")
        )
        _stateLiveData.value = ShahryVerifyState.Error(
                nextButtonEnabled = true,
                message = getReason(errorResult)
        )
    }
}