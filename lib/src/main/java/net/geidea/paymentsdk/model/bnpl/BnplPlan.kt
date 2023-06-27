package net.geidea.paymentsdk.model.bnpl

import java.math.BigDecimal

interface BnplPlan {
    val tenorMonth: Int
    val installmentAmount: BigDecimal
}