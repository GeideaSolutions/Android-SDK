package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.phone

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentValuPhoneNumberBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.viewBinding
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter


internal class ValuPhoneNumberFragment : BaseFragment<ValuPhoneNumberViewModel>(R.layout.gd_fragment_valu_phone_number) {

    override val viewModel: ValuPhoneNumberViewModel by viewModels { ViewModelFactory(paymentViewModel) }

    private val binding by viewBinding(GdFragmentValuPhoneNumberBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        addBackListener(viewModel::navigateBack)

        with(appBarWithStepper.stepper) {
            setOnBackClickListener { viewModel.navigateBack() }
            currentStep = 1
            stepCount = 3
            text = getString(R.string.gd_valu_confirm_phone_number)
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            nextButton.isEnabled = state.nextButtonEnabled
            val isLoading = state is ValuPhoneNumberState.Loading
            nextButton.text = if (isLoading) "" else getString(R.string.gd_btn_next)
            nextButtonProgress.isVisible = isLoading
            phoneNumberEditText.isEnabled = !isLoading
            phoneNumberInputLayout.error = if (state is ValuPhoneNumberState.Error)
                state.message?.toCharSequence()
            else null
        }

        phoneNumberEditText.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                onNextButtonClicked(nextButton)
                true
            } else
                false
        }
        phoneNumberEditText.setValidator(viewModel.phoneValidator)
        phoneNumberEditText.setOnValidStatusListener { phoneNumber ->
            viewModel.onPhoneNumberValidationChanged(phoneNumber, ValidationStatus.Valid)
        }
        phoneNumberEditText.setOnInvalidStatusListener { phoneNumber, invalidStatus ->
            viewModel.onPhoneNumberValidationChanged(phoneNumber, invalidStatus)
        }
        phoneNumberEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                phoneNumberInputLayout.error = null
            }
        })

        nextButton.setOnClickListener(::onNextButtonClicked)
        cancelButton.setOnClickListener(::onCancelButtonClicked)
    }

    private fun onNextButtonClicked(nextButton: View) {
        binding.phoneNumberEditText.text
                ?.toString()
                ?.takeIf(String::isNotEmpty)
                ?.let { phoneNumber -> "0$phoneNumber" }
                ?.let(viewModel::verifyPhoneNumber)
    }

    private fun onCancelButtonClicked(view: View) {
        confirmCancellationDialog(onPositive = { _, _ -> viewModel.navigateCancel() })
    }

    private class ViewModelFactory(
            private val paymentViewModel: PaymentViewModel,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ValuPhoneNumberViewModel(
                    paymentViewModel,
                    SdkComponent.valuService,
                    SdkComponent.connectivity,
            ) as T
        }
    }
}