package net.geidea.paymentsdk.model.common

import android.os.Parcelable

/**
 * The common set of properties returned in all Geidea HTTP responses.
 */
interface GeideaResponse : Parcelable {
    val responseCode: String?
    val responseMessage: String?
    val detailedResponseCode: String?
    val detailedResponseMessage: String?
    val language: String?

    val isSuccess: Boolean get() = responseCode == "000"
}