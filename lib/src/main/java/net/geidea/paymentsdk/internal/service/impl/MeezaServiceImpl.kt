package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.MeezaService
import net.geidea.paymentsdk.model.meezaqr.CreateMeezaPaymentIntentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentResponse
import net.geidea.paymentsdk.model.meezaqr.MeezaQrImageResponse

internal class MeezaServiceImpl(private val client: HttpsClient) : MeezaService {

    override suspend fun postCreateQrCodeImageBase64(request: CreateMeezaPaymentIntentRequest): MeezaQrImageResponse {
        return client.post<CreateMeezaPaymentIntentRequest, MeezaQrImageResponse>(
                path = "/payment-intent/api/v1/meezaPayment/image/base64",
                body = request,
        ).unwrapOrThrow()
    }

    override suspend fun postRequestToPay(request: MeezaPaymentRequest): MeezaPaymentResponse {
        return client.post<MeezaPaymentRequest, MeezaPaymentResponse>(
                path = "/meeza/api/v1/transaction/requestToPay",
                body = request,
        ).unwrapOrThrow()
    }
}