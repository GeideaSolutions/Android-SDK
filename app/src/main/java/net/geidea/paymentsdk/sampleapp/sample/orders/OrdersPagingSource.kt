package net.geidea.paymentsdk.sampleapp.sample.orders

import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.model.order.OrderSearchRequest
import net.geidea.paymentsdk.model.order.OrderSearchResponse
import net.geidea.paymentsdk.sampleapp.sample.GeideaPagingSource
import net.geidea.paymentsdk.sampleapp.sample.Page

class OrdersPagingSource : GeideaPagingSource<OrderSearchRequest, Order>(
        pageLoader = { request ->
            when (val result = GatewayApi.getOrders(request)) {
                is GeideaResult.Success<OrderSearchResponse> ->
                    Page(
                            items = result.data.orders ?: emptyList(),
                            totalCount = result.data.totalCount ?: 0
                    )
                else -> error(result.toString())
            }
        }
)