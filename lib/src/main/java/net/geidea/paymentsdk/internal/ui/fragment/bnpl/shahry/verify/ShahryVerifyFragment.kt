package net.geidea.paymentsdk.internal.ui.fragment.bnpl.shahry.verify

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentShahryVerifyBinding
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.util.browserIntent
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.formatAmount
import net.geidea.paymentsdk.internal.util.viewBinding
import net.geidea.paymentsdk.ui.validation.card.validator.AlphanumericFilter
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter

@GeideaSdkInternal
internal class ShahryVerifyFragment : BaseFragment<ShahryVerifyViewModel>(R.layout.gd_fragment_shahry_verify) {

    private val binding: GdFragmentShahryVerifyBinding by viewBinding(GdFragmentShahryVerifyBinding::bind)

    override val viewModel: ShahryVerifyViewModel by viewModels { ViewModelFactory(paymentViewModel) }

    private val paymentData: PaymentData get() = paymentViewModel.initialPaymentData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        addBackListener(viewModel::navigateBack)
        with(appBarWithStepper.stepper) {
            setOnBackClickListener { viewModel.navigateBack() }
            currentStep = 1
            stepCount = 2
            text = getString(R.string.gd_shahry_confirm_id)
        }

        merchantNameTextView.text = paymentViewModel.merchantConfiguration.merchantName
        totalAmountTextView.text = formatAmount(paymentData.amount, paymentData.currency)

        shahryIdEditText.filters = arrayOf(
                InputFilter.LengthFilter(255),
                AlphanumericFilter
        )
        shahryIdEditText.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                onNextButtonClicked(nextButton)
                true
            } else
                false
        }
        shahryIdEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                shahryIdInputLayout.error = null
                nextButton.isEnabled = s.toString().isNotEmpty()
            }
        })

        link1Button.setOnClickListener { startActivity(browserIntent(URL1)) }
        link2Button.setOnClickListener { startActivity(browserIntent(URL2)) }

        nextButton.setOnClickListener(::onNextButtonClicked)
        cancelButton.setOnClickListener(::onCancelButtonClicked)

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            binding.nextButton.isEnabled = state.nextButtonEnabled
            val isLoading = state is ShahryVerifyState.Loading
            binding.nextButton.text = if (isLoading) "" else getString(R.string.gd_btn_next)
            binding.nextButtonProgress.isVisible = isLoading
            shahryIdEditText.isEnabled = !isLoading
        }
    }

    private fun onNextButtonClicked(nextButton: View) {
        binding.shahryIdEditText.text
                ?.toString()
                ?.takeIf(String::isNotEmpty)
                ?.let(viewModel::onNextButtonClicked)
    }

    private fun onCancelButtonClicked(view: View) {
        confirmCancellationDialog(onPositive = { _, _ -> viewModel.navigateCancel() })
    }

    private class ViewModelFactory(
            private val paymentViewModel: PaymentViewModel,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ShahryVerifyViewModel(
                    paymentViewModel = paymentViewModel,
                    shahryService = SdkComponent.shahryService,
                    connectivity = SdkComponent.connectivity,
            ) as T
        }
    }

    companion object {
        const val URL1 = "https://shahry.app/my-shahry-id"
        const val URL2 = "https://shahry.app/"
    }
}