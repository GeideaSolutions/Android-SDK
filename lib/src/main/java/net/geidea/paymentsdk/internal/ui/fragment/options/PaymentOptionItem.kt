package net.geidea.paymentsdk.internal.ui.fragment.options

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor

@GeideaSdkInternal
internal sealed interface PaymentOptionItem

@GeideaSdkInternal
internal data class PaymentMethodItem(
    val paymentMethod: PaymentMethodDescriptor,
    val label: NativeText? = null,
) : PaymentOptionItem

@GeideaSdkInternal
internal data class PaymentMethodGroup(
    val items: Set<PaymentMethodItem>,
    val label: NativeText,
) : PaymentOptionItem

@GeideaSdkInternal
internal fun <E : PaymentOptionItem> Collection<E>.flatten(): Set<PaymentMethodItem> {
    return flatMap { item: PaymentOptionItem ->
        when (item) {
            is PaymentMethodItem -> setOf(item)
            is PaymentMethodGroup -> item.items
        }
    }.toSet()
}