package net.geidea.paymentsdk.model.receipt

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Merchant(
        val referenceId: String?,
        val name: String?,
        val nameAr: String?,
        val vatNumber: String?,
        val vatNumberAr: String?,
) : Parcelable