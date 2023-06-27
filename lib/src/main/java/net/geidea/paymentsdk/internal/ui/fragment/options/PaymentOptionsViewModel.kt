package net.geidea.paymentsdk.internal.ui.fragment.options

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentMethodsFilter
import net.geidea.paymentsdk.flow.pay.PaymentOption
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.service.MeezaService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.qr.r2p.MeezaQrRequestToPay
import net.geidea.paymentsdk.internal.ui.widget.OnImeActionListener
import net.geidea.paymentsdk.internal.ui.widget.OnPhoneNumberChangedListener
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import java.math.BigDecimal
import kotlin.random.Random

@GeideaSdkInternal
internal class PaymentOptionsViewModel(
    private val paymentViewModel: PaymentViewModel,
    private val paymentMethodsFilter: PaymentMethodsFilter,
    private val meezaService: MeezaService,
    private val connectivity: NetworkConnectivity,
    private val formatter: NativeTextFormatter,
    private val step: Step?,
    private val downPaymentAmount: BigDecimal?,
) : BaseViewModel() {

    private val paymentData = paymentViewModel.initialPaymentData

    private val isBnplDownPaymentMode = downPaymentAmount != null

    val optionItems: List<PaymentOptionItem>

    private val _nextButtonEnabledLiveData = MutableLiveData<Boolean>()
    val nextButtonEnabledLiveData: LiveData<Boolean> = _nextButtonEnabledLiveData

    private val _selectedPaymentMethodLiveData = MutableLiveData<PaymentMethodDescriptor?>()
    val selectedPaymentMethodLiveData: LiveData<PaymentMethodDescriptor?> = _selectedPaymentMethodLiveData

    // MeezaQR-specific

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressLiveData: LiveData<Boolean> = _progressVisible

    private var isMeezaQrPhoneNumberValid: Boolean = false
    private var meezaQrPhoneNumber: String? = null

    val phoneNumberChangedListener: OnPhoneNumberChangedListener = { phoneNumber: String?, isValid: Boolean ->
        isMeezaQrPhoneNumberValid = isValid
        meezaQrPhoneNumber = phoneNumber
        updateNextButton()
    }

    val imeNextActionListener: OnImeActionListener = {
        if (_selectedPaymentMethodLiveData.value != null) {
            onNextButtonClicked()
        }
    }

    init {
        optionItems = createPaymentOptionItems(paymentMethodsFilter.filteredPaymentOptions)
    }

    fun start() {
        val errorMessage: NativeText? = paymentMethodsFilter.checkValidity()
        if (errorMessage != null) {
            paymentViewModel.setResult(GeideaResult.SdkError(
                errorMessage = formatter.format(errorMessage).toString()
            ))
            paymentViewModel.navigateToReceiptOrFinish()
            return
        }

        navigateToSinglePaymentIfPossible()
    }

    /**
     * Check if there is only one option and navigates to it if required conditions are met.
     */
    private fun navigateToSinglePaymentIfPossible() {
        // Ungroup and flatten everything
        val flattenedItems: Set<PaymentMethodItem> = optionItems.flatten()

        val optionsCount = flattenedItems.size
        when {
            optionsCount > 1 -> {
                // Do nothing, user should select from the list
            }
            optionsCount == 1 -> {
                val singleOption = flattenedItems.single()
                if (shouldNavigateAutomaticallyTo(singleOption)) {
                    // If only 1 method available directly navigate to it
                    attemptToNavigateToPaymentMethodWithNoArgs(
                        paymentMethod = singleOption.paymentMethod,
                        popFromBackStack = true,
                    )
                }
            }
            else -> {
                val errorResult = GeideaResult.SdkError(
                    errorMessage = "No available payment options"
                )
                paymentViewModel.setResult(errorResult)
                paymentViewModel.navigateToReceiptOrFinish()
            }
        }
    }

    /**
     * Attempt to navigate to payment method which has no dynamic arguments, such as:
     * all cards and all BNPL. A payment with dynamic argument (phone number) is Meeza QR.
     * Phone number must be entered before navigating to Meeza QR screen.
     *
     * @see attemptMeezaQr
     */
    private fun attemptToNavigateToPaymentMethodWithNoArgs(
        paymentMethod: PaymentMethodDescriptor,
        popFromBackStack: Boolean = false
    ) {
        paymentViewModel.onPaymentMethodSelected(paymentMethod, isBnplDownPaymentMode)

        val navOptions =  if (popFromBackStack) {
            navOptions {
                popUpTo(R.id.gd_paymentoptionsfragment) {
                    inclusive = true
                }
            }
        } else {
            null
        }

        when (paymentMethod) {
            is PaymentMethodDescriptor.Card -> {
                navigate(
                    navDirections = PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdCardGraph(
                        step = step,
                        downPaymentAmount = downPaymentAmount,
                    ),
                    navOptions = navOptions,
                )
            }
            is PaymentMethodDescriptor.MeezaQr -> {
                // Nothing to do - Meeza QR flow requires input arguments, use navigateToMeezaQr() instead
            }
            is BnplPaymentMethodDescriptor.ValuInstallments -> {
                navigate(
                    navDirections = PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdValuGraph(),
                    navOptions = navOptions,
                )
            }
            is BnplPaymentMethodDescriptor.ShahryInstallments -> {
                navigate(
                    navDirections = PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdShahryGraph(),
                    navOptions = navOptions,
                )
            }
            is BnplPaymentMethodDescriptor.SouhoolaInstallments -> {
                navigate(
                    navDirections = PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdSouhoolaverifycustomerfragment(),
                    navOptions = navOptions,
                )
            }
        }
    }

    /**
     * No-argument payment methods are cards and BNPL because they can be navigated to without
     * prior data input from user and passed as arguments. Only Meeza QR requires phone number to
     * be entered before.
     */
    private val PaymentMethodDescriptor.requiresArguments: Boolean get()
        = this is PaymentMethodDescriptor.MeezaQr

    private fun shouldNavigateAutomaticallyTo(option: PaymentOptionItem): Boolean {
        return !isBnplDownPaymentMode
                && option is PaymentMethodItem
                && !option.paymentMethod.requiresArguments
    }

    private fun navigateToMeezaQr(
        paymentIntentId: String,
        qrMessage: String,
        qrCodeImageBase64: String,
    ) {
        paymentViewModel.onPaymentMethodSelected(PaymentMethodDescriptor.MeezaQr, isBnplDownPaymentMode)
        navigate(
            PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdMeezaqrpaymentfragment(
                step = step,
                downPaymentAmount = downPaymentAmount,
                merchantName = paymentViewModel.merchantConfiguration.merchantName,
                qrMessage = qrMessage,
                qrCodeImageBase64 = qrCodeImageBase64,
                paymentIntentId = paymentIntentId,
            )
        )
    }

    private fun createPaymentOptionItems(options: Set<PaymentOption>): List<PaymentOptionItem> {
        return options
            .groupBy { option ->
                // BNPL methods are always in one group, other payment methods are individual
                when (option.paymentMethod) {
                    is BnplPaymentMethodDescriptor -> KEY_GROUP_BNPL
                    else -> {
                        // Make a unique key to avoid falling into same group
                        Random.nextInt().toString()
                    }
                }
            }
            .map { (key, optionOrGroup) ->
                when (key) {
                    KEY_GROUP_BNPL -> {
                        PaymentMethodGroup(
                            label = resourceText(R.string.gd_pm_group_bnpl),
                            items = optionOrGroup.map(::createPaymentMethodItem).toSet()
                        )
                    }
                    else -> {
                        val option = optionOrGroup.first()
                        createPaymentMethodItem(option)
                    }
                }
            }
    }

    private fun createPaymentMethodItem(option: PaymentOption): PaymentMethodItem {
        return PaymentMethodItem(
            label = option.label?.let(::plainText)
                ?: resourceText(option.paymentMethod.text), //getOptionLabel(option),
            paymentMethod = option.paymentMethod
        )
    }

    private fun getOptionLabel(option: PaymentOption): NativeText {
        return when {
            option.label != null -> plainText(option.label)
            option.paymentMethod is PaymentMethodDescriptor.Card -> {
                val brands = option.paymentMethod.acceptedBrands
                when (brands.size == 1) {
                    true -> resourceText(brands.first().displayName)
                    false -> resourceText(option.paymentMethod.text)
                }
            }
            else -> resourceText(option.paymentMethod.text)
        }
    }

    fun onPaymentMethodSelected(paymentMethod: PaymentMethodDescriptor) {
        _selectedPaymentMethodLiveData.value = paymentMethod
        updateNextButton()
    }

    private fun updateNextButton() {
        _nextButtonEnabledLiveData.value = shouldEnableNextButton()
    }

    private fun shouldEnableNextButton(): Boolean {
        val selectedPaymentMethod = _selectedPaymentMethodLiveData.value
        return if (selectedPaymentMethod != null) {
            if (selectedPaymentMethod == PaymentMethodDescriptor.MeezaQr) {
                // Meeza QR requires valid phone to proceed
                isMeezaQrPhoneNumberValid
            } else {
                // Other PM selected
                true
            }
        } else {
            // No PM selected
            false
        }
    }

    fun onNextButtonClicked() {
        val selectedPaymentMethod = _selectedPaymentMethodLiveData.value
        if (selectedPaymentMethod == null) {
            // Should not reach here but as precaution return
            return
        }

        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        val errorMessage = paymentMethodsFilter.checkRequirements(selectedPaymentMethod)
        if (errorMessage != null) {
            showSnack(errorSnack(message = errorMessage))
            return
        }

        // The requirements of the selected payment methods are met, can proceed

        if (selectedPaymentMethod is PaymentMethodDescriptor.MeezaQr) {
            // Double-check if phone is valid, cause keyboard "Next" is always active and can
            // circumvent the phone validation.
            val phoneNumber = this.meezaQrPhoneNumber
            if (phoneNumber != null && isMeezaQrPhoneNumberValid) {
                attemptMeezaQr(phoneNumber)
            }
        } else {
            attemptToNavigateToPaymentMethodWithNoArgs(selectedPaymentMethod)
        }
    }

    private fun attemptMeezaQr(phoneNumber: String) {
        viewModelScope.launch {
            showProgress(true)

            // Before navigating to Meeza QR, generate the QR and send a wallet app notification first
            val result = MeezaQrRequestToPay(
                orderId = paymentViewModel.orderId,
                amount = if (isBnplDownPaymentMode)
                    downPaymentAmount!!
                else
                    paymentData.amount,
                currency = paymentData.currency,
                phoneNumber = phoneNumber,
                customerEmail = paymentData.customerEmail,
                callbackUrl = paymentData.callbackUrl,
            ).perform()

            when (result) {
                is GeideaResult.Success -> {
                    val qrImageResponse = result.data
                    navigateToMeezaQr(
                        paymentIntentId = qrImageResponse.paymentIntentId!!,
                        qrMessage = qrImageResponse.message!!,
                        qrCodeImageBase64 = qrImageResponse.image!!,
                    )
                }
                is GeideaResult.Error -> {
                    showSnack(errorSnack(result))
                    paymentViewModel.setResult(result)
                }
                else -> {}
            }

            showProgress(false)
        }
    }

    private fun showProgress(inProgress: Boolean) {
        _progressVisible.value = inProgress
        _nextButtonEnabledLiveData.value = !inProgress
    }

    override fun navigateBack() {
        // BNPL currently disallows changing the installment plan after it is selected.
        // So going back from down payment flow to BNPL is prohibited.
        if (!isBnplDownPaymentMode) {
            super.navigateBack()
        }
    }

    companion object {
        private const val KEY_GROUP_BNPL = "bnpl"
    }
}