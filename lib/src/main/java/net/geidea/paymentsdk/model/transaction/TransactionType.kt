package net.geidea.paymentsdk.model.transaction

class TransactionType {
    companion object {
        const val AUTHENTICATION = "Authentication"
        const val AUTHORIZATION = "Authorization"
        const val CAPTURE = "Capture"
        const val PAY = "Pay"
        const val REFUND = "Refund"
        const val REVERSAL = "Reversal"
        const val VOID = "Void"
        const val INSTALLMENT = "Installment"
    }
}