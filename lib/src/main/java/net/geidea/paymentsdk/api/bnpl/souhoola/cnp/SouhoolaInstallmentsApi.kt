package net.geidea.paymentsdk.api.bnpl.souhoola.cnp

import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.bnpl.souhoola.SouhoolaService
import net.geidea.paymentsdk.model.bnpl.souhoola.*

public object SouhoolaInstallmentsApi {

    private val souhoolaService: SouhoolaService get() = SdkComponent.souhoolaService

    /**
     * Perform a verification of a Souhoola customer. This method is the first step in Souhoola installment
     * payment flow.
     *
     * @param request a request containing a customer identifier (phone number).
     */
    suspend fun verifyCustomer(request: VerifyCustomerRequest): GeideaResult<VerifyCustomerResponse> {
        return responseAsResult(::asIs) {
            souhoolaService.postVerifyCustomer(request)
        }
    }

    /**
     * Perform a call to Souhoola to calculate and retrieve a list of installment plans.
     * The plans are varying in tenure from few months to few years.
     * [request].
     *
     * @param request a request containing the amounts
     */
    suspend fun getInstallmentPlans(request: InstallmentPlansRequest): GeideaResult<InstallmentPlansResponse> {
        return responseAsResult(::asIs) {
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
    suspend fun selectInstallmentPlan(request: SelectInstallmentPlanRequest): GeideaResult<SelectInstallmentPlanResponse> {
        return responseAsResult(::asIs) {
            souhoolaService.postSelectInstallmentPlan(request)
        }
    }

    /**
     * Review the contents of Souhoola Installments transaction. The data returned should be
     * displayed to the user to confirm.
     *
     * @param request
     */
    suspend fun reviewTransaction(request: ReviewTransactionRequest): GeideaResult<ReviewTransactionResponse> {
        return responseAsResult(::asIs) {
            souhoolaService.postReviewTransaction(request)
        }
    }

    /**
     * Generate and send One-Time Password (OTP) for confirmation.
     *
     * @param request a request identifying the order, transaction and the customer
     */
    suspend fun generateOtp(request: GenerateOtpRequest): GeideaResult<GenerateOtpResponse> {
        return responseAsResult(::asIs) {
            souhoolaService.postGenerateOtp(request)
        }
    }

    /**
     * Confirm purchase with Souhoola Installments.
     *
     * @param request a request containing all necessary data to finalize the Souhoola Installments
     * purchase.
     */
    suspend fun confirm(request: ConfirmRequest): GeideaResult<ConfirmResponse> {
        return responseAsResult(::asIs) {
            souhoolaService.postConfirm(request)
        }
    }
}