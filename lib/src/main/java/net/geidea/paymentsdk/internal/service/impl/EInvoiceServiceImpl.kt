package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.EInvoiceService
import net.geidea.paymentsdk.model.paymentintent.CreatePaymentIntentRequest
import net.geidea.paymentsdk.model.paymentintent.EInvoiceOrdersResponse
import net.geidea.paymentsdk.model.paymentintent.UpdateEInvoiceRequest

internal class EInvoiceServiceImpl(private val client: HttpsClient) : EInvoiceService {

    override suspend fun postEInvoice(request: CreatePaymentIntentRequest): EInvoiceOrdersResponse {
        return client.post<CreatePaymentIntentRequest, EInvoiceOrdersResponse>(
                path = "/payment-intent/api/v1/direct/eInvoice",
                body = request,
        ).unwrapOrThrow()
    }

    override suspend fun getEInvoice(paymentIntentId: String): EInvoiceOrdersResponse {
        return client.get<EInvoiceOrdersResponse>(
                path = "/payment-intent/api/v1/direct/eInvoice/$paymentIntentId",
        ).unwrapOrThrow()
    }

    override suspend fun putEInvoice(request: UpdateEInvoiceRequest): EInvoiceOrdersResponse {
        return client.put<UpdateEInvoiceRequest, EInvoiceOrdersResponse>(
                path = "/payment-intent/api/v1/direct/eInvoice",
                body = request,
        ).unwrapOrThrow()
    }

    override suspend fun deleteEInvoice(paymentIntentId: String): EInvoiceOrdersResponse {
        return client.delete<Unit, EInvoiceOrdersResponse>(
                path = "/payment-intent/api/v1/direct/eInvoice/$paymentIntentId",
        ).unwrapOrThrow()
    }

    override suspend fun sendEInvoiceBySms(eInvoiceId: String): EInvoiceOrdersResponse {
        return client.post<Unit, EInvoiceOrdersResponse>(
                path = "/payment-intent/api/v1/direct/eInvoice/$eInvoiceId/sendBySms",
        ).unwrapOrThrow()
    }

    override suspend fun sendEInvoiceByEmail(eInvoiceId: String): EInvoiceOrdersResponse {
        return client.post<Unit, EInvoiceOrdersResponse>(
                path = "/payment-intent/api/v1/direct/eInvoice/$eInvoiceId/sendByEmail",
        ).unwrapOrThrow()
    }
}