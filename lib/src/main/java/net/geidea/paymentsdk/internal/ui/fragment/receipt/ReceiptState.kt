package net.geidea.paymentsdk.internal.ui.fragment.receipt

import androidx.annotation.DrawableRes
import net.geidea.paymentsdk.internal.util.NativeText

internal sealed interface ReceiptState {
    object Loading : ReceiptState

    data class TransactionSuccess(
            val statusText: NativeText,
            @DrawableRes val statusImage: Int,
            val items: List<ReceiptItem>
    ) : ReceiptState

    data class TransactionError(
            val statusText: NativeText,
            @DrawableRes val statusImage: Int,
            val items: List<ReceiptItem>
    ) : ReceiptState

    object LoadingError : ReceiptState
}
