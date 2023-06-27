package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.verify

import android.os.Bundle
import android.text.Editable
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentSouhoolaVerifyCustomerBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.BnplEgyptPhoneValidator
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.SouhoolaSharedViewModel
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.souhoolaNavGraphViewModel
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.textOrNull
import net.geidea.paymentsdk.internal.util.viewBinding
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter

@GeideaSdkInternal
internal class SouhoolaVerifyCustomerFragment :
    BaseFragment<SouhoolaVerifyCustomerViewModel>(R.layout.gd_fragment_souhoola_verify_customer) {

    private val binding: GdFragmentSouhoolaVerifyCustomerBinding
            by viewBinding(GdFragmentSouhoolaVerifyCustomerBinding::bind)

    private val souhoolaSharedViewModel: SouhoolaSharedViewModel
            by souhoolaNavGraphViewModel(lazy { paymentViewModel })

    override val viewModel: SouhoolaVerifyCustomerViewModel by viewModels {
        ViewModelFactory(
            paymentViewModel = paymentViewModel,
            souhoolaSharedViewModel = souhoolaSharedViewModel,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        addBackListener(viewModel::navigateBack)

        with(appBarWithStepper.stepper) {
            setOnBackClickListener { viewModel.navigateBack() }
            currentStep = 1
            stepCount = 4
            text = getString(R.string.gd_souhoola_confirm_phone_number_and_pin)
        }

        // Phone number

        phoneNumberEditText.setOnErrorListener(TextInputErrorListener(phoneNumberInputLayout))
        phoneNumberEditText.setValidator(viewModel.phoneValidator)
        phoneNumberEditText.setOnValidStatusListener { phoneNumber ->
            viewModel.onPhoneNumberValidationChanged(phoneNumber, ValidationStatus.Valid)
        }
        phoneNumberEditText.setOnInvalidStatusListener { phoneNumber, invalidStatus ->
            viewModel.onPhoneNumberValidationChanged(phoneNumber, invalidStatus)
        }
        phoneNumberEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length == BnplEgyptPhoneValidator.MAX_LENGTH) {
                    phoneNumberEditText.updateErrorMessage()
                }
            }
        })

        // PIN

        // After the new business requirement changed from fixed 5 digit PIN to variable length max 10 digits,
        // practically there is no client-side PIN validation. Only that the PIN edittext
        // restriction of the length.
        pinEditText.setValidator(viewModel.pinValidator)
        pinEditText.setOnValidStatusListener { pin ->
            viewModel.onPinValidationChanged(pin, ValidationStatus.Valid)
        }
        pinEditText.setOnInvalidStatusListener { pin, invalidStatus ->
            viewModel.onPinValidationChanged(pin, invalidStatus)
        }
        pinEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun afterTextChanged(s: Editable) {
                pinEditText.validate()
            }
        })
        pinEditText.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                onNextButtonClicked(nextButton)
                true
            } else
                false
        }

        // Buttons

        nextButton.setOnClickListener(::onNextButtonClicked)
        cancelButton.setOnClickListener(::onCancelButtonClicked)

        // "Don't have an account..."

        notRegisteredHintTextView.text = HtmlCompat.fromHtml(
            getString(R.string.gd_souhoola_not_registered),
            FROM_HTML_MODE_COMPACT
        )
        notRegisteredHintTextView.movementMethod = LinkMovementMethod.getInstance()

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            binding.nextButton.isEnabled = state.nextButtonEnabled
            val isLoading = state is SouhoolaVerifyCustomerState.Loading
            binding.nextButton.text = if (isLoading) "" else getString(R.string.gd_btn_next)
            binding.nextButtonProgress.isVisible = isLoading
            phoneNumberEditText.isEnabled = !isLoading
            pinEditText.isEnabled = !isLoading
        }
    }

    private fun onNextButtonClicked(nextButton: View) {
        val phoneNumber = binding.phoneNumberEditText.textOrNull
        val pin = binding.pinEditText.textOrNull
        if (phoneNumber != null && pin != null) {
            viewModel.verifyCustomer("0$phoneNumber", pin)
        }
    }

    private fun onCancelButtonClicked(view: View) {
        confirmCancellationDialog(onPositive = { _, _ -> viewModel.navigateCancel() })
    }

    private class ViewModelFactory(
        private val souhoolaSharedViewModel: SouhoolaSharedViewModel,
        private val paymentViewModel: PaymentViewModel,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SouhoolaVerifyCustomerViewModel(
                paymentViewModel = paymentViewModel,
                souhoolaSharedViewModel = souhoolaSharedViewModel,
                souhoolaService = SdkComponent.souhoolaService,
                connectivity = SdkComponent.connectivity,
            ) as T
    }
}