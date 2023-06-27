package net.geidea.paymentsdk.ui.widget.bnpl.valu

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
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
import net.geidea.paymentsdk.databinding.GdViewValuInstallmentPlanBinding
import net.geidea.paymentsdk.internal.util.formatAmount
import net.geidea.paymentsdk.internal.util.toBigDecimal
import net.geidea.paymentsdk.model.bnpl.valu.InstallmentPlan
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter
import net.geidea.paymentsdk.ui.widget.bnpl.InstallmentPlanView
import java.math.BigDecimal

@GeideaSdkInternal
internal class ValuInstallmentPlanView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : InstallmentPlanView<InstallmentPlan>(context, attrs, defStyleAttr, defStyleRes) {

    private val binding: GdViewValuInstallmentPlanBinding

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

    override val payUpfrontRootView: View
        get() = binding.payUpfront.root
    override val adminFeesTextView: TextView
        get() = binding.payUpfront.adminFeesTextView
    override val downPaymentTextView: TextView
        get() = binding.payUpfront.downPaymentTextView
    override val totalUpfrontTextView: TextView
        get() = binding.payUpfront.totalUpfrontTextView

    var giftCardAmount: BigDecimal
        get() = binding.giftCardAmountEditText.toBigDecimal()
        set(newValue) {
            binding.giftCardAmountEditText.setText("")
            binding.giftCardAmountEditText.append(formatAmount(newValue))
        }

    var campaignAmount: BigDecimal
        get() = binding.campaignAmountEditText.toBigDecimal()
        set(newValue) {
            binding.campaignAmountEditText.setText("")
            binding.campaignAmountEditText.append(formatAmount(newValue))
        }

    var giftCardAmountError: CharSequence?
        get() = binding.giftCardAmountInputLayout.error
        set(newValue) {
            binding.giftCardAmountInputLayout.error = newValue
            binding.installmentPlanGridLayout.isVisible = newValue == null
        }

    var campaignAmountError: CharSequence?
        get() = binding.campaignAmountInputLayout.error
        set(newValue) {
            binding.campaignAmountInputLayout.error = newValue
            binding.installmentPlanGridLayout.isVisible = newValue == null
        }

    override var isProgressVisible: Boolean
        get() = false
        set(newValue) {
            // TODO
        }

    init {
        val materialContext = MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes)
        inflate(materialContext, R.layout.gd_view_valu_installment_plan, this)

        binding = GdViewValuInstallmentPlanBinding.bind(this)

        isUpfrontAmountsVisible = false

        binding.downPaymentAmountEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                binding.downPaymentAmountInputLayout.error = null
            }
        })

        binding.giftCardAmountEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                binding.giftCardAmountInputLayout.error = null
            }
        })

        binding.campaignAmountEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                binding.campaignAmountInputLayout.error = null
            }
        })
    }

    fun addGiftCardAmountTextWatcher(textWatcher: TextWatcher) {
        binding.giftCardAmountEditText.addTextChangedListener(textWatcher)
    }

    fun removeGiftCardAmountTextWatcher(textWatcher: TextWatcher) {
        binding.giftCardAmountEditText.removeTextChangedListener(textWatcher)
    }

    fun addCampaignAmountTextWatcher(textWatcher: TextWatcher) {
        binding.campaignAmountEditText.addTextChangedListener(textWatcher)
    }

    fun removeCampaignAmountTextWatcher(textWatcher: TextWatcher) {
        binding.campaignAmountEditText.removeTextChangedListener(textWatcher)
    }

    override fun updateUpfrontView() {
        val plan = selectedInstallmentPlan
        val showUpfrontAmounts = selectedInstallmentPlan != null
        isUpfrontAmountsVisible = showUpfrontAmounts
        adminFeesTextView.text = plan?.let { formatAmount(plan.adminFees, currency) } ?: dash
        downPaymentTextView.text = plan?.let { formatAmount(plan.downPayment, currency) } ?: dash
        totalUpfrontTextView.text = plan?.let { formatAmount(plan.downPayment + plan.adminFees, currency) } ?: dash
    }
}