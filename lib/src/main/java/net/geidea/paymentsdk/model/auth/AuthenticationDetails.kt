package net.geidea.paymentsdk.model.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import net.geidea.paymentsdk.internal.serialization.decodeFromJson
import net.geidea.paymentsdk.internal.serialization.encodeToJson
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import java.util.*

@Parcelize
@Serializable
class AuthenticationDetails

internal constructor(
        val acsEci: String? = null,
        val authenticationToken: String? = null,
        val paResStatus: String? = null,
        val veResEnrolled: String? = null,
        val xid: String? = null,
        val accountAuthenticationValue: String? = null,
        val proofXml: String? = null,
) : GeideaJsonObject, Parcelable {

    override fun toJson(pretty: Boolean): String = encodeToJson(pretty)

    // GENERATED
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthenticationDetails

        if (acsEci != other.acsEci) return false
        if (authenticationToken != other.authenticationToken) return false
        if (paResStatus != other.paResStatus) return false
        if (veResEnrolled != other.veResEnrolled) return false
        if (xid != other.xid) return false
        if (accountAuthenticationValue != other.accountAuthenticationValue) return false
        if (proofXml != other.proofXml) return false

        return true
    }

    // GENERATED
    override fun hashCode(): Int {
        return Objects.hash(
                acsEci,
                authenticationToken,
                paResStatus,
                veResEnrolled,
                xid,
                accountAuthenticationValue,
                proofXml
        )
    }

    // GENERATED
    override fun toString(): String {
        return "AuthenticationDetails(acsEci=$acsEci, authenticationToken=$authenticationToken, paResStatus=$paResStatus, veResEnrolled=$veResEnrolled, xid=$xid, accountAuthenticationValue=$accountAuthenticationValue, proofXml=$proofXml)"
    }

    companion object {
        @JvmStatic
        fun fromJson(json: String): AuthenticationDetails = decodeFromJson(json)
    }
}