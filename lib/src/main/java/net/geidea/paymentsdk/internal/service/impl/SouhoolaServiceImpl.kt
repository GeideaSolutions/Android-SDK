package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.bnpl.souhoola.SouhoolaService
import net.geidea.paymentsdk.model.bnpl.souhoola.*

internal class SouhoolaServiceImpl(private val client: HttpsClient) : SouhoolaService {

    override suspend fun postVerifyCustomer(verifyCustomerRequest: VerifyCustomerRequest): VerifyCustomerResponse {
        return client.post<VerifyCustomerRequest, VerifyCustomerResponse>(
                path = "/bnpl/api/direct/souhoola/cnp/v1/verifyCustomer",
                body = verifyCustomerRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postInstallmentPlans(installmentPlansRequest: InstallmentPlansRequest): InstallmentPlansResponse {
        return client.post<InstallmentPlansRequest, InstallmentPlansResponse>(
                path = "/bnpl/api/direct/souhoola/cnp/v1/retrieveInstallmentPlans",
                body = installmentPlansRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postSelectInstallmentPlan(selectInstallmentPlanRequest: SelectInstallmentPlanRequest): SelectInstallmentPlanResponse {
        return client.post<SelectInstallmentPlanRequest, SelectInstallmentPlanResponse>(
                path = "/bnpl/api/direct/souhoola/cnp/v1/selectInstallmentPlan",
                body = selectInstallmentPlanRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postReviewTransaction(reviewTransactionRequest: ReviewTransactionRequest): ReviewTransactionResponse {
        return client.post<ReviewTransactionRequest, ReviewTransactionResponse>(
            path = "/bnpl/api/direct/souhoola/cnp/v1/reviewTransaction",
            body = reviewTransactionRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postGenerateOtp(generateOtpRequest: GenerateOtpRequest): GenerateOtpResponse {
        return client.post<GenerateOtpRequest, GenerateOtpResponse>(
                path = "/bnpl/api/direct/souhoola/cnp/v1/generateOtp",
                body = generateOtpRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postConfirm(confirmRequest: ConfirmRequest): ConfirmResponse {
        return client.post<ConfirmRequest, ConfirmResponse>(
                path = "/bnpl/api/direct/souhoola/cnp/v1/confirm",
                body = confirmRequest,
        ).unwrapOrThrow()
    }

    override suspend fun deleteCancel(cancelTransactionRequest: CancelTransactionRequest): CancelTransactionResponse {
        return client.delete<CancelTransactionRequest, CancelTransactionResponse>(
            path = "/bnpl/api/direct/souhoola/cnp/v1/cancel",
            body = cancelTransactionRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postResendOtp(resendOtpRequest: ResendOtpRequest): ResendOtpResponse {
        return client.post<ResendOtpRequest, ResendOtpResponse>(
            path = "/bnpl/api/direct/souhoola/cnp/v1/resendOtp",
            body = resendOtpRequest,
        ).unwrapOrThrow()
    }
}