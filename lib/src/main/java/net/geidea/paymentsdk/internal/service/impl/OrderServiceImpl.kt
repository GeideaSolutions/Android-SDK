package net.geidea.paymentsdk.internal.service.impl

import android.net.Uri
import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.OrderService
import net.geidea.paymentsdk.model.order.OrderResponse
import net.geidea.paymentsdk.model.order.OrderSearchRequest
import net.geidea.paymentsdk.model.order.OrderSearchResponse

internal class OrderServiceImpl(private val client: HttpsClient) : OrderService {

    override suspend fun getOrder(orderId: String): OrderResponse {
        return client.get<OrderResponse>(path = "/pgw/api/v1/direct/order/$orderId").unwrapOrThrow()
    }

    override suspend fun getOrders(orderSearchRequest: OrderSearchRequest): OrderSearchResponse = with(orderSearchRequest) {
        val uriBuilder: Uri.Builder = Uri.Builder()
        uriBuilder.appendEncodedPath("pgw/api/v1/direct/order")
        if (status != null) {
            uriBuilder.appendQueryParameter("status", status)
        }
        detailedStatuses.forEach { detailedStatus ->
            uriBuilder.appendQueryParameter("detailedStatuses", detailedStatus)
        }
        if (fromDate != null) {
            uriBuilder.appendQueryParameter("fromDate", fromDate)
        }
        if (toDate != null) {
            uriBuilder.appendQueryParameter("toDate", toDate)
        }
        if (skip != null) {
            uriBuilder.appendQueryParameter("skip", skip.toString())
        }
        if (take != null) {
            uriBuilder.appendQueryParameter("take", take.toString())
        }

        val url = uriBuilder.build().toString()

        return client.get<OrderSearchResponse>(path = url).unwrapOrThrow()
    }
}