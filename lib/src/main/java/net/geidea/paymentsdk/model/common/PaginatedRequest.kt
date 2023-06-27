package net.geidea.paymentsdk.model.common

/**
 * Represents a paginated network request used to retrieve a list of items page by page.
 */
interface PaginatedRequest<T> {
    /**
     * Number of items to be skipped to reach the page first item.
     */
    val skip: Int?

    /**
     * Maximal number of page items to be returned. Valid value is in the range [1..100].
     */
    val take: Int?

    /**
     * Creates a copy of this request with mutated properties.
     */
    fun mutate(skip: Int? = null, take: Int? = null): T
}
