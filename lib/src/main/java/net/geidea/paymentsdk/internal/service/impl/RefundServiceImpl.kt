package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.RefundService
import net.geidea.paymentsdk.model.order.RefundRequest
import net.geidea.paymentsdk.model.order.RefundResponse

internal class RefundServiceImpl(private val client: HttpsClient) : RefundService {

    override suspend fun postRefund(refundRequest: RefundRequest): RefundResponse {
        return client.post<RefundRequest, RefundResponse>(
                path = "/pgw/api/v1/direct/refund",
                body = refundRequest,
        ).unwrapOrThrow()
    }
}