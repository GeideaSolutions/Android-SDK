package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.PaymentService
import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.pay.PaymentRequest

internal class PaymentServiceImpl(private val client: HttpsClient) : PaymentService {

    override suspend fun postPay(paymentRequest: PaymentRequest): OrderResponse {
        return client.post<PaymentRequest, OrderResponse>(
                path = "/pgw/api/v2/direct/pay",
                body = paymentRequest,
        ).unwrapOrThrow()
    }
}