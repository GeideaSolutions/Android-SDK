package net.geidea.paymentsdk.internal.ui.fragment.qr.r2p

import net.geidea.paymentsdk.GeideaSdkInternal

@Deprecated("From the old flow which is not used anymore")
@GeideaSdkInternal
internal sealed interface MeezaQrRequestPaymentState {
    object Sending : MeezaQrRequestPaymentState
    object Success : MeezaQrRequestPaymentState
    object Error : MeezaQrRequestPaymentState
}