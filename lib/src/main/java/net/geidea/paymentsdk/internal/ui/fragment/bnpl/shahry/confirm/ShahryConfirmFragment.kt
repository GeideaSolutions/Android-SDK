package net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.confirm

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentShahryConfirmBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.ui.validation.card.validator.AlphanumericFilter
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter


@GeideaSdkInternal
internal class ShahryConfirmFragment : BaseFragment<ShahryConfirmViewModel>(R.layout.gd_fragment_shahry_confirm) {

    private val args: ShahryConfirmFragmentArgs by navArgs()

    private val binding: GdFragmentShahryConfirmBinding by viewBinding(GdFragmentShahryConfirmBinding::bind)

    override val viewModel: ShahryConfirmViewModel by viewModels { ViewModelFactory(paymentViewModel, args) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        addBackListener { confirmCancellationDialog(onPositive = { _, _ -> viewModel.navigateCancel() }) }
        with (appBarWithStepper.stepper) {
            isBackButtonVisible = false
            currentStep = 2
            stepCount = 2
            text = getString(R.string.gd_shahry_confirm_purchase)
        }

        merchantNameTextView.text = paymentViewModel.merchantConfiguration.merchantName
        totalAmountTextView.text = formatAmount(
                amount = viewModel.paymentData.amount,
                currency = viewModel.paymentData.currency
        )

        shahryIdTextView.text = args.customerIdentifier

        hintTextView setText NativeText.Resource(R.string.gd_shahry_open_app)
        hintTextView.isClickable = true
        hintTextView.movementMethod = LinkMovementMethod.getInstance()

        termsTextView setText NativeText.Template(R.string.gd_shahry_terms_s, listOf(viewModel.merchantName))

        orderTokenEditText.filters = arrayOf(
                InputFilter.LengthFilter(255),
                AlphanumericFilter
        )
        orderTokenEditText.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                orderTokenEditText.textOrNull?.let(viewModel::onButtonClicked)
                true
            } else
                false
        }
        orderTokenEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                val isEmptyText = s.toString().isEmpty()
                confirmButton.isEnabled = !isEmptyText
                orderTokenInputLayout.error = null
            }
        })

        payUpfront.payUpfrontLinearLayout.isVisible = false

        downPaymentOptions.downPaymentOptionsRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            viewModel.onCashOnDeliverySelected(checkedId == R.id.payOnDeliveryRadioButton)
        }

        confirmButton.setOnClickListener {
            orderTokenEditText.textOrNull?.let(viewModel::onButtonClicked)
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            confirmButton.isEnabled = state.buttonEnabled && !orderTokenEditText.textOrNull.isNullOrEmpty()
            confirmButton setText state.buttonTitle
            confirmButtonProgress.isVisible = state.progressVisible
            orderTokenEditText.isEnabled = state.orderTokenInputEnabled
            orderTokenInputLayout setError state.errorMessage
            termsTextView.isVisible = state.termsTextVisible
            with(payUpfront) {
                root.isVisible = state.payUpfrontVisible
                adminFeesTextView setText state.purchaseFeesText
                downPaymentTextView setText state.downPaymentText
                totalUpfrontTextView setText state.totalUpfrontText
            }
            with (downPaymentOptions) {
                downPaymentOptionsRadioGroup.isVisible = state.downPaymentOptionsVisible
                payNowRadioButton.setText(state.downPaymentNowText)
                payOnDeliveryRadioButton.setText(state.downPaymentOnDeliveryText)
            }
            appBarWithStepper.stepper.stepCount = state.stepCount
        }
    }

    private class ViewModelFactory(
            private val paymentViewModel: PaymentViewModel,
            private val args: ShahryConfirmFragmentArgs,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ShahryConfirmViewModel(
            paymentViewModel = paymentViewModel,
            shahryService = SdkComponent.shahryService,
            connectivity = SdkComponent.connectivity,
            merchantName = SdkComponent.merchantsService.cachedMerchantConfiguration.merchantName
                ?: "[Merchant name]",
            customerIdentifier = args.customerIdentifier,
        ) as T
    }
}