package net.geidea.paymentsdk.sampleapp.sample.orders

enum class OrderOperation(val displayText: CharSequence) {
    CAPTURE("Capture order"),
    REFUND("Refund order"),
    CANCEL("Cancel order"),
}