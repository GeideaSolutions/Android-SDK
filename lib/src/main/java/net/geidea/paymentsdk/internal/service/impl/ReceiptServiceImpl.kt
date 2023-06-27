package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.receipt.ReceiptService
import net.geidea.paymentsdk.model.receipt.ReceiptResponse

internal class ReceiptServiceImpl(private val client: HttpsClient) : ReceiptService {

    override suspend fun getOrderReceipt(orderId: String): ReceiptResponse {
        return client.get<ReceiptResponse>(path = "/receipt/api/direct/v1/receipt/$orderId").unwrapOrThrow()
    }
}