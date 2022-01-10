package net.geidea.paymentsdk.sampleapp.sample.orders

import net.geidea.paymentsdk.GeideaPaymentAPI
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.Order
import net.geidea.paymentsdk.model.OrderSearchRequest
import net.geidea.paymentsdk.model.OrderSearchResponse
import net.geidea.paymentsdk.sampleapp.sample.GeideaPagingSource
import net.geidea.paymentsdk.sampleapp.sample.Page

class OrdersPagingSource : GeideaPagingSource<OrderSearchRequest, Order>(
        pageLoader = { request ->
            when (val result = GeideaPaymentAPI.getOrders(request)) {
                is GeideaResult.Success<OrderSearchResponse> ->
                    Page(
                            items = result.data.orders ?: emptyList(),
                            totalCount = result.data.totalCount ?: 0
                    )
                else -> error(result.toString())
            }
        }
)