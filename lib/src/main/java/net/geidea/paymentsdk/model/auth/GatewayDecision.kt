package net.geidea.paymentsdk.model.auth

object GatewayDecision {
    const val ContinueToPayer = "ContinueToPayer"
    const val ContinueToPay = "ContinueToPay"
    const val ContinueToPayWithNotEnrolledCard = "ContinueToPayWithNotEnrolledCard"
    const val Reject = "Reject"
}