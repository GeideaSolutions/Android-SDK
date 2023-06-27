package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.bnpl.souhoola.SouhoolaService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.BnplEgyptPhoneValidator
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.SouhoolaSharedViewModel
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.bnpl.souhoola.VerifyCustomerRequest
import net.geidea.paymentsdk.model.bnpl.souhoola.VerifyCustomerResponse
import net.geidea.paymentsdk.model.error.ErrorCodes
import net.geidea.paymentsdk.ui.validation.ValidationStatus

internal class SouhoolaVerifyCustomerViewModel(
    private val paymentViewModel: PaymentViewModel,
    private val souhoolaSharedViewModel: SouhoolaSharedViewModel,
    private val souhoolaService: SouhoolaService,
    private val connectivity: NetworkConnectivity,
) : BaseViewModel() {

    private val paymentData: PaymentData get() = paymentViewModel.initialPaymentData

    private val _stateLiveData =
        MutableLiveData<SouhoolaVerifyCustomerState>(SouhoolaVerifyCustomerState.Idle())
    val stateLiveData: LiveData<SouhoolaVerifyCustomerState> = _stateLiveData

    val phoneValidator = BnplEgyptPhoneValidator
    val pinValidator = SouhoolaPinValidator()

    private var phoneValidationStatus: ValidationStatus? = null
    private var pinValidationStatus: ValidationStatus? = null

    private val isValid
        get() = phoneValidationStatus == ValidationStatus.Valid
                && pinValidationStatus == ValidationStatus.Valid

    private val souhoolaMinimumAmount = paymentViewModel.merchantConfiguration.souhoolaMinimumAmount.orZero()

    fun onPhoneNumberValidationChanged(phoneNumber: String?, validationStatus: ValidationStatus) {
        this.phoneValidationStatus = validationStatus
        _stateLiveData.value = SouhoolaVerifyCustomerState.Idle(
            nextButtonEnabled = isValid
        )
    }

    fun onPinValidationChanged(pin: String?, validationStatus: ValidationStatus) {
        this.pinValidationStatus = validationStatus
        _stateLiveData.value = SouhoolaVerifyCustomerState.Idle(
            nextButtonEnabled = isValid
        )
    }

    fun verifyCustomer(phoneNumber: String, pin: String) {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            _stateLiveData.value = SouhoolaVerifyCustomerState.Error(
                nextButtonEnabled = isValid,
                phoneError = null,
                pinError = null
            )

            return
        }

        viewModelScope.launch {
            _stateLiveData.value = SouhoolaVerifyCustomerState.Loading

            val result: GeideaResult<VerifyCustomerResponse> = responseAsResult {
                val request = VerifyCustomerRequest(
                    customerIdentifier = phoneNumber,
                    customerPin = pin,
                )
                souhoolaService.postVerifyCustomer(request)
            }

            when (result) {
                is GeideaResult.Success -> {
                    if (result.data.availableLimit.orZero() >= souhoolaMinimumAmount) {
                        with(souhoolaSharedViewModel) {
                            customerIdentifier = phoneNumber
                            customerPin = pin
                            verifyResponse = result.data
                        }
                        _stateLiveData.value = SouhoolaVerifyCustomerState.Success(result.data)
                        navigate(
                            SouhoolaVerifyCustomerFragmentDirections
                                .gdActionGdSouhoolaverifycustomerfragmentToGdSouhoolainstallmentplanfragment()
                        )
                    } else {
                        souhoolaSharedViewModel.verifyResponse = null
                        showSnack(errorSnack(title = templateText(
                            R.string.gd_souhoola_avail_limit_below_limit,
                            formatAmount(souhoolaMinimumAmount, currency = paymentData.currency),
                        )))
                        _stateLiveData.value = SouhoolaVerifyCustomerState.Idle(isValid)
                    }
                }
                is GeideaResult.Error -> {
                    souhoolaSharedViewModel.verifyResponse = null
                    paymentViewModel.setResult(result)
                    showSnack(errorSnack(result))

                    if (result is GeideaResult.NetworkError) {
                        if (result.responseCode == ErrorCodes.BnplErrorGroup.code &&
                            result.detailedResponseCode == ErrorCodes.BnplErrorGroup.SouhoolaTechnicalFailure
                        ) {
                            // Non-recoverable error - Show failure receipt screen
                            paymentViewModel.navigateToReceiptOrFinish()
                        } else {
                            val customerIdentifierErrors: List<String> =
                                result.errors?.get("customerIdentifier").orEmpty()
                            val customerPinErrors: List<String> =
                                result.errors?.get("customerPin").orEmpty()

                            _stateLiveData.value = SouhoolaVerifyCustomerState.Error(
                                nextButtonEnabled = true,
                                phoneError = customerIdentifierErrors.firstOrNull()
                                    ?.let(NativeText::Plain),
                                pinError = customerPinErrors.firstOrNull()?.let(NativeText::Plain),
                            )
                        }
                    } else {
                        _stateLiveData.value = SouhoolaVerifyCustomerState.Idle(isValid)
                    }
                }
                else -> {
                    // Nothing to do
                }
            }
        }
    }
}

