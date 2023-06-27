package net.geidea.paymentsdk.api.bnpl.souhoola.cnp

import kotlinx.coroutines.CoroutineScope
import net.geidea.paymentsdk.flow.GeideaResultCallback
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.bnpl.souhoola.SouhoolaService
import net.geidea.paymentsdk.internal.util.launchWithCallback
import net.geidea.paymentsdk.model.bnpl.souhoola.*

public object SouhoolaInstallmentsCallbackApi {

    private val scope: CoroutineScope get() = SdkComponent.supervisorScope

    private val souhoolaService: SouhoolaService get() = SdkComponent.souhoolaService

    /**
     * Perform a verification of a Souhoola customer. This method is the first step in Souhoola installment
     * payment flow.
     *
     * @param request a request containing customer identifier (phone number).
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun verifyCustomer(request: VerifyCustomerRequest, resultCallback: GeideaResultCallback<VerifyCustomerResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            souhoolaService.postVerifyCustomer(request)
        }
    }

    /**
     * Perform a call to Souhoola to calculate and retrieve a list of installment plans.
     * The plans are varying in tenure from few months to few years.
     * [request].
     *
     * @param request a request containing the amounts
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun getInstallmentPlans(request: InstallmentPlansRequest, resultCallback: GeideaResultCallback<InstallmentPlansResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            souhoolaService.postInstallmentPlans(request)
        }
    }

    /**
     * Selects a given installment plan on the server. This effectively creates a new Souhoola
     * transaction associated to the Geidea order. Once this method is called, a different
     * installment plan cannot be selected anymore.
     *
     * @param request a request describing the selected installment plan
     */
    suspend fun selectInstallmentPlan(request: SelectInstallmentPlanRequest, resultCallback: GeideaResultCallback<SelectInstallmentPlanResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            souhoolaService.postSelectInstallmentPlan(request)
        }
    }

    /**
     * Review the contents of Souhoola Installments transaction. The data returned should be
     * displayed to the user to confirm.
     *
     * @param request a request containing the amounts
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun reviewTransaction(request: ReviewTransactionRequest, resultCallback: GeideaResultCallback<ReviewTransactionResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            souhoolaService.postReviewTransaction(request)
        }
    }

    /**
     * Generate and send One-Time Password (OTP) for confirmation.
     *
     * @param request a request identifying the order, transaction and the customer
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun generateOtp(request: GenerateOtpRequest, resultCallback: GeideaResultCallback<GenerateOtpResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            souhoolaService.postGenerateOtp(request)
        }
    }

    /**
     * Confirm purchase with Souhoola Installments.
     *
     * @param request a request containing all necessary data to finalize the Souhoola Installments
     * purchase.
     * @param resultCallback callback to receive the result on the Main thread.
     */
    @JvmStatic
    fun confirm(request: ConfirmRequest, resultCallback: GeideaResultCallback<ConfirmResponse>) {
        scope.launchWithCallback(resultCallback, outputTransform = ::asIs) {
            souhoolaService.postConfirm(request)
        }
    }
}