package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.TokenPaymentService
import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.pay.TokenPaymentRequest

internal class TokenPaymentServiceImpl(private val client: HttpsClient) : TokenPaymentService {

    override suspend fun postPayWithToken(tokenPaymentRequest: TokenPaymentRequest): OrderResponse {
        return client.post<TokenPaymentRequest, OrderResponse>(
                path = "/pgw/api/v1/direct/pay/token",
                body = tokenPaymentRequest
        ).unwrapOrThrow()
    }
}