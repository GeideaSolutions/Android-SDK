package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.bnpl.valu.ValuService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.BnplEgyptPhoneValidator
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.model.bnpl.valu.VerifyCustomerRequest
import net.geidea.paymentsdk.model.bnpl.valu.VerifyCustomerResponse
import net.geidea.paymentsdk.model.error.getReason
import net.geidea.paymentsdk.ui.validation.ValidationStatus

internal class ValuPhoneNumberViewModel(
    private val paymentViewModel: PaymentViewModel,
    private val valuService: ValuService,
    private val connectivity: NetworkConnectivity,
) : BaseViewModel() {

    private val _stateLiveData = MutableLiveData<ValuPhoneNumberState>(ValuPhoneNumberState.Idle())
    val stateLiveData: LiveData<ValuPhoneNumberState> = _stateLiveData

    // Accepts Egypt 10-digit long numbers
    val phoneValidator = BnplEgyptPhoneValidator

    private var validationStatus: ValidationStatus? = null

    private val isValid get() = validationStatus == ValidationStatus.Valid

    fun onPhoneNumberValidationChanged(phoneNumber: String?, validationStatus: ValidationStatus) {
        this.validationStatus = validationStatus
        _stateLiveData.value = ValuPhoneNumberState.Idle(validationStatus == ValidationStatus.Valid)
    }

    fun verifyPhoneNumber(phoneNumber: String) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            _stateLiveData.value = ValuPhoneNumberState.Error(isValid, null)

            return
        }

        viewModelScope.launch {
            _stateLiveData.value = ValuPhoneNumberState.Loading

            val request = VerifyCustomerRequest(customerIdentifier = phoneNumber)
            val result: GeideaResult<VerifyCustomerResponse> = responseAsResult {
                valuService.postVerifyCustomer(request)
            }

            when (result) {
                is GeideaResult.Success -> {
                    _stateLiveData.value = ValuPhoneNumberState.Success(result.data)
                    navigate(ValuPhoneNumberFragmentDirections.gdActionGdValuphonenumberfragmentToGdValuinstallmentplanfragment(
                            customerIdentifier = phoneNumber,
                            step = Step(current = 2, stepCount = 3)
                    ))
                }
                is GeideaResult.Error -> {
                    paymentViewModel.setResult(result)
                    _stateLiveData.value = ValuPhoneNumberState.Error(isValid, null)
                    _stateLiveData.value = ValuPhoneNumberState.Error(isValid, getReason(result))
                }
                else -> {
                    // Nothing to do
                }
            }

        }
    }
}

