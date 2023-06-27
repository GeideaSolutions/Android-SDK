package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.meezaqr.CreateMeezaPaymentIntentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentRequest
import net.geidea.paymentsdk.model.meezaqr.MeezaPaymentResponse
import net.geidea.paymentsdk.model.meezaqr.MeezaQrImageResponse

internal interface MeezaService {
    suspend fun postCreateQrCodeImageBase64(request: CreateMeezaPaymentIntentRequest): MeezaQrImageResponse
    suspend fun postRequestToPay(request: MeezaPaymentRequest): MeezaPaymentResponse
}