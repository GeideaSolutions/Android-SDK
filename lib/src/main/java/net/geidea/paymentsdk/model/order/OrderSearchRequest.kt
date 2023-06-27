package net.geidea.paymentsdk.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.LocalizableRequest
import net.geidea.paymentsdk.model.common.PaginatedRequest
import java.math.BigDecimal
import java.util.*

@Serializable
@Parcelize
class OrderSearchRequest(
        override var language: String? = null,
        val status: String? = null,
        val detailedStatuses: Set<String> = emptySet(),
        val fromDate: String? = null,
        val toDate: String? = null,
        override val skip: Int? = null,
        override val take: Int? = null,
) : LocalizableRequest, PaginatedRequest<OrderSearchRequest>, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    fun copy(
            language: String? = null,
            status: String? = null,
            detailedStatuses: Set<String>? = null,
            fromDate: String? = null,
            toDate: String? = null,
            skip: Int? = null,
            take: Int? = null,
    ): OrderSearchRequest {
        return OrderSearchRequest {
            this.language = language ?: this@OrderSearchRequest.language
            this.status = status ?: this@OrderSearchRequest.status
            this.detailedStatuses = detailedStatuses ?: this@OrderSearchRequest.detailedStatuses
            this.fromDate = fromDate ?: this@OrderSearchRequest.fromDate
            this.toDate = toDate ?: this@OrderSearchRequest.toDate
            this.skip = skip ?: this@OrderSearchRequest.skip
            this.take = take ?: this@OrderSearchRequest.take
        }
    }

    override fun mutate(skip: Int?, take: Int?): OrderSearchRequest {
        return copy(skip = skip, take = take)
    }

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderSearchRequest

        if (language != other.language) return false
        if (status != other.status) return false
        if (detailedStatuses != other.detailedStatuses) return false
        if (fromDate != other.fromDate) return false
        if (toDate != other.toDate) return false
        if (skip != other.skip) return false
        if (take != other.take) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                language,
                status,
                detailedStatuses,
                fromDate,
                toDate,
                skip,
                take,
        )
    }

    // GENERATED
    override fun toString(): String {
        return "OrderSearchRequest(status=$status, detailedStatuses=$detailedStatuses, fromDate=$fromDate, toDate=$toDate, skip=$skip, take=$take)"
    }

    class Builder {
        @set:JvmSynthetic // Hide 'void' setter from Java
        var language: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var status: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var detailedStatuses: Set<String>? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var fromDate: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var toDate: String? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var skip: Int? = null

        @set:JvmSynthetic // Hide 'void' setter from Java
        var take: Int? = null

        fun setLanguage(language: String?): Builder = apply { this.language = language }
        fun setStatus(amount: BigDecimal?): Builder = apply { this.status = status }
        fun setDetailedStatuses(detailedStatuses: Set<String>?): Builder = apply { this.detailedStatuses = detailedStatuses }
        fun setFromDate(fromDate: String?): Builder = apply { this.fromDate = fromDate }
        fun setToDate(toDate: String?): Builder = apply { this.toDate = toDate }
        fun setSkip(skip: Int?): Builder = apply { this.skip = skip }
        fun setTake(take: Int?): Builder = apply { this.take = take }

        fun build(): OrderSearchRequest {
            return OrderSearchRequest(
                    language = this.language,
                    status = this.status,
                    detailedStatuses = this.detailedStatuses ?: emptySet(),
                    toDate = this.toDate,
                    fromDate = this.fromDate,
                    skip = this.skip,
                    take = this.take,
            )
        }
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): OrderSearchRequest = decodeFromJson(json)
    }
}

/**
 * Kotlin builder function.
 */
@Suppress("FunctionName")
@JvmSynthetic // Hide from Java callers who should use Builder.
fun OrderSearchRequest(initializer: OrderSearchRequest.Builder.() -> Unit): OrderSearchRequest {
    return OrderSearchRequest.Builder().apply(initializer).build()
}