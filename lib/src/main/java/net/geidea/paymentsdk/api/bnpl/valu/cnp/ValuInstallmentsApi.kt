package net.geidea.paymentsdk.api.bnpl.valu.cnp

import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.asIs
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.service.bnpl.valu.ValuService
import net.geidea.paymentsdk.model.bnpl.valu.*


public object ValuInstallmentsApi {

    private val valuService: ValuService get() = SdkComponent.valuService

    /**
     * Perform a verification of a ValU customer. This method is the first step in ValU installment
     * payment flow.
     *
     * @param request a request containing a customer identifier (phone number).
     */
    suspend fun verifyCustomer(request: VerifyCustomerRequest): GeideaResult<VerifyCustomerResponse> {
        return responseAsResult(::asIs) {
            valuService.postVerifyCustomer(request)
        }
    }

    /**
     * Perform a call to ValU to calculate and retrieve a list of installment plans.
     * The plans are varying in tenure from few months to few years.
     * [request].
     *
     * @param request a request containing the amounts
     */
    suspend fun getInstallmentPlans(request: InstallmentPlansRequest): GeideaResult<InstallmentPlansResponse> {
        return responseAsResult(::asIs) {
            valuService.postInstallmentPlans(request)
        }
    }

    /**
     * Select an installment plan. This call creates an order in pending state. If the user decides
     * to change the plan a new call to this method will create a new order while the old one will
     * be automatically canceled by the server after if expires.
     *
     * @param request a request containing the amounts
     */
    suspend fun selectInstallmentPlan(request: SelectInstallmentPlanRequest): GeideaResult<SelectInstallmentPlanResponse> {
        return responseAsResult(::asIs) {
            valuService.postSelectInstallmentPlan(request)
        }
    }

    /**
     * Generate and send a One-Time Password (OTP) for ValU customer verification.
     *
     * @param request a request containing the ValU customer identifier (phone number)
     */
    suspend fun generateOtp(request: GenerateOtpRequest): GeideaResult<GenerateOtpResponse> {
        return responseAsResult(::asIs) {
            valuService.postGenerateOtp(request)
        }
    }

    /**
     * Confirm purchase with ValU Installments.
     *
     * @param request a request containing all necessary data to finalize the ValU Installments
     * purchase.
     */
    suspend fun confirm(request: ConfirmRequest): GeideaResult<ConfirmResponse> {
        return responseAsResult(::asIs) {
            valuService.postConfirm(request)
        }
    }
}