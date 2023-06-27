package net.geidea.paymentsdk.internal.ui.fragment.receipt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor

@GeideaSdkInternal
sealed class ReceiptArgs : Parcelable {

    abstract val merchantReferenceId: String?

    @Parcelize
    data class Success(
        val paymentMethodDescriptor: PaymentMethodDescriptor,
        val orderId: String,
        override var merchantReferenceId: String?
    ) : ReceiptArgs()

    @Parcelize
    data class Error(
        val paymentMethodDescriptor: PaymentMethodDescriptor,
        val reason: CharSequence,
        val orderId: String?,
        override var merchantReferenceId: String?
    ) : ReceiptArgs()
}