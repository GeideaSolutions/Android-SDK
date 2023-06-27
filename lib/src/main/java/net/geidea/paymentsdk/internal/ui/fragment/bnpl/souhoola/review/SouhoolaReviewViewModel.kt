package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.review

import android.graphics.Typeface
import android.view.Gravity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
import net.geidea.paymentsdk.internal.ui.fragment.receipt.ReceiptItem
import net.geidea.paymentsdk.internal.ui.widget.Step
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.internal.util.Logger.loge
import net.geidea.paymentsdk.model.bnpl.souhoola.*
import net.geidea.paymentsdk.model.common.Source
import net.geidea.paymentsdk.model.error.ErrorCodes
import net.geidea.paymentsdk.model.error.getReason
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@GeideaSdkInternal
internal class SouhoolaReviewViewModel(
    private val souhoolaService: SouhoolaService,
    private val connectivity: NetworkConnectivity,
    private val paymentViewModel: PaymentViewModel,
    private val souhoolaSharedViewModel: SouhoolaSharedViewModel,
) : BaseViewModel() {

    private val _stateLiveData = MutableLiveData<SouhoolaReviewState>(Initial)
    val stateLiveData: LiveData<SouhoolaReviewState> = _stateLiveData

    private lateinit var selectedPlan: InstallmentPlan
    private lateinit var reviewResponse: ReviewTransactionResponse

    private var cashOnDelivery: Boolean? = null

    init {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
        } else {
            viewModelScope.launch {
                _stateLiveData.value = _stateLiveData.value!!.loadingReview()

                val result: GeideaResult<ReviewTransactionResponse> = responseAsResult {
                    selectedPlan = souhoolaSharedViewModel.selectedInstallmentPlan!!
                    val currency = paymentViewModel.initialPaymentData.currency
                    val reviewRequest = ReviewTransactionRequest {
                        totalAmount = paymentViewModel.initialPaymentData.amount
                        this.currency = currency
                        items = paymentViewModel.initialPaymentData.orderItems?.toSouhoolaOrderItems(currency)

                        customerIdentifier = souhoolaSharedViewModel.customerIdentifier
                        customerPin = souhoolaSharedViewModel.customerPin

                        val verifyResponse = souhoolaSharedViewModel.verifyResponse!!
                        approvedLimit = verifyResponse.approvedLimit
                        outstanding = verifyResponse.outstanding
                        availableLimit = verifyResponse.availableLimit
                        minLoanAmount = verifyResponse.minLoanAmount

                        downPayment = selectedPlan.downPayment
                        tenure = selectedPlan.tenorMonth
                        minimumDownPaymentTenure = selectedPlan.minDownPayment
                        promoCode = selectedPlan.promoCode
                    }
                    souhoolaService.postReviewTransaction(reviewRequest)
                }

                if (result is GeideaResult.Error) {
                    paymentViewModel.setResult(result)
                }

                when (result) {
                    is GeideaResult.Success -> {
                        reviewResponse = result.data
                        with (souhoolaSharedViewModel) {
                            souhoolaTransactionId = reviewResponse.souhoolaTransactionId!!
                            downPayment = reviewResponse.downPayment
                            netAdministrativeFees = reviewResponse.netAdministrativeFees
                        }
                        val showDownPaymentOptions = souhoolaSharedViewModel.upfrontAmount > BigDecimal.ZERO
                                && paymentViewModel.merchantConfiguration.allowCashOnDeliverySouhoola == true
                        _stateLiveData.value = _stateLiveData.value!!.loadedReview(
                            items = createReceiptItems(result.data),
                            isDownPaymentOptionsVisible = showDownPaymentOptions,
                            // If options are present, user must first choose explicitly
                            proceedButtonEnabled = !showDownPaymentOptions || cashOnDelivery != null,
                            proceedButtonTitle = if (hasUpfrontPayment()) {
                                resourceText(R.string.gd_souhoola_btn_proceed_to_down_payment)
                            } else {
                                resourceText(R.string.gd_souhoola_btn_proceed)
                            },
                            stepCount = if (hasUpfrontPayment()) 5 else 4,
                        )
                    }
                    is GeideaResult.NetworkError -> {
                        if (result.hasCode(ErrorCodes.BnplErrorGroup.code, ErrorCodes.BnplErrorGroup.SouhoolaTechnicalFailure) ||
                            result.hasCode(ErrorCodes.BnplErrorGroup.code, ErrorCodes.BnplErrorGroup.DownPaymentOutOfRange) ||
                            result.hasCode(ErrorCodes.BnplErrorGroup.code, ErrorCodes.BnplErrorGroup.DownPaymentLimitsViolated)
                        ) {
                            showSnack(errorSnack(result))
                            _stateLiveData.value = _stateLiveData.value!!.error(getReason(result))
                        } else {
                            paymentViewModel.navigateToReceiptOrFinish()
                        }
                    }
                    is GeideaResult.Error -> {
                        paymentViewModel.navigateToReceiptOrFinish()
                    }
                    is GeideaResult.Cancelled -> {
                    }
                }
            }
        }
    }

    private fun createReceiptItems(reviewResponse: ReviewTransactionResponse): List<ReceiptItem> {
        val items = mutableListOf<ReceiptItem>()

        with(reviewResponse) {

            val currency = paymentViewModel.initialPaymentData.currency
            val firstInstallment = reviewResponse.installments?.firstOrNull()

            // Purchase details label

            items += ReceiptItem.Text(
                text = NativeText.Resource(R.string.gd_souhoola_review_purchase_details),
                textGravity = Gravity.START,
                textStyle = Typeface.BOLD,
                textSize = TEXT_SIZE,
            )

            // Merchant name

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_souhoola_review_merchant_name),
                labelTextSize = TEXT_SIZE,
                value = plainText(paymentViewModel.merchantConfiguration.merchantName ?: "-"),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )

            // Total items in cart

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_souhoola_review_total_cart_count),
                labelTextSize = TEXT_SIZE,
                value = plainText(cartCount.toString()),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )

            // Total amount

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_bnpl_total_amount),
                labelTextSize = TEXT_SIZE,
                value = makeAmountText(paymentViewModel.initialPaymentData.amount, currency),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )

            // Space

            items += ReceiptItem.Spacer(40.dp)

            // Installment plan label

            items += ReceiptItem.Text(
                text = NativeText.Resource(R.string.gd_souhoola_review_installment_plans),
                textGravity = Gravity.START,
                textStyle = Typeface.BOLD,
                textSize = TEXT_SIZE,
            )

            // Financed amount

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_bnpl_financed_amount),
                labelTextSize = TEXT_SIZE,
                value = makeAmountText(loanAmount, currency),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )

            // Tenure

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_bnpl_tenure),
                labelTextSize = TEXT_SIZE,
                value = NativeText.Template.of(R.string.gd_bnpl_n_months, selectedPlan.tenorMonth),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )

            // Installment amount

            firstInstallment?.kstAmt?.let { installmentAmount ->
                val installmentAmountText = currencyFormat(currency).format(installmentAmount)
                items += ReceiptItem.Property(
                    label = NativeText.Resource(R.string.gd_bnpl_installment_amount),
                    labelTextSize = TEXT_SIZE,
                    value = NativeText.Template.of(R.string.gd_bnpl_amount_vary_s, installmentAmountText),
                    valueTextStyle = Typeface.BOLD,
                    valueTextSize = TEXT_SIZE,
                )
            }

            // First installment date

            val firstInstallmentDate: Date? = try {
                firstInstallment?.kstDate?.let(SERVER_DATE_FORMAT::parse)
            } catch (e: ParseException) {
                loge(e.stackTraceToString())
                null
            }

            firstInstallmentDate?.let {
                val dateText = NativeText.Plain(UI_DATE_FORMAT.format(firstInstallmentDate))
                items += ReceiptItem.Property(
                    label = NativeText.Resource(R.string.gd_souhoola_first_installment_date),
                    labelTextSize = TEXT_SIZE,
                    value = dateText,
                    valueTextStyle = Typeface.BOLD,
                    valueTextSize = TEXT_SIZE,
                )
            }

            // Space

            items += ReceiptItem.Spacer(40.dp)

            // Pay Upfront section

            items += ReceiptItem.Text(
                text = NativeText.Resource(R.string.gd_bnpl_pay_upfront),
                textGravity = Gravity.START,
                textStyle = Typeface.BOLD,
                textSize = TEXT_SIZE,
            )

            // Purchase fees

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_bnpl_purchase_fees),
                labelTextSize = TEXT_SIZE,
                value = makeAmountText(netAdministrativeFees, currency),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )

            // Down Payment

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_bnpl_down_payment),
                labelTextSize = TEXT_SIZE,
                value = makeAmountText(downPayment.orZero(), currency),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )

            // Total upfront

            items += ReceiptItem.Property(
                label = NativeText.Resource(R.string.gd_bnpl_total_amount_upfront),
                labelTextSize = TEXT_SIZE,
                value = makeAmountText(netAdministrativeFees.orZero() + downPayment.orZero(), currency),
                valueTextStyle = Typeface.BOLD,
                valueTextSize = TEXT_SIZE,
            )
        }

        return items
    }

    fun onNextButtonClicked() {
        if (!connectivity.isConnected) {
            showSnack(noInternetSnack)
            return
        }

        viewModelScope.launch {
            _stateLiveData.value = _stateLiveData.value!!.selecting()

            val result = responseAsResult {
                val currency = paymentViewModel.initialPaymentData.currency
                val selectPlanRequest = SelectInstallmentPlanRequest {
                    orderId = souhoolaSharedViewModel.orderId
                    totalAmount = paymentViewModel.initialPaymentData.amount
                    this.currency = currency
                    merchantReferenceId = paymentViewModel.initialPaymentData.merchantReferenceId
                    callbackUrl = paymentViewModel.initialPaymentData.callbackUrl
                    billingAddress = paymentViewModel.initialPaymentData.billingAddress
                    shippingAddress = paymentViewModel.initialPaymentData.shippingAddress
                    customerEmail = paymentViewModel.initialPaymentData.customerEmail
                    paymentMethods = paymentViewModel.acceptedCardBrandNames
                    restrictPaymentMethods = paymentViewModel.acceptedCardBrandNames != null

                    source = Source.MOBILE_APP
                    //platform = paymentData.platform       // TODO
                    //statementDescriptor = paymentData.statementDescriptor     // TODO
                    items = paymentViewModel.initialPaymentData.orderItems?.toSouhoolaOrderItems(currency)
                    customerIdentifier = souhoolaSharedViewModel.customerIdentifier
                    customerPin = souhoolaSharedViewModel.customerPin

                    bnplDetails = SouhoolaBnplDetails {
                        tenure = selectedPlan.tenorMonth
                        souhoolaTransactionId = reviewResponse.souhoolaTransactionId!!.toString()
                        totalInvoicePrice = reviewResponse.totalInvoicePrice
                        downPayment = reviewResponse.downPayment
                        loanAmount = reviewResponse.loanAmount
                        netAdminFees = reviewResponse.netAdministrativeFees
                        mainAdminFees = reviewResponse.mainAdministrativeFees
                        annualRate = reviewResponse.annualRate
                    }
                    cashOnDelivery = _stateLiveData.value!!.cashOnDelivery
                }
                souhoolaService.postSelectInstallmentPlan(selectPlanRequest)
            }

            if (result is GeideaResult.Error) {
                paymentViewModel.setResult(result)
            }

            when (result) {
                is GeideaResult.Success -> {
                    val selectPlanResponse = result.data
                    souhoolaSharedViewModel.orderId = selectPlanResponse.orderId!!
                    proceedWithNextStep(selectedPlan, selectPlanResponse)
                }
                is GeideaResult.NetworkError -> {
                    if (result.hasCode(ErrorCodes.BnplErrorGroup.code, ErrorCodes.BnplErrorGroup.SouhoolaTechnicalFailure) ||
                        result.hasCode(ErrorCodes.BnplErrorGroup.code, ErrorCodes.BnplErrorGroup.DownPaymentOutOfRange) ||
                        result.hasCode(ErrorCodes.BnplErrorGroup.code, ErrorCodes.BnplErrorGroup.DownPaymentLimitsViolated)
                    ) {
                        showSnack(errorSnack(result))
                        _stateLiveData.value = _stateLiveData.value!!.error(getReason(result))
                    } else {
                        paymentViewModel.navigateToReceiptOrFinish()
                    }
                }
                is GeideaResult.Error -> {
                    showSnack(errorSnack(result))
                    _stateLiveData.value = _stateLiveData.value!!.error(getReason(result))
                }
                is GeideaResult.Cancelled -> {
                    // Must not reach here
                }
            }
        }
    }

    private fun proceedWithNextStep(selectedPlan: InstallmentPlan, selectPlanResponse: SelectInstallmentPlanResponse) {
        when (selectPlanResponse.nextStep) {
            SelectInstallmentPlanResponse.NextStepProceedWithBnpl -> {
                // Without down payment - proceed to OTP screen
                navigate(
                    SouhoolaReviewFragmentDirections.gdActionGdSouhoolareviewfragmentToGdSouhoolaotpfragment(
                        step = Step(current = 4, stepCount = 4)
                    )
                )
            }
            SelectInstallmentPlanResponse.NextStepProceedWithDownPayment -> {
                // With down payment - proceed to payment options screen
                navigate(
                    SouhoolaReviewFragmentDirections.gdActionGdSouhoolareviewfragmentToGdPaymentoptionsfragment(
                        downPaymentAmount = souhoolaSharedViewModel.downPayment.orZero()
                                + souhoolaSharedViewModel.netAdministrativeFees.orZero(),
                        step = Step(
                            current = 4,
                            stepCount = 5,
                            textResId = R.string.gd_bnpl_process_down_payment
                        )
                    )
                )
            }
            else -> {
                // Must not reach here
            }
        }
    }

    private fun hasUpfrontPayment(): Boolean {
        return reviewResponse.downPayment.orZero() + reviewResponse.netAdministrativeFees.orZero() > BigDecimal.ZERO
    }

    fun onCashOnDeliverySelected(cashOnDelivery: Boolean) {
        this.cashOnDelivery = cashOnDelivery
        _stateLiveData.value = _stateLiveData.value!!
                .withCashOnDelivery(!hasUpfrontPayment() || cashOnDelivery)
    }

    override fun navigateBack() {
        viewModelScope.launch {
            souhoolaSharedViewModel.cancelIfNeeded()
            super.navigateBack()
        }
    }

    companion object {
        private val UI_DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy")
        private val SERVER_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        private const val TEXT_SIZE = 16f
    }
}