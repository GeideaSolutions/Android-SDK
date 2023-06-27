package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.CaptureService
import net.geidea.paymentsdk.model.order.CaptureRequest
import net.geidea.paymentsdk.model.order.OrderResponse

internal class CaptureServiceImpl(private val client: HttpsClient) : CaptureService {

    override suspend fun postCapture(captureRequest: CaptureRequest): OrderResponse {
        return client.post<CaptureRequest, OrderResponse>(
                path = "/pgw/api/v1/direct/capture",
                body = captureRequest,
        ).unwrapOrThrow()
    }
}