package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.paymentintent.CreatePaymentIntentRequest
import net.geidea.paymentsdk.model.paymentintent.EInvoiceOrdersResponse
import net.geidea.paymentsdk.model.paymentintent.UpdateEInvoiceRequest

internal interface EInvoiceService {
    suspend fun postEInvoice(request: CreatePaymentIntentRequest): EInvoiceOrdersResponse
    suspend fun getEInvoice(paymentIntentId: String): EInvoiceOrdersResponse
    suspend fun putEInvoice(request: UpdateEInvoiceRequest): EInvoiceOrdersResponse
    suspend fun deleteEInvoice(paymentIntentId: String): EInvoiceOrdersResponse
    suspend fun sendEInvoiceBySms(eInvoiceId: String): EInvoiceOrdersResponse
    suspend fun sendEInvoiceByEmail(eInvoiceId: String): EInvoiceOrdersResponse
}