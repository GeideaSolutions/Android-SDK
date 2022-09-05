package net.geidea.paymentsdk.sampleapp.sample

import android.annotation.SuppressLint
import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.geidea.paymentsdk.model.common.PaginatedRequest
import java.text.SimpleDateFormat
import java.util.*

/**
 * [PagingSource] for loading [PaginatedRequest]s.
 *
 * @param K paginated request type used as a key
 * @param V type of page items
 */
open class GeideaPagingSource<K : PaginatedRequest<K>, V : Any>(
        /**
         * Suspend function that load page items. In case of error it must throw exception with an
         * appropriate message.
         */
        val pageLoader: suspend (K) -> Page<V>
) : PagingSource<K, V>() {

    private var firstPageRequest: K? = null

    var totalCount: Int = 0
        private set

    override suspend fun load(
            params: LoadParams<K>
    ): LoadResult<K, V> {

        return try {
            // Key to the current page
            val key: K
            if (params is LoadParams.Refresh) {
                key = params.key!!.mutate(take = params.loadSize)
                firstPageRequest = key
            } else {
                key = params.key!!
            }

            val page: Page<V> = pageLoader(key)
            val noMore = page.items.isEmpty()
            totalCount = page.totalCount
            LoadResult.Page(
                    data = page.items,
                    prevKey = null,
                    nextKey = if (noMore) {
                        null
                    } else {
                        key.mutate(skip = (key.skip ?: 0) + params.loadSize)
                    },
            )
        } catch (e: Exception) {
            totalCount = 0
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<K, V>): K? {
        return firstPageRequest?.mutate(skip = 0)
    }

    companion object {
        /**
         * Preferred page size (1..100).
         */
        internal const val PAGE_SIZE = 20

        @SuppressLint("SimpleDateFormat", "ConstantLocale")
        internal val DATE_FORMAT_SERVER = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
}

data class Page<T>(val items: List<T>, val totalCount: Int)