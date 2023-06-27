package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.review

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.ui.fragment.receipt.ReceiptItem
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.emptyText
import net.geidea.paymentsdk.internal.util.resourceText

@GeideaSdkInternal
internal data class SouhoolaReviewState(
    val items: List<ReceiptItem>,
    val isDownPaymentOptionsVisible: Boolean,
    val progressVisible: Boolean,
    val proceedButtonEnabled: Boolean,
    val proceedButtonTitle: NativeText,
    val proceedButtonProgressVisible: Boolean,
    val stepCount: Int,
    val errorMessage: NativeText,
    val cashOnDelivery: Boolean,
)

internal val Initial = SouhoolaReviewState(
    items = emptyList(),
    isDownPaymentOptionsVisible = false,
    progressVisible = false,
    proceedButtonEnabled = false,
    proceedButtonTitle = resourceText(R.string.gd_souhoola_btn_proceed),
    proceedButtonProgressVisible = false,
    stepCount = 4,
    errorMessage = emptyText(),
    cashOnDelivery = false,
)

// Mutators

internal fun SouhoolaReviewState.loadingReview() = copy(
    progressVisible = true,
    proceedButtonEnabled = false,
)

internal fun SouhoolaReviewState.loadedReview(
    items: List<ReceiptItem>,
    isDownPaymentOptionsVisible: Boolean,
    proceedButtonEnabled: Boolean,
    proceedButtonTitle: NativeText,
    stepCount: Int,
) = copy(
    items = items,
    isDownPaymentOptionsVisible = isDownPaymentOptionsVisible,
    proceedButtonEnabled = proceedButtonEnabled,
    proceedButtonTitle = proceedButtonTitle,
    progressVisible = false,
    stepCount = stepCount,
)

internal fun SouhoolaReviewState.selecting() = copy(
    proceedButtonProgressVisible = true,
    proceedButtonEnabled = false,
)

internal fun SouhoolaReviewState.withCashOnDelivery(cashOnDelivery: Boolean) = copy(
    cashOnDelivery = cashOnDelivery,
    proceedButtonTitle = if (cashOnDelivery) {
        resourceText(R.string.gd_souhoola_btn_proceed)
    } else {
        resourceText(R.string.gd_souhoola_btn_proceed_to_down_payment)
    },
    proceedButtonEnabled = true,
    stepCount = if (cashOnDelivery) 4 else 5,
)

internal fun SouhoolaReviewState.error(errorMessage: NativeText) = copy(
    errorMessage = errorMessage,
    proceedButtonProgressVisible = false,
    proceedButtonEnabled = true,
)