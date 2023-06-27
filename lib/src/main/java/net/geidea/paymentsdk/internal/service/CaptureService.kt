package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.order.CaptureRequest
import net.geidea.paymentsdk.model.order.OrderResponse

internal interface CaptureService {
    suspend fun postCapture(captureRequest: CaptureRequest): OrderResponse
}