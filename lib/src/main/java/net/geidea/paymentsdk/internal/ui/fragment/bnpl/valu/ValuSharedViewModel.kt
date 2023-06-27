package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.model.bnpl.valu.InstallmentPlan
import net.geidea.paymentsdk.model.bnpl.valu.InstallmentPlansResponse

@GeideaSdkInternal
internal class ValuSharedViewModel : BaseViewModel() {

    var customerIdentifier: String? = null
    var installmentPlansResponse: InstallmentPlansResponse? = null
    var selectedInstallmentPlan: InstallmentPlan? = null

    fun startSession(
            customerIdentifier: String,
            installmentPlansResponse: InstallmentPlansResponse,
            selectedInstallmentPlan: InstallmentPlan,
    ) {
        this.customerIdentifier = customerIdentifier
        this.installmentPlansResponse = installmentPlansResponse
        this.selectedInstallmentPlan = selectedInstallmentPlan
    }
}