package net.geidea.paymentsdk.api.bnpl.valu.cnp

import kotlinx.coroutines.CoroutineScope
import net.geidea.paymentsdk.flow.GeideaResultCallback
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.util.launchWithCallback
import net.geidea.paymentsdk.model.bnpl.valu.*


public object ValuInstallmentsCallbackApi {

    private val scope: CoroutineScope get() = SdkComponent.supervisorScope

    private val valuService get() = SdkComponent.valuService

    /**
     * Perform a verification of a ValU customer. This method is the first step in ValU installment
     * payment flow.
     *
     * @param request a request containing customer identifier (phone number).
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun verifyCustomer(request: VerifyCustomerRequest, resultCallback: GeideaResultCallback<VerifyCustomerResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            valuService.postVerifyCustomer(request)
        }
    }

    /**
     * Perform a call to ValU to calculate and retrieve a list of installment plans.
     * The plans are varying in tenure from few months to few years.
     * [request].
     *
     * @param request a request containing the amounts
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun getInstallmentPlans(request: InstallmentPlansRequest, resultCallback: GeideaResultCallback<InstallmentPlansResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            valuService.postInstallmentPlans(request)
        }
    }

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
            valuService.postSelectInstallmentPlan(request)
        }
    }

    /**
     * Generate and send a One-Time Password (OTP) for ValU customer verification.
     *
     * @param request a request containing the ValU customer identifier (phone number)
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun generateOtp(request: GenerateOtpRequest, resultCallback: GeideaResultCallback<GenerateOtpResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            valuService.postGenerateOtp(request)
        }
    }

    /**
     * Confirm purchase with ValU Installments.
     *
     * @param request a request containing all necessary data to finalize the ValU Installments
     * purchase.
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun confirm(request: ConfirmRequest, resultCallback: GeideaResultCallback<ConfirmResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            valuService.postConfirm(request)
        }
    }
}