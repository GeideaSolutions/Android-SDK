package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.CancellationService
import net.geidea.paymentsdk.model.order.CancelRequest
import net.geidea.paymentsdk.model.pay.PaymentResponse

internal class CancellationServiceImpl(private val client: HttpsClient) : CancellationService {

    override suspend fun postCancel(cancelRequest: CancelRequest): PaymentResponse {
        return client.post<CancelRequest, PaymentResponse>(
                path = "/pgw/api/v1/direct/cancel",
                body = cancelRequest,
        ).unwrapOrThrow()
    }
}