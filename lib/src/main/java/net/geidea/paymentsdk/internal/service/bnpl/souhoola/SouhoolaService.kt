package net.geidea.paymentsdk.internal.service.bnpl.souhoola

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.model.bnpl.souhoola.*

@GeideaSdkInternal
internal interface SouhoolaService {
    suspend fun postVerifyCustomer(verifyCustomerRequest: VerifyCustomerRequest): VerifyCustomerResponse
    suspend fun postInstallmentPlans(installmentPlansRequest: InstallmentPlansRequest): InstallmentPlansResponse
    suspend fun postSelectInstallmentPlan(selectInstallmentPlanRequest: SelectInstallmentPlanRequest): SelectInstallmentPlanResponse
    suspend fun postReviewTransaction(reviewTransactionRequest: ReviewTransactionRequest): ReviewTransactionResponse
    suspend fun postGenerateOtp(generateOtpRequest: GenerateOtpRequest): GenerateOtpResponse
    suspend fun deleteCancel(cancelTransactionRequest: CancelTransactionRequest): CancelTransactionResponse
    suspend fun postConfirm(confirmRequest: ConfirmRequest): ConfirmResponse
    suspend fun postResendOtp(resendOtpRequest: ResendOtpRequest): ResendOtpResponse
}