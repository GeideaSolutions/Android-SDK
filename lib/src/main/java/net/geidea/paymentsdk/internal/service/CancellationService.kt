package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.order.CancelRequest
import net.geidea.paymentsdk.model.pay.PaymentResponse

internal interface CancellationService {
    suspend fun postCancel(cancelRequest: CancelRequest): PaymentResponse
}