package net.geidea.paymentsdk.sampleapp.sample.orders

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.geidea.paymentsdk.GeideaPaymentAPI
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.Order
import net.geidea.paymentsdk.model.OrderSearchRequest
import net.geidea.paymentsdk.model.OrderSearchResponse
import java.text.SimpleDateFormat
import java.util.*

class OrdersPagingSource : PagingSource<OrderSearchRequest, Order>() {

    var searchRequest: OrderSearchRequest? = null

    var totalCount: Int = 0
        private set

    override suspend fun load(
            params: LoadParams<OrderSearchRequest>
    ): LoadResult<OrderSearchRequest, Order> {

        if (params is LoadParams.Refresh) {
            searchRequest = params.key!!.copy(take = params.loadSize)
        }

        return try {
            val key = params.key!!
            when (val result: GeideaResult<OrderSearchResponse> = GeideaPaymentAPI.getOrders(key)) {
                is GeideaResult.Success<OrderSearchResponse> -> {
                    totalCount = result.data.totalCount ?: -1
                    val orders = result.data.orders ?: emptyList()
                    val noMore = orders.isEmpty()
                    LoadResult.Page(
                            data = orders,
                            prevKey = null,
                            nextKey = if (noMore) {
                                null
                            } else {
                                key.copy(skip = (key.skip ?: 0) + params.loadSize)
                            },
                    )
                }
                else -> {
                    totalCount = 0
                    LoadResult.Error(RuntimeException(result.toString()))
                }
            }
        } catch (e: Exception) {
            totalCount = 0
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<OrderSearchRequest, Order>): OrderSearchRequest? {
        return searchRequest?.copy(skip = 0)
    }

    companion object {
        const val PAGE_SIZE = 20

        @SuppressLint("SimpleDateFormat", "ConstantLocale")
        val DATE_FORMAT_SERVER = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
}