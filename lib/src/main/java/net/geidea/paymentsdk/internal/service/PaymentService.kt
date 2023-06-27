package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.pay.PaymentRequest

internal interface PaymentService {
    suspend fun postPay(paymentRequest: PaymentRequest): OrderResponse
}