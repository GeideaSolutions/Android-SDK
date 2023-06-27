package net.geidea.paymentsdk.model.paymentintent

object Channel {
    const val SMS = "Sms"
    const val EMAIL = "Email"
    val ALL: List<String> = listOf(SMS, EMAIL)
}