package net.geidea.paymentsdk.internal.ui.fragment.card.cvv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.service.TokenService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.ui.fragment.card.TokenPaymentViewModel3dsV2
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.OnInvalidStatusListener
import net.geidea.paymentsdk.ui.validation.OnValidStatusListener
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.validation.card.validator.CvvValidator
import java.util.*


internal class CvvInputViewModel(
    private val paymentViewModel: PaymentViewModel,
    private val tokenPaymentViewModel: TokenPaymentViewModel3dsV2,
    private val tokenService: TokenService,
    connectivity: NetworkConnectivity,
    private val tokenId: String,
) : BaseViewModel(), OnValidStatusListener<String>, OnInvalidStatusListener<String> {

    private val _loadingLiveData = MutableLiveData(false)
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    private val _nextButtonEnabledLiveData = MutableLiveData(false)
    val nextButtonEnabledLiveData: LiveData<Boolean> = _nextButtonEnabledLiveData

    private val _cardVisibleLiveData = MutableLiveData(false)
    val cardVisibleLiveData: LiveData<Boolean> = _cardVisibleLiveData

    private val _cardBrandLiveData = MutableLiveData<CardBrand>()
    val cardBrandLiveData: LiveData<CardBrand> = _cardBrandLiveData

    private val _maskedCardNumberLiveData = MutableLiveData(emptyText() as NativeText)
    val maskedCardNumberLiveData: LiveData<NativeText> = _maskedCardNumberLiveData

    private val _expiryTextLiveData = MutableLiveData(emptyText() as NativeText)
    val expiryTextLiveData: LiveData<NativeText> = _expiryTextLiveData

    private val _expiredLiveData = MutableLiveData<Boolean>()
    val expiredLiveData: LiveData<Boolean> = _expiredLiveData

    private val _cvvInputEnabledLiveData = MutableLiveData<Boolean>()
    val cvvInputEnabledLiveData: LiveData<Boolean> = _cvvInputEnabledLiveData

    init {
        if (connectivity.isConnected) {
            viewModelScope.launch {
                tokenPaymentViewModel.finishOnCatch {
                    val token = try {
                        _loadingLiveData.value = true
                        tokenService.getToken(tokenId)
                    } finally {
                        _loadingLiveData.value = false
                    }

                    if (token.isSuccess) {
                        _cardVisibleLiveData.value = true

                        val cardBrand = token.schema?.let(CardBrand::fromBrandName) ?: CardBrand.Unknown
                        _cardBrandLiveData.value = cardBrand

                        _maskedCardNumberLiveData.value = plainText("${token.schema ?: ""} •••• ${token.lastFourDigits}")

                        val expiryDate = token.expiryDate
                        val expired = !(expiryDate?.isValid ?: true)
                        _expiredLiveData.value = expired
                        _cvvInputEnabledLiveData.value = !expired

                        var calendar = expiryDate?.calendar
                        if (calendar != null) {
                            val monthText = monthShort(calendar)
                            var year = calendar.get(Calendar.YEAR)
                            if (year < 100) {
                                year += 2000
                            }
                            _expiryTextLiveData.value = templateText(R.string.gd_expires_month_year, monthText, year.toString())
                        }
                    } else {
                        paymentViewModel.onPaymentFinished(GeideaResult.NetworkError(response = token))
                    }
                }
            }
        } else {
            showSnack(noInternetSnack)
        }
    }

    fun createCvvValidator(cardBrand: CardBrand): Validator<String> {
        return CvvValidator(cardBrand)
    }

    override fun onValidStatus(value: String) {
        _nextButtonEnabledLiveData.value = true
    }

    override fun onInvalidStatus(value: String?, validationStatus: ValidationStatus.Invalid) {
        _nextButtonEnabledLiveData.value = false
    }

    fun onNextButtonClicked(cvv: String) {
        tokenPaymentViewModel.onCvvEntered(cvv)
    }

    fun onCancelConfirmed() {
        paymentViewModel.onPaymentFinished(paymentViewModel.makeDefaultCancelledResult(orderId = null))
    }
}