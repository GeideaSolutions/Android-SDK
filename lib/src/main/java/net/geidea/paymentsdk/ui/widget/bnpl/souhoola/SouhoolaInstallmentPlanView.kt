package net.geidea.paymentsdk.ui.widget.bnpl.souhoola

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdViewSouhoolaInstallmentPlanBinding
import net.geidea.paymentsdk.internal.util.formatAmount
import net.geidea.paymentsdk.internal.util.setupIndeterminateProgressOn
import net.geidea.paymentsdk.model.bnpl.souhoola.InstallmentPlan
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter
import net.geidea.paymentsdk.ui.widget.bnpl.InstallmentPlanView

@GeideaSdkInternal
internal class SouhoolaInstallmentPlanView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : InstallmentPlanView<InstallmentPlan>(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: GdViewSouhoolaInstallmentPlanBinding

    override val totalAmountTextView: TextView
        get() = binding.totalAmountTextView
    override val financedAmountTextView: TextView
        get() = binding.financedAmountTextView
    override val downPaymentAmountInputLayout: TextInputLayout
        get() = binding.downPaymentAmountInputLayout
    override val downPaymentAmountEditText: TextInputEditText
        get() = binding.downPaymentAmountEditText
    override val installmentPlanGridLayout: ViewGroup
        get() = binding.installmentPlanGridLayout
    override val errorTextView: TextView
        get() = binding.errorTextView

    val financedAmountErrorTextView: TextView
        get() = binding.financedAmountErrorTextView

    override var isProgressVisible: Boolean
        get() = binding.downPaymentAmountInputLayout.isEndIconVisible
        set(newValue) {
            binding.downPaymentAmountInputLayout.isEndIconVisible = newValue
        }

    override val payUpfrontRootView: View
        get() = binding.payUpfront.root
    override val adminFeesTextView: TextView
        get() = binding.payUpfront.adminFeesTextView
    override val downPaymentTextView: TextView
        get() = binding.payUpfront.downPaymentTextView
    override val totalUpfrontTextView: TextView
        get() = binding.payUpfront.totalUpfrontTextView

    init {
        val materialContext = MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes)
        inflate(materialContext, R.layout.gd_view_souhoola_installment_plan, this)

        binding = GdViewSouhoolaInstallmentPlanBinding.bind(this)

        isUpfrontAmountsVisible = false

        binding.downPaymentAmountEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                binding.downPaymentAmountInputLayout.error = null
            }
        })

        setupIndeterminateProgressOn(downPaymentAmountInputLayout)
    }

    override fun updateUpfrontView() {
        val plan = selectedInstallmentPlan
        binding.approxAmountHintTextView.isVisible = binding.installmentPlanGridLayout.isVisible
        binding.payUpfront.payUpfrontLinearLayout.isVisible = false
        adminFeesTextView.text = plan?.let { formatAmount(plan.adminFees, currency) } ?: dash
        downPaymentTextView.text = plan?.let { formatAmount(plan.downPayment, currency) } ?: dash
        totalUpfrontTextView.text = plan?.let { formatAmount(plan.downPayment + plan.adminFees, currency) } ?: dash
    }
}