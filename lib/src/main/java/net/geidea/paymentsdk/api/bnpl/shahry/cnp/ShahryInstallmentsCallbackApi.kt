package net.geidea.paymentsdk.api.bnpl.shahry.cnp

import kotlinx.coroutines.CoroutineScope
import net.geidea.paymentsdk.flow.GeideaResultCallback
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.bnpl.shahry.ShahryService
import net.geidea.paymentsdk.internal.util.launchWithCallback
import net.geidea.paymentsdk.model.bnpl.shahry.ConfirmRequest
import net.geidea.paymentsdk.model.bnpl.shahry.ConfirmResponse
import net.geidea.paymentsdk.model.bnpl.shahry.SelectInstallmentPlanRequest
import net.geidea.paymentsdk.model.bnpl.shahry.SelectInstallmentPlanResponse


public object ShahryCallbackApi {

    private val scope: CoroutineScope get() = SdkComponent.supervisorScope

    private val shahryService: ShahryService get() = SdkComponent.shahryService

    /**
     * Select an installment plan. This call creates an order in pending state. If the user decides
     * to change the plan a new call to this method will create a new order while the old one will
     * be automatically canceled by the server after if expires.
     *
     * @param request a request containing the amounts
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun selectInstallmentPlan(request: SelectInstallmentPlanRequest, resultCallback: GeideaResultCallback<SelectInstallmentPlanResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            shahryService.postSelectInstallmentPlan(request)
        }
    }

    /**
     * Confirm purchase with Shahry Installments.
     *
     * @param request a request containing all necessary data to confirm the Shahry Installments
     * purchase.
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun confirm(request: ConfirmRequest, resultCallback: GeideaResultCallback<ConfirmResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            shahryService.postConfirm(request)
        }
    }
}