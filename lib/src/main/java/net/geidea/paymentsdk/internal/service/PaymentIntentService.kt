package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.paymentintent.PaymentIntentResponse

internal interface PaymentIntentService {
    suspend fun getPaymentIntent(paymentIntentId: String): PaymentIntentResponse
}