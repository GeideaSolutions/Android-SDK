package net.geidea.paymentsdk.ui.widget.bnpl

import net.geidea.paymentsdk.model.bnpl.BnplPlan

fun interface OnInstallmentPlanSelectedListener<PLAN : BnplPlan> {
    fun onInstallmentPlanSelected(installmentPlan: PLAN?)
}
