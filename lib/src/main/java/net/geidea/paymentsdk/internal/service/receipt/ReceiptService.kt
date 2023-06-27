package net.geidea.paymentsdk.internal.service.receipt

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.model.receipt.ReceiptResponse

@GeideaSdkInternal
internal interface ReceiptService {
    suspend fun getOrderReceipt(orderId: String): ReceiptResponse
}