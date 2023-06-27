package net.geidea.paymentsdk.internal.service.bnpl.shahry

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.model.bnpl.shahry.*

@GeideaSdkInternal
internal interface ShahryService {
    suspend fun postSelectInstallmentPlan(selectInstallmentPlanRequest: SelectInstallmentPlanRequest): SelectInstallmentPlanResponse
    suspend fun postConfirm(confirmRequest: ConfirmRequest): ConfirmResponse
    suspend fun postCashOnDelivery(request: CashOnDeliveryRequest): CashOnDeliveryResponse
}