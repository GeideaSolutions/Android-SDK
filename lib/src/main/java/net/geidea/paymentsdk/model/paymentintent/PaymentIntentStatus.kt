package net.geidea.paymentsdk.model.paymentintent

class PaymentIntentStatus {
    companion object {
        const val CREATED = "Created"
        const val PAID = "Paid"
        const val INCOMPLETE = "Incomplete"
        const val EXPIRED = "Expired"
        const val PREAUTHORIZED = "Preauthorized"
        const val SENT_TO_CUSTOMER = "SentToCustomer"
    }
}