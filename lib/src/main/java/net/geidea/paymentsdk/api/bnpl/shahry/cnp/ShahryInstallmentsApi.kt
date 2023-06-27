package net.geidea.paymentsdk.api.bnpl.shahry.cnp

import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.bnpl.shahry.ShahryService
import net.geidea.paymentsdk.model.bnpl.shahry.ConfirmRequest
import net.geidea.paymentsdk.model.bnpl.shahry.ConfirmResponse
import net.geidea.paymentsdk.model.bnpl.shahry.SelectInstallmentPlanRequest
import net.geidea.paymentsdk.model.bnpl.shahry.SelectInstallmentPlanResponse


public object ShahryInstallmentsApi {

    private val shahryService: ShahryService get() = SdkComponent.shahryService

    /**
     * Select an installment plan. This call creates an order in pending state. If the user decides
     * to change the plan a new call to this method will create a new order while the old one will
     * be automatically canceled by the server after if expires.
     *
     * @param request a request containing the amounts
     */
    suspend fun selectInstallmentPlan(request: SelectInstallmentPlanRequest): GeideaResult<SelectInstallmentPlanResponse> {
        return responseAsResult(::asIs) {
            shahryService.postSelectInstallmentPlan(request)
        }
    }

    /**
     * Confirm purchase with Shahry Installments.
     *
     * @param request a request containing all necessary data to confirm the Shahry Installments
     * purchase.
     */
    suspend fun confirm(request: ConfirmRequest): GeideaResult<ConfirmResponse> {
        return responseAsResult(::asIs) {
            shahryService.postConfirm(request)
        }
    }
}