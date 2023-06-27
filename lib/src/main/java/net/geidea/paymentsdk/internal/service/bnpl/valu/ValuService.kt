package net.geidea.paymentsdk.internal.service.bnpl.valu

import net.geidea.paymentsdk.model.bnpl.valu.*

internal interface ValuService {
    suspend fun postVerifyCustomer(verifyCustomerRequest: VerifyCustomerRequest): VerifyCustomerResponse
    suspend fun postInstallmentPlans(installmentPlansRequest: InstallmentPlansRequest): InstallmentPlansResponse
    suspend fun postSelectInstallmentPlan(selectInstallmentPlanRequest: SelectInstallmentPlanRequest): SelectInstallmentPlanResponse
    suspend fun postGenerateOtp(generateOtpRequest: GenerateOtpRequest): GenerateOtpResponse
    suspend fun postConfirm(confirmRequest: ConfirmRequest): ConfirmResponse
}