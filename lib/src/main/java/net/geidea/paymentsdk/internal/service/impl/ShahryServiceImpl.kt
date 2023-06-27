package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.bnpl.shahry.ShahryService
import net.geidea.paymentsdk.model.bnpl.shahry.*

internal class ShahryServiceImpl(private val client: HttpsClient) : ShahryService {

    override suspend fun postSelectInstallmentPlan(selectInstallmentPlanRequest: SelectInstallmentPlanRequest): SelectInstallmentPlanResponse {
        return client.post<SelectInstallmentPlanRequest, SelectInstallmentPlanResponse>(
                path = "/bnpl/api/direct/shahry/cnp/v1/selectInstallmentPlan",
                body = selectInstallmentPlanRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postConfirm(confirmRequest: ConfirmRequest): ConfirmResponse {
        return client.post<ConfirmRequest, ConfirmResponse>(
                path = "/bnpl/api/direct/shahry/cnp/v1/confirm",
                body = confirmRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postCashOnDelivery(request: CashOnDeliveryRequest): CashOnDeliveryResponse {
        return client.post<CashOnDeliveryRequest, CashOnDeliveryResponse>(
            path = "/bnpl/api/direct/shahry/cnp/v1/cashOnDelivery",
            body = request,
        ).unwrapOrThrow()
    }
}