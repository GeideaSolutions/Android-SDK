package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.order.RefundRequest
import net.geidea.paymentsdk.model.order.RefundResponse

internal interface RefundService {
    suspend fun postRefund(refundRequest: RefundRequest): RefundResponse
}