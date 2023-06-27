package net.geidea.paymentsdk.model.order

object OrderStatus {
    const val INITIATED = "Initiated"
    const val AUTHENTICATED = "Authenticated"
    const val AUTHENTICATION_FAILED = "AuthenticationFailed"
    const val AUTHORIZED = "Authorized"
    const val AUTHORIZATION_FAILED = "AuthorizationFailed"
    const val CAPTURED = "Captured"
    const val CAPTURE_FAILED = "CaptureFailed"
    const val PAID = "Paid"
    const val PAY_FAILED = "PayFailed"
    const val REFUNDED = "Refunded"
    const val CANCELLED = "Cancelled"
    const val CLIENT_TIMED_OUT = "ClientTimedOut"
    const val SERVER_TIMED_OUT = "ServerTimedOut"
    const val BLOCKED = "Blocked"
    const val VOIDED = "Voided"
    const val PARTIALLY_COMPLETED = "PartiallyCompleted"
    const val PARTIALLY_REFUNDED = "PartiallyRefunded"
    const val REFUND_INCOMPLETE = "RefundIncomplete"
    const val REVERSED = "Reversed"

    val ALL: Set<String> = setOf(
        INITIATED,
        AUTHENTICATED,
        AUTHENTICATION_FAILED,
        AUTHORIZATION_FAILED,
        CAPTURE_FAILED,
        PAY_FAILED,
        AUTHORIZED,
        CAPTURED,
        PAID,
        REFUNDED,
        CANCELLED,
        SERVER_TIMED_OUT,
        CLIENT_TIMED_OUT,
        BLOCKED,
        VOIDED,
        PARTIALLY_COMPLETED,
        PARTIALLY_REFUNDED,
        REFUND_INCOMPLETE,
        REVERSED,
    )
}