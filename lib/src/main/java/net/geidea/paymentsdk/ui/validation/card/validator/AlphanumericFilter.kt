package net.geidea.paymentsdk.ui.validation.card.validator

import net.geidea.paymentsdk.ui.validation.SimpleCharFilter

object AlphanumericFilter : SimpleCharFilter({ c -> c in '0'..'9' || c in 'a'..'z' || c in 'A'..'Z' })