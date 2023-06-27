package net.geidea.paymentsdk.ui.widget.bnpl

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdItemBnplInstallmentPlanBinding
import net.geidea.paymentsdk.internal.util.formatAmount
import net.geidea.paymentsdk.internal.util.toBigDecimal
import net.geidea.paymentsdk.model.bnpl.BnplPlan
import java.math.BigDecimal

/**
 * Vertical linear layout representing a form for inputting a set of payment amounts needed to
 * define a BNPL installment plan. This view also presents a grid of selectable
 * installment plans.
 */
@Suppress("LeakingThis")
@GeideaSdkInternal
internal abstract class InstallmentPlanView<PLAN : BnplPlan> @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    protected val dash = context.getString(R.string.gd_dash)

    // State

    var currency: String = ""
        private set

    var totalAmount: BigDecimal = BigDecimal.ZERO
        private set(newValue) {
            if (field != newValue) {
                require(newValue.signum() == 1) { "totalAmount must be positive" }
                field = newValue
                totalAmountTextView.text = formatAmount(newValue, currency)
                updateUpfrontView()
            }
        }

    var financedAmount: BigDecimal = BigDecimal.ZERO
        set(newValue) {
            if (field != newValue) {
                field = newValue
                financedAmountTextView.text = formatAmount(newValue, currency)
                updateUpfrontView()
            }
        }

    var downPaymentAmount: BigDecimal
        get() = downPaymentAmountEditText.toBigDecimal()
        set(newValue) {
            downPaymentAmountEditText.setText("")
            downPaymentAmountEditText.append(formatAmount(newValue))
            updateUpfrontView()
        }

    var isDownPaymentInputEnabled: Boolean
        get() = downPaymentAmountEditText.isEnabled
        set(newValue) {
            downPaymentAmountEditText.isEnabled = newValue
        }

    var installmentPlans: List<PLAN> = emptyList()

        @Suppress("UNCHECKED_CAST")
        set(newValue) {
            if (field != newValue) {
                field = newValue
                val previousSelectedPlan: PLAN? = selectedInstallmentPlan
                installmentPlanGridLayout.removeAllViews()

                // Populate the grid layout with inflated item views
                newValue.map(::createItemView)

                if (previousSelectedPlan != null) {
                    // Find the newly created item having the tenure of previously selected
                    val itemView = installmentPlanGridLayout.children
                        .find { (it.tag as PLAN).tenorMonth == previousSelectedPlan.tenorMonth }
                    if (itemView is Checkable) {
                        itemView.isChecked = true
                        selectedInstallmentPlan = itemView?.tag as? PLAN?
                    }
                }
            }
        }

    abstract var isProgressVisible: Boolean

    private fun createItemView(installmentPlan: PLAN): View {
        val inflater = LayoutInflater.from(context)
        val itemBinding = GdItemBnplInstallmentPlanBinding.inflate(inflater, installmentPlanGridLayout, true)
        with(itemBinding) {
            root.tag = installmentPlan
            monthsTextView.text =
                context.getString(R.string.gd_bnpl_n_months, installmentPlan.tenorMonth)
            val amountPerMonth = installmentPlan.installmentAmount.toBigInteger().toInt()
            val amountText = context.getString(R.string.gd_bnpl_amount_per_month, amountPerMonth)
            monthlyAmountTextView.text = amountText

            root.setOnClickListener { clickedView: View ->
                onItemClicked(clickedView, installmentPlan)
            }
        }
        return itemBinding.root
    }

    private fun onItemClicked(installmentPlanView: View, installmentPlan: PLAN) {
        installmentPlanGridLayout.forEach { childView ->
            // Uncheck the previously checked
            if (childView != installmentPlanView && childView is Checkable && childView.isChecked) {
                childView.isChecked = false
            }
        }
        // Make sure it is not toggled but always checked on click
        if (installmentPlanView is Checkable) {
            installmentPlanView.isChecked = true
            selectedInstallmentPlan = installmentPlan
        }
    }

    var selectedInstallmentPlan: PLAN? = null
        set(newValue) {
            if (field != newValue) {
                field = newValue

                updateUpfrontView()

                selectionListener?.onInstallmentPlanSelected(newValue)
            }
        }

    private var selectionListener: OnInstallmentPlanSelectedListener<PLAN>? = null

    var error: CharSequence? = null
        set(newValue) {
            if (field != newValue) {
                field = newValue
                errorTextView.text = newValue
                errorTextView.isVisible = newValue != null

                installmentPlanGridLayout.isVisible = newValue == null && error == null
            }
        }

    var isDownPaymentVisible: Boolean
        get() = downPaymentAmountInputLayout.isVisible
        set(newValue) {
            downPaymentAmountInputLayout.isVisible = newValue
        }

    var downPaymentHelperText: CharSequence?
        get() = downPaymentAmountInputLayout.helperText
        set(newValue) {
            downPaymentAmountInputLayout.helperText = newValue
        }

    var downPaymentAmountError: CharSequence?
        get() = downPaymentAmountInputLayout.error
        set(newValue) {
            downPaymentAmountInputLayout.error = newValue
            installmentPlanGridLayout.isVisible = newValue == null && error == null
        }

    var isUpfrontAmountsVisible: Boolean
        get() = payUpfrontRootView.isVisible
        set(newValue) {
            payUpfrontRootView.isVisible = newValue
        }

    fun setTotalAmount(totalAmount: BigDecimal, currency: String) {
        this.currency = currency
        this.totalAmount = totalAmount
    }

    fun addDownPaymentAmountTextWatcher(textWatcher: TextWatcher) {
        downPaymentAmountEditText.addTextChangedListener(textWatcher)
    }

    fun removeDownPaymentAmountTextWatcher(textWatcher: TextWatcher) {
        downPaymentAmountEditText.removeTextChangedListener(textWatcher)
    }

    fun setDownPaymentOnEditorActionListener(listener: TextView.OnEditorActionListener?) {
        downPaymentAmountEditText.setOnEditorActionListener(listener)
    }

    fun setOnInstallmentPlanSelectedListener(listener: OnInstallmentPlanSelectedListener<PLAN>?) {
        this.selectionListener = listener
    }

    protected abstract val totalAmountTextView: TextView
    protected abstract val financedAmountTextView: TextView
    protected abstract val downPaymentAmountInputLayout: TextInputLayout
    protected abstract val downPaymentAmountEditText: TextInputEditText
    protected abstract val installmentPlanGridLayout: ViewGroup
    protected abstract val errorTextView: TextView

    // Pay upfront section
    protected abstract val payUpfrontRootView: View
    protected abstract val adminFeesTextView: TextView
    protected abstract val downPaymentTextView: TextView
    protected abstract val totalUpfrontTextView: TextView

    protected abstract fun updateUpfrontView()
}