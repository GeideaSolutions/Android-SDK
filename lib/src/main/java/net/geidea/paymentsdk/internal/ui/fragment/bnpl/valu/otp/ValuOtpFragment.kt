package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.otp

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentValuOtpBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.ValuSharedViewModel
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.viewBinding

internal class ValuOtpFragment : BaseFragment<ValuOtpViewModel>(R.layout.gd_fragment_valu_otp) {

    private val args: ValuOtpFragmentArgs by navArgs()

    override val viewModel: ValuOtpViewModel by viewModels { ViewModelFactory(paymentViewModel, valuSharedViewModel) }

    private val valuSharedViewModel: ValuSharedViewModel by navGraphViewModels(R.id.gd_valu_graph)

    private val binding by viewBinding(GdFragmentValuOtpBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            addBackListener(::onBackButtonClicked)

            with(appBarWithStepper.stepper) {
                isBackButtonVisible = false
                text = getString(R.string.gd_bnpl_fill_otp)
                step = args.step
            }

            otpInputView.setOnOtpChangedListener { otp, filled ->
                purchaseButton.isEnabled = filled
                errorTextView.isInvisible = true
            }

            otpInputView.setOnEditorActionListener { view, actionId, event: KeyEvent? ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onPurchaseButtonClicked(purchaseButton)
                    true
                } else {
                    false
                }
            }

            /*if (otpInputView.prepopulateFromClipboard()) {
                otpInputView.clearFocus()
                hideKeyboard(otpInputView)
            }*/

            resendCodeButton.setOnClickListener {
                viewModel.onResendButtonClicked()
            }

            purchaseButton.setOnClickListener(::onPurchaseButtonClicked)
            cancelButton.setOnClickListener(::onCancelButtonClicked)

            viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is ValuOtpState.Initial -> {
                        otpInputView.isEnabled = true
                        resendCodeButton.isEnabled = true
                        purchaseButton.isEnabled = false
                        errorTextView.isInvisible = true
                    }
                    is ValuOtpState.Loading -> {
                        otpInputView.isEnabled = false
                        resendCodeButton.isEnabled = false
                        purchaseButton.isEnabled = false
                        errorTextView.isInvisible = true
                    }
                    is ValuOtpState.OtpSent -> {
                        otpInputView.isEnabled = true
                        resendCodeButton.isEnabled = true
                        purchaseButton.isEnabled = true
                        errorTextView.isInvisible = true
                    }
                    is ValuOtpState.Error -> {
                        otpInputView.isEnabled = true
                        resendCodeButton.isEnabled = true
                        purchaseButton.isEnabled = true
                        errorTextView.isInvisible = false
                        errorTextView.text = state.message
                    }
                    is ValuOtpState.PurchaseSuccess -> {
                        // Nothing to change
                    }
                }
            }
        }
    }

    private fun onPurchaseButtonClicked(view: View) {
        if (binding.otpInputView.isFilled) {
            val otp = binding.otpInputView.otp
            viewModel.onPurchaseButtonClicked(otp)
        }
    }

    private fun onBackButtonClicked() {
        confirmCancellationDialog(onPositive = { _, _ -> viewModel.navigateCancel() })
    }

    private fun onCancelButtonClicked(view: View) {
        onBackButtonClicked()
    }

    private class ViewModelFactory(
            private val paymentViewModel: PaymentViewModel,
            private val valuSharedViewModel: ValuSharedViewModel
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val plansResponse = valuSharedViewModel.installmentPlansResponse!!
            val selectedPlan = valuSharedViewModel.selectedInstallmentPlan!!

            return ValuOtpViewModel(
                    paymentViewModel,
                    SdkComponent.valuService,
                    SdkComponent.orderService,
                    SdkComponent.connectivity,
                    customerIdentifier = valuSharedViewModel.customerIdentifier!!,
                    bnplOrderId = requireNotNull(plansResponse.bnplOrderId),
                    currency = plansResponse.currency,
                    totalAmount = plansResponse.totalAmount,
                    adminFees = selectedPlan.adminFees,
                    downPaymentAmount = selectedPlan.downPayment,
                    giftCardAmount = plansResponse.giftCardAmount,
                    campaignAmount = plansResponse.campaignAmount,
                    tenure = selectedPlan.tenorMonth,
            ) as T
        }
    }
}