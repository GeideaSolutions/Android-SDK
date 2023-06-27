package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.order.OrderSearchRequest
import net.geidea.paymentsdk.model.order.OrderSearchResponse

internal interface OrderService {
    suspend fun getOrder(orderId: String): OrderResponse
    suspend fun getOrders(orderSearchRequest: OrderSearchRequest): OrderSearchResponse
}