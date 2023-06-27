package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.PaymentIntentService
import net.geidea.paymentsdk.model.paymentintent.PaymentIntentResponse

internal class PaymentIntentServiceImpl(private val client: HttpsClient) : PaymentIntentService {

    override suspend fun getPaymentIntent(paymentIntentId: String): PaymentIntentResponse {
        return client.get<PaymentIntentResponse>(
                path = "/payment-intent/api/v1/paymentIntent/$paymentIntentId",
        ).unwrapOrThrow()
    }
}