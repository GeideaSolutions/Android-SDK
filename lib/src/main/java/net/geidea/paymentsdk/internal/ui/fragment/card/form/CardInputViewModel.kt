package net.geidea.paymentsdk.internal.ui.fragment.card.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentMethodsFilter
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.errorSnack
import net.geidea.paymentsdk.internal.ui.fragment.card.BaseCardPaymentViewModel
import net.geidea.paymentsdk.internal.ui.fragment.card.CardPaymentViewModel3dsV2
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.DeviceInfo
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.UserAgent
import net.geidea.paymentsdk.internal.ui.fragment.options.PaymentOptionsFragmentDirections
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.Event
import net.geidea.paymentsdk.internal.util.LiveEvent
import net.geidea.paymentsdk.internal.util.MutableLiveEvent
import net.geidea.paymentsdk.internal.util.NetworkConnectivity
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.Country
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import java.math.BigDecimal


internal class CardInputViewModel(
        private val cardPaymentViewModel: BaseCardPaymentViewModel,
        private val step: Step?,
        private val connectivity: NetworkConnectivity,
        // Runtime arguments
        val downPaymentAmount: BigDecimal?,
) : BaseViewModel(), UserAgent {

    val acceptedCardBrands: Set<CardBrand> get() = paymentMethodsFilter.acceptedCardBrands

    private val _payButtonEnabledLiveData = MutableLiveData<Boolean>(false)
    val payButtonEnabledLiveData: LiveData<Boolean> = _payButtonEnabledLiveData

    private val _htmlLiveData = object : MutableLiveData<String>() {
        override fun setValue(value: String?) {
            super.setValue(value)
            if (value != null) {
                htmlLoadedCompletable.complete(Unit)
            }
        }
    }

    /**
     * Fingerprinting JS script coming from the [net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationResponse]
     * loaded here.
     */
    val htmlLiveData: LiveData<String> = _htmlLiveData

    private val _clearCardLiveEvent = MutableLiveEvent<Unit>()
    val clearCardLiveEvent: LiveEvent<Unit> = _clearCardLiveEvent

    /**
     * Serves as a condition that authentication is initiated. 'Pay' button will await until this
     * completes.
     */
    private var htmlLoadedCompletable = CompletableDeferred<Unit>()

    override lateinit var deviceInfo: DeviceInfo
        internal set

    private val _cancelButtonEnabledLiveData = MutableLiveData<Boolean>(true)
    val cancelButtonLiveData: LiveData<Boolean> = _cancelButtonEnabledLiveData

    private val paymentViewModel = cardPaymentViewModel.paymentViewModel

    internal val paymentData: PaymentData get() = downPaymentAmount
        ?.let { paymentViewModel.initialPaymentData.copy(amount = it) }
        ?: paymentViewModel.initialPaymentData

    // Alternative payment methods selector

    private val paymentMethodsFilter = PaymentMethodsFilter(
        merchantConfiguration = paymentViewModel.merchantConfiguration,
        paymentData = paymentData,
        isBnplDownPaymentMode = isInDownPaymentMode,
    )

    val alternativePaymentMethods: Set<PaymentMethodDescriptor> get() = paymentMethodsFilter.getPaymentMethodsAlternativeTo(
        PaymentMethodDescriptor.Card()
    )

    private val _alternativeMethodSelectorVisible = MutableLiveData<Boolean>()
    val alternativeMethodSelectorVisible: LiveData<Boolean> = _alternativeMethodSelectorVisible

    private val failedOnceObserver = Observer<Boolean> { failedOnce ->
        _alternativeMethodSelectorVisible.value = failedOnce && alternativePaymentMethods.isNotEmpty()
    }

    private val isInDownPaymentMode: Boolean get() = step != null

    init {
        cardPaymentViewModel.failedOnceLiveData.observeForever(failedOnceObserver)
    }

    override fun onCleared() {
        super.onCleared()
        cardPaymentViewModel.failedOnceLiveData.removeObserver(failedOnceObserver)
    }

    override fun loadHtml(html: String) {
        _htmlLiveData.value = html
    }

    fun onCardNumberEntered(cardNumber: String, paymentData: PaymentData) {
        if (cardPaymentViewModel is CardPaymentViewModel3dsV2) {
            cardPaymentViewModel.onCardNumberEntered(cardNumber, paymentData, userAgent = this)

            // Create a new html
            htmlLoadedCompletable = CompletableDeferred()
        }
    }

    fun getPrePopulatedBillingCountryCode(merchantConfig: MerchantConfigurationResponse): String {
        return getPrePopulatedCountryCode(paymentData.billingAddress, merchantConfig)
    }

    fun getPrePopulatedShippingCountryCode(merchantConfig: MerchantConfigurationResponse): String {
        return getPrePopulatedCountryCode(paymentData.shippingAddress, merchantConfig)
    }

    private fun getPrePopulatedCountryCode(address: Address?, merchantConfig: MerchantConfigurationResponse): String {
        val merchantCountryCode2 = merchantConfig.merchantCountryTwoLetterCode?.uppercase()
        val fallbackCountryCode3 = merchantConfig.countries
            ?.find { it.key2 == merchantCountryCode2 }
            ?.takeIf(Country::isSupported)
            ?.key3
            ?: "SAU"

        return address?.countryCode
            ?.takeIf { countryCode ->
                merchantConfig.countries.orEmpty().any { country ->
                    country.isSupported && country.key2 == countryCode
                }
            }
            ?: fallbackCountryCode3
    }

    fun onPayButtonClicked(finalPaymentData: PaymentData) {
        viewModelScope.launch {
            // This can be added as an improvement but has to be agreed with web and iOS
            /*if (!connectivity.isConnected) {
                showSnack(noInternetSnack)
                return
            }*/

            if (cardPaymentViewModel is CardPaymentViewModel3dsV2) {
                // 3DS v2
                if (cardPaymentViewModel.initiateAuthResponse?.isSuccess == true) {
                    awaitRedirectHtml()
                    cardPaymentViewModel.onPayButtonClicked(finalPaymentData)
                } else {
                    _clearCardLiveEvent.value = Event(Unit)
                    val initiateAuthResponse = cardPaymentViewModel.initiateAuthResponse
                    if (initiateAuthResponse != null) {
                        showSnack(errorSnack(initiateAuthResponse))
                        cardPaymentViewModel.markAsFailedOnce()
                        cardPaymentViewModel.paymentViewModel.setResult(GeideaResult.NetworkError(
                            orderId = cardPaymentViewModel.orderId,
                            response = initiateAuthResponse,
                        ))
                    }
                }
            } else {
                // 3DS v1
                cardPaymentViewModel.onPayButtonClicked(finalPaymentData)
            }
        }
    }

    /**
     * Await for the authentication to be initiated and the redirectHtml to be loaded.
     * Block UI until this happens.
     */
    private suspend fun awaitRedirectHtml() {
        cardPaymentViewModel.paymentViewModel.processing = true
        try {
            htmlLoadedCompletable.await()
        } catch(e: CancellationException) {
            /* Nothing to do */
        } finally {
            cardPaymentViewModel.paymentViewModel.processing = false
        }
    }

    fun onBackPressed() {
        navigateBack()
    }

    override fun navigateBack() {
        dismissGlobalSnack()
        super.navigateBack()
    }

    fun onCancelConfirmed() {
        _cancelButtonEnabledLiveData.value = false
        cardPaymentViewModel.cancelAndFinish()
    }

    fun onAlternativePaymentMethodClicked(alternativeMethod: PaymentMethodDescriptor) {
        dismissGlobalSnack()

        val errorMessage = paymentMethodsFilter.checkRequirements(alternativeMethod)
        if (errorMessage == null) {
            if (alternativeMethod !is PaymentMethodDescriptor.MeezaQr) {
                navigateBackTo(R.id.gd_paymentoptionsfragment)
            } else {
                navigateBackToWithResult(R.id.gd_paymentoptionsfragment, "selectMeezaQr", true)
            }

            // Now that we are back to Payment Options screen, navigate from it to the alternativeMethod
            when (alternativeMethod) {
                is PaymentMethodDescriptor.Card -> {
                    navigate(
                        navDirections = PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdCardGraph(
                            step = step,
                            downPaymentAmount = downPaymentAmount,
                        ),
                    )
                }
                is PaymentMethodDescriptor.MeezaQr -> {
                    // Meeza QR is different. We cannot go directly to it cause customer must input phone.
                    // So, do nothing, customer is now back to Payment Options screen where they
                    // must first enter phone number to proceed with Meeza QR
                }
                is BnplPaymentMethodDescriptor.ValuInstallments -> {
                    PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdValuGraph()
                }
                is BnplPaymentMethodDescriptor.ShahryInstallments -> {
                    PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdShahryGraph()
                }
                is BnplPaymentMethodDescriptor.SouhoolaInstallments -> {
                    PaymentOptionsFragmentDirections.gdActionGdPaymentoptionsfragmentToGdSouhoolaverifycustomerfragment()
                }
            }
        } else {
            showSnack(errorSnack(message = errorMessage))
        }
    }

    fun onShowAllPaymentMethodsClicked() {
        dismissSnacks()
        // Presence of "Show All" button means there are 2 or more payment methods to choose from,
        // So it is expected that previous screen is a Payment Options screen
        navigateBack()
    }

    fun onCardFieldChanged() {
        dismissSnacks()
    }

    private fun dismissSnacks() {
        dismissGlobalSnack()
        dismissSnack()
    }

    private fun dismissGlobalSnack() {
        // Card transaction error snacks are shown on global / paymentViewModel level. This approach
        // makes it possible to show a snackbar on Auth screen and be preserved after returning
        // back to Form screen.
        cardPaymentViewModel.paymentViewModel.dismissSnack()
    }
}