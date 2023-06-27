package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.plan

import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.model.bnpl.valu.InstallmentPlan
import net.geidea.paymentsdk.model.bnpl.valu.InstallmentPlansResponse

internal sealed interface ValuInstallmentPlanState {
    object Initial : ValuInstallmentPlanState
    object Loading : ValuInstallmentPlanState
    data class Success(
            val response: InstallmentPlansResponse,
            val selectedInstallmentPlan: InstallmentPlan? = null,
            val nextButtonEnabled: Boolean = false,
            val downPaymentOptionsVisible: Boolean,
            val downPaymentNowText: NativeText = NativeText.Empty,
            val downPaymentOnDeliveryText: NativeText = NativeText.Empty,
            val stepCount: Int
    ) : ValuInstallmentPlanState

    sealed interface Error : ValuInstallmentPlanState

    data class ValidationErrors(
            // Key: json field name ("DownPayment", "GiftCardAmount", "CampaignAmount")
            // Value: validation error message(s)
            val errors: Map<String, List<String?>>
    ) : ValuInstallmentPlanState
    data class ProcessingError(val message: CharSequence?) : ValuInstallmentPlanState
}