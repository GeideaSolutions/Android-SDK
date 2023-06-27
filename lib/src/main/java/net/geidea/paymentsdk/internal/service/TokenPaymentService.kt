package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.pay.TokenPaymentRequest

internal interface TokenPaymentService {
    suspend fun postPayWithToken(tokenPaymentRequest: TokenPaymentRequest): OrderResponse
}