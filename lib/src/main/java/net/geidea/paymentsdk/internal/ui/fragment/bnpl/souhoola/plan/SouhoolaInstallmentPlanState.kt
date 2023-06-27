package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.plan

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.orZero
import net.geidea.paymentsdk.model.bnpl.souhoola.InstallmentPlan
import java.math.BigDecimal

@GeideaSdkInternal
internal data class SouhoolaInstallmentPlanState(
    val internalState: InternalState = InternalState.Initial,
    val totalAmount: BigDecimal,
    val installmentPlans: List<InstallmentPlan> = emptyList(),
    val selectedInstallmentPlan: InstallmentPlan? = null,
    val error: NativeText? = null,
    val financedAmount: BigDecimal = BigDecimal.ZERO,
    val upFrontAmount: BigDecimal = BigDecimal.ZERO,
    val downPaymentVisible: Boolean = false,
    val downPaymentHelperText: NativeText? = null,
    val downPaymentError: NativeText? = null,
    val financedAmountError: NativeText? = null,
    val nextButtonEnabled: Boolean = false,
) {
    val stepCount: Int get() = if (upFrontAmount > BigDecimal.ZERO) {
        5   // 1:Phone & PIN, 2:Plan, 3:Review, 4:Select payment method and Pay, 5:OTP
    } else {
        4   // 1:Phone & PIN, 2:Plan, 3:Review, 4:OTP
    }

    companion object {
        fun initial(totalAmount: BigDecimal): SouhoolaInstallmentPlanState =
            SouhoolaInstallmentPlanState(totalAmount = totalAmount)
    }
}

 @GeideaSdkInternal
 internal enum class InternalState {
    Initial,
    Loading,
    Loaded,
    Selected,
    OutOfRange,
    Error
}

// Transition mutators

internal fun SouhoolaInstallmentPlanState.loading(): SouhoolaInstallmentPlanState = copy(
    internalState = InternalState.Loading,
    nextButtonEnabled = false,
)

internal fun SouhoolaInstallmentPlanState.recalculate(
    downPayment: BigDecimal
): SouhoolaInstallmentPlanState = copy(
    financedAmount = (totalAmount - downPayment).takeIf { it > BigDecimal.ZERO }.orZero(),
    upFrontAmount = downPayment + selectedInstallmentPlan?.adminFees.orZero(),
)

internal fun SouhoolaInstallmentPlanState.plansLoaded(
    installmentPlans: List<InstallmentPlan>,
): SouhoolaInstallmentPlanState = copy(
    internalState = InternalState.Loaded,
    installmentPlans = installmentPlans,
    error = null,
    downPaymentError = null,
    financedAmountError = null,
)

internal fun SouhoolaInstallmentPlanState.planSelected(
    selectedInstallmentPlan: InstallmentPlan,
    downPaymentHelperText: NativeText,
): SouhoolaInstallmentPlanState = copy(
    internalState = InternalState.Selected,
    selectedInstallmentPlan = selectedInstallmentPlan,
    downPaymentVisible = true,
    downPaymentHelperText = downPaymentHelperText,
    nextButtonEnabled = true,
)

internal fun SouhoolaInstallmentPlanState.downPaymentOutOfRange(
    downPaymentError: NativeText,
): SouhoolaInstallmentPlanState = copy(
    internalState = InternalState.OutOfRange,
    downPaymentError = downPaymentError,
    downPaymentHelperText = null,
    nextButtonEnabled = false,
)

internal fun SouhoolaInstallmentPlanState.financedAmountOutOfRange(
    financedAmountError: NativeText,
): SouhoolaInstallmentPlanState = copy(
    internalState = InternalState.OutOfRange,
    financedAmount = financedAmount,
    financedAmountError = financedAmountError,
    nextButtonEnabled = false,
)

internal fun SouhoolaInstallmentPlanState.clearDownPaymentError(): SouhoolaInstallmentPlanState =
    copy(
        downPaymentError = null,
        financedAmountError = null
    )

internal fun SouhoolaInstallmentPlanState.error(
    error: NativeText
): SouhoolaInstallmentPlanState = copy(
    internalState = InternalState.Error,
    error = error
)