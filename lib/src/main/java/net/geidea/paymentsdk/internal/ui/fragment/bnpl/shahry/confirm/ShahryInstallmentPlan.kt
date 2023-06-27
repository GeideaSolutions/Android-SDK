package net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.confirm

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.model.bnpl.BnplDetails
import java.math.BigDecimal

@Parcelize
internal data class ShahryInstallmentPlan(
    val purchaseFees: BigDecimal = BigDecimal.ZERO,
    val downPayment: BigDecimal = BigDecimal.ZERO,
    val totalUpfront: BigDecimal = BigDecimal.ZERO,
    val requiresDownPayment: Boolean = totalUpfront > BigDecimal.ZERO
) : Parcelable {

    companion object {
        internal fun makeShahryPlan(bnplDetails: BnplDetails): ShahryInstallmentPlan {
            val fees = (bnplDetails.adminFees ?: BigDecimal.ZERO) + (bnplDetails.otherFees ?: BigDecimal.ZERO)
            val downPayment = (bnplDetails.downPayment ?: BigDecimal.ZERO)
            return ShahryInstallmentPlan(
                purchaseFees = fees,
                downPayment = downPayment,
                totalUpfront = downPayment + fees,
            )
        }
    }
}