package net.geidea.paymentsdk.sampleapp

/**
 * Drop-down menu item used.
 */
data class DropDownItem<T>(
        val value: T?,
        val text: String
) {
    constructor(value: T) : this(value, value.toString())

    override fun toString(): String = text
}