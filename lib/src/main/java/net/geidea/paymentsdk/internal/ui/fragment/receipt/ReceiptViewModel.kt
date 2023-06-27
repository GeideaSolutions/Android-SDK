package net.geidea.paymentsdk.internal.ui.fragment.receipt

import android.graphics.Typeface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.service.receipt.ReceiptService
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.ui.fragment.base.NavigationCommand
import net.geidea.paymentsdk.internal.ui.fragment.base.noInternetSnack
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.PaymentType
import net.geidea.paymentsdk.model.receipt.Receipt
import net.geidea.paymentsdk.model.receipt.ReceiptResponse
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.getBnplPaymentMethodBy
import java.util.*

@GeideaSdkInternal
internal class ReceiptViewModel(
        private val receiptService: ReceiptService,
        private val connectivity: NetworkConnectivity,
        private val receiptArgs: ReceiptArgs,
) : BaseViewModel() {

    private val _stateLiveData = MutableLiveData<ReceiptState>()
    val stateLiveData: LiveData<ReceiptState> = _stateLiveData

    init {
        loadReceiptAndFinishWithDelay()
    }

    fun onTryAgainButtonClicked() {
        loadReceiptAndFinishWithDelay()
    }

    fun onReturnButtonClicked() {
        navigate(NavigationCommand.Finish)
    }

    private fun loadReceiptAndFinishWithDelay() {
        viewModelScope.launch {
            if (!connectivity.isConnected) {
                showSnack(noInternetSnack)
                _stateLiveData.value = ReceiptState.LoadingError

            } else {

                when (receiptArgs) {
                    is ReceiptArgs.Success -> {
                        _stateLiveData.value = ReceiptState.Loading
                        val result: GeideaResult<ReceiptResponse> = responseAsResult {
                            receiptService.getOrderReceipt(orderId = receiptArgs.orderId)
                        }

                        when (result) {
                            is GeideaResult.Success -> {
                                val receipt: Receipt = result.data.receipt!!

                                _stateLiveData.value = ReceiptState.TransactionSuccess(
                                    statusText = makeStatusText(receipt),
                                    statusImage = R.drawable.gd_ic_blue_checkmark_rounded,
                                    items = createSuccessReceiptItems(receipt)
                                )
                            }
                            else -> {
                                ReceiptState.LoadingError
                            }
                        }
                    }
                    is ReceiptArgs.Error -> {
                        emitErrorState(receiptArgs)
                    }
                }
            }

            delay(AUTO_DISMISS_DELAY)
            navigate(NavigationCommand.Finish)
        }
    }

    private fun emitErrorState(errorArgs: ReceiptArgs.Error) {
        var reason = if (errorArgs.reason.toString() == "Job was cancelled") {
            resourceText(R.string.gd_cancelled_by_user)
        } else {
            plainText(errorArgs.reason ?: "Unknown error")
        }
        _stateLiveData.value = ReceiptState.TransactionError(
                statusText = NativeText.Resource(R.string.gd_transation_failed),
                statusImage = R.drawable.gd_ic_x_circle,
                items = createFailureReceiptItems(
                        orderId = errorArgs.orderId,
                        merchantReferenceId = errorArgs.merchantReferenceId,
                        reason = reason,
                        paymentMethodDescriptor = errorArgs.paymentMethodDescriptor,
                )
        )
    }

    private fun createSuccessReceiptItems(receipt: Receipt): List<ReceiptItem> {
        val receiptItems = mutableListOf<ReceiptItem>()

        val mainPaymentType: String? = when {
            receipt.bnplDetails != null -> PaymentType.BNPL
            receipt.paymentMethod?.type == PaymentType.CARD -> PaymentType.CARD
            receipt.paymentMethod?.type == PaymentType.QR -> PaymentType.QR
            else -> null
        }

        when (mainPaymentType) {
            PaymentType.QR -> {
                receipt.paymentDate?.let { date ->
                    val localDateTimeText = NativeText.Plain(date.toDate().formatWith(DATE_FORMAT))
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_date_time), localDateTimeText)
                }
                receipt.meezaDetails?.type?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_operation), NativeText.Plain(it))
                }
                receipt.meezaDetails?.receiverId?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_mobile_or_digital_id), NativeText.Plain(it))
                }
                receipt.merchant?.name?.takeIf(String::isNotBlank)?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_username), NativeText.Plain(it))
                }
                receipt.orderId.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_geidea_order_id), NativeText.Plain(it))
                }
                receipt.merchant?.referenceId?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_merchant_ref_id), NativeText.Plain(it))
                }
                receipt.meezaDetails?.meezaTransactionId?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_meeza_transaction_id), NativeText.Plain(lastNDigitsOf(it, LAST_DIGITS_TO_SHOW)))
                }

                receiptItems += ReceiptItem.Divider()

                receiptItems += ReceiptItem.Property(
                        label = NativeText.Resource(R.string.gd_label_total),
                        value = makeAmountText(receipt.amount, receipt.currency),
                        labelTextStyle = Typeface.BOLD,
                        labelTextSize = 16f,
                )
            }
            PaymentType.CARD -> {
                receipt.paymentDate?.let { date ->
                    val localDateTimeText = NativeText.Plain(date.toDate().formatWith(DATE_FORMAT))
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_date_time), localDateTimeText)
                }
                receipt.paymentOperation?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_operation), NativeText.Plain(it))
                }
                receipt.orderId.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_geidea_order_id), NativeText.Plain(it))
                }
                receipt.merchant?.referenceId?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_merchant_ref_id), NativeText.Plain(it))
                }

                receiptItems += ReceiptItem.Divider()

                receiptItems += ReceiptItem.Property(
                        label = NativeText.Resource(R.string.gd_label_total),
                        value = makeAmountText(receipt.amount, receipt.currency),
                        labelTextStyle = Typeface.BOLD,
                        labelTextSize = 16f,
                )
            }
            PaymentType.BNPL -> {
                val bnplPaymentMethod: BnplPaymentMethodDescriptor? = receipt.bnplDetails?.provider?.let(::getBnplPaymentMethodBy)

                receipt.paymentDate?.let { date ->
                    val localDateTimeText = NativeText.Plain(date.toDate().formatWith(DATE_FORMAT))
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_date_time), localDateTimeText)
                }
                receipt.paymentOperation?.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_operation), NativeText.Plain(it))
                }
                receipt.orderId.let {
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_geidea_order_id), NativeText.Plain(it))
                }

                receiptItems += ReceiptItem.Spacer()

                receipt.bnplDetails?.totalAmount?.let {
                    val text = currencyFormat(receipt.currency).format(it)
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_bnpl_total_amount), NativeText.Plain(text))
                }

                receipt.bnplDetails?.financedAmount?.let {
                    val text = currencyFormat(receipt.currency).format(it)
                    receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_bnpl_financed_amount), NativeText.Plain(text))
                }

                receipt.bnplDetails?.installmentAmount?.let {
                    val text = currencyFormat(receipt.currency).format(it)
                    receiptItems += ReceiptItem.Property(
                            label = NativeText.Resource(R.string.gd_bnpl_installment_amount),
                            value = NativeText.Template.of(R.string.gd_bnpl_amount_vary_s, text)
                    )
                }

                receiptItems += ReceiptItem.Divider()

                receipt.bnplDetails?.tenure?.let { tenure ->
                    receiptItems += ReceiptItem.Property(
                            label = NativeText.Resource(R.string.gd_bnpl_tenure),
                            value = NativeText.Template.of(R.string.gd_bnpl_n_months, tenure)
                    )
                }

                receiptItems += ReceiptItem.Property(
                        label = NativeText.Resource(R.string.gd_bnpl_down_payment),
                        value = makeAmountText(receipt.bnplDetails?.downPayment, receipt.currency)
                )

                if (bnplPaymentMethod == BnplPaymentMethodDescriptor.ValuInstallments) {
                    receiptItems += ReceiptItem.Property(
                        label = NativeText.Resource(R.string.gd_valu_to_u_balance),
                        value = makeAmountText(
                            receipt.bnplDetails?.giftCardAmount,
                            receipt.currency
                        )
                    )


                    receiptItems += ReceiptItem.Property(
                        label = NativeText.Resource(R.string.gd_valu_cashback_amount),
                        value = makeAmountText(
                            receipt.bnplDetails?.campaignAmount,
                            receipt.currency
                        )
                    )
                }

                receipt.bnplDetails?.adminFees?.let {
                    val text = currencyFormat(receipt.currency).format(it)
                    receiptItems += ReceiptItem.Property(
                            label = NativeText.Resource(R.string.gd_bnpl_purchase_fees),
                            value = NativeText.Plain(text)
                    )
                }

                receipt.bnplDetails?.providerTransactionId?.let { providerTransactionId ->
                    receiptItems += ReceiptItem.Property(
                            label = templateText(R.string.gd_bnpl_provider_transaction_id_s, bnplPaymentMethod?.nameShort?.let(::resourceText) ?: "BNPL Provider"),
                            value = plainText(providerTransactionId)
                    )
                }

                receiptItems += ReceiptItem.Divider()

                if (bnplPaymentMethod == BnplPaymentMethodDescriptor.ValuInstallments) {
                    receiptItems += ReceiptItem.Text(
                        text = NativeText.Resource(R.string.gd_valu_call_support),
                    )
                }

                receiptItems += ReceiptItem.Spacer(24.dp)

                // Logo

                bnplPaymentMethod?.embeddableLogo?.let {
                    receiptItems += ReceiptItem.Image(it)
                }
            }
        }

        return receiptItems
    }

    private fun createFailureReceiptItems(
        orderId: String?,
        merchantReferenceId: String?,
        reason: NativeText,
        paymentMethodDescriptor: PaymentMethodDescriptor?,
    ): List<ReceiptItem> {

        val receiptItems = mutableListOf<ReceiptItem>()

        val dateTimeText = NativeText.Plain(Date().formatWith(DATE_FORMAT))
        receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_date_time), dateTimeText)

        orderId?.let {
            receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_geidea_order_id), NativeText.Plain(it))
        }
        merchantReferenceId?.let {
            receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_merchant_ref_id), NativeText.Plain(it))
        }
        receiptItems += ReceiptItem.Property(NativeText.Resource(R.string.gd_label_failure_reason), reason)

        receiptItems += ReceiptItem.Spacer(48.dp)

        val logoResId: Int = when (paymentMethodDescriptor) {
            BnplPaymentMethodDescriptor.ValuInstallments -> R.drawable.gd_ic_valu_logo
            else -> 0
        }
        if (logoResId != 0) {
            receiptItems += ReceiptItem.Image(logoResId)
        }

        return receiptItems
    }

    private fun makeStatusText(receipt: Receipt): NativeText {
        return when (receipt.paymentMethod?.type) {
            PaymentType.BNPL -> {
                templateText(R.string.gd_bnpl_purchase_with_s_confirmed,
                    receipt.bnplDetails?.provider?.let(::getBnplPaymentMethodBy)?.nameShort?.let(::resourceText)
                        ?: "BNPL Provider"
                )
            }
            else -> NativeText.Resource(R.string.gd_transation_approved)
        }
    }

    companion object {
        private const val AUTO_DISMISS_DELAY = 15_000L

        private const val DATE_FORMAT = "dd.MM.yyyy | HH:mm"

        private const val LAST_DIGITS_TO_SHOW = 7

        private const val LENGTH_GUID = 36

        private fun lastNDigitsOf(guid: String, n: Int): String {
            return if (guid.length > LENGTH_GUID - n) {
                guid.substring(LENGTH_GUID - n)
            } else {
                guid
            }
        }
    }
}