package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.bnpl.valu.ValuService
import net.geidea.paymentsdk.model.bnpl.valu.*

internal class ValuServiceImpl(private val client: HttpsClient) : ValuService {

    override suspend fun postVerifyCustomer(verifyCustomerRequest: VerifyCustomerRequest): VerifyCustomerResponse {
        return client.post<VerifyCustomerRequest, VerifyCustomerResponse>(
                path = "/bnpl/api/direct/valu/cnp/v1/verifyCustomer",
                body = verifyCustomerRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postInstallmentPlans(installmentPlansRequest: InstallmentPlansRequest): InstallmentPlansResponse {
        return client.post<InstallmentPlansRequest, InstallmentPlansResponse>(
                path = "/bnpl/api/direct/valu/cnp/v1/installmentPlans",
                body = installmentPlansRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postSelectInstallmentPlan(selectInstallmentPlanRequest: SelectInstallmentPlanRequest): SelectInstallmentPlanResponse {
        return client.post<SelectInstallmentPlanRequest, SelectInstallmentPlanResponse>(
                path = "/bnpl/api/direct/valu/cnp/v1/selectInstallmentPlan",
                body = selectInstallmentPlanRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postGenerateOtp(generateOtpRequest: GenerateOtpRequest): GenerateOtpResponse {
        return client.post<GenerateOtpRequest, GenerateOtpResponse>(
                path = "/bnpl/api/direct/valu/cnp/v1/generateOtp",
                body = generateOtpRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postConfirm(confirmRequest: ConfirmRequest): ConfirmResponse {
        return client.post<ConfirmRequest, ConfirmResponse>(
                path = "/bnpl/api/direct/valu/cnp/v1/confirm",
                body = confirmRequest,
        ).unwrapOrThrow()
    }
}