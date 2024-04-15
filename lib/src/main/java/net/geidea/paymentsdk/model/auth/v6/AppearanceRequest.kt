package net.geidea.paymentsdk.model.auth.v6

import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.Objects

@Serializable
class AppearanceRequest internal constructor(
    val showEmail: Boolean? = false,
    val showAddress: Boolean? = false,
    val showPhone: Boolean? = false,
    val receiptPage: Boolean? = false,
    val styles: StylesRequest? = null,
    val uiMode: String? = null
) : GeideaJsonObject {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppearanceRequest

        if (showEmail != other.showEmail) return false
        if (showAddress != other.showAddress) return false
        if (showPhone != other.showPhone) return false
        if (receiptPage != other.receiptPage) return false
        if (styles != other.styles) return false
        return uiMode == other.uiMode
    }

    override fun hashCode(): Int {
        return Objects.hash(
            showEmail,
            showAddress,
            showPhone,
            receiptPage,
            styles,
            uiMode
        )
    }

    override fun toString(): String {
        return "AppearanceRequest(showEmail=$showEmail, showAddress=$showAddress, showPhone=$showPhone, receiptPage=$receiptPage, styles=$styles, uiMode=$uiMode)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): AppearanceRequest = decodeFromJson(json)
    }

    class Builder() {
        @set:JvmSynthetic
        var showEmail: Boolean? = false

        @set:JvmSynthetic
        var showAddress: Boolean? = false

        @set:JvmSynthetic
        var showPhone: Boolean? = false

        @set:JvmSynthetic
        var receiptPage: Boolean? = false

        @set:JvmSynthetic
        var styles: StylesRequest? = null

        @set:JvmSynthetic
        var uiMode: String? = null


        fun build() = AppearanceRequest(
            showEmail = this.showEmail,
            showAddress = this.showAddress,
            showPhone = this.showPhone,
            receiptPage = this.receiptPage,
            styles = this.styles,
            uiMode = this.uiMode
        )
    }
}

@JvmSynthetic
fun AppearanceRequest(initializer: AppearanceRequest.Builder.() -> Unit): AppearanceRequest {
    return AppearanceRequest.Builder().apply(initializer).build()
}


@Serializable
class StylesRequest internal constructor(
    val hideGeideaLogo: Boolean? = false
) : GeideaJsonObject {
    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)
    override fun toString(): String {
        return "StylesRequest(hideGeideaLogo=$hideGeideaLogo)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StylesRequest

        return hideGeideaLogo == other.hideGeideaLogo
    }

    override fun hashCode(): Int {
        return Objects.hash(hideGeideaLogo)
    }


    class Builder() {

        @set:JvmSynthetic
        var hideGeideaLogo: Boolean? = false

        fun build() = StylesRequest(
            hideGeideaLogo = this.hideGeideaLogo
        )

    }
}

@JvmSynthetic
fun StylesRequest(initializer: StylesRequest.Builder.() -> Unit): StylesRequest {
    return StylesRequest.Builder().apply(initializer).build()
}