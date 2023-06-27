package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.otp

import android.os.Bundle
import android.transition.TransitionManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentSouhoolaOtpBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.SouhoolaSharedViewModel
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.souhoolaNavGraphViewModel
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.setText
import net.geidea.paymentsdk.internal.util.viewBinding

@GeideaSdkInternal
internal class SouhoolaOtpFragment : BaseFragment<SouhoolaOtpViewModel>(R.layout.gd_fragment_souhoola_otp) {

    private val args: SouhoolaOtpFragmentArgs by navArgs()

    override val viewModel: SouhoolaOtpViewModel by viewModels { ViewModelFactory(paymentViewModel, souhoolaSharedViewModel) }

    private val souhoolaSharedViewModel: SouhoolaSharedViewModel
            by souhoolaNavGraphViewModel(lazy { paymentViewModel })

    private val binding by viewBinding(GdFragmentSouhoolaOtpBinding::bind)

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

            viewModel.timeRemainingLiveData.observe(viewLifecycleOwner, timeRemainingTextView::setText)
            viewModel.codesLeftLiveData.observe(viewLifecycleOwner, codesLeftTextView::setText)

            viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->

                TransitionManager.beginDelayedTransition(nestedScrollView)

                otpHelpTextView.isVisible = state.otpHelpTextVisible
                otpInputView.isEnabled = state.otpInputEnabled
                resendCodeButton.isEnabled = state.resendCodeButtonEnabled
                purchaseButton.isEnabled = state.purchaseButtonEnabled
                purchaseButtonProgress.isVisible = state.purchaseButtonProgressVisible
                purchaseButton.setText(state.purchaseButtonText)
                errorTextView.isInvisible = state.errorTextInvisible
                errorTextView.setText(state.errorText)
                timeRemainingTextView.isVisible = state.timeRemainingVisible
                codesLeftTextView.isVisible = state.codesLeftVisible
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
            private val souhoolaSharedViewModel: SouhoolaSharedViewModel
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SouhoolaOtpViewModel(
                    paymentViewModel,
                    souhoolaSharedViewModel,
                    SdkComponent.souhoolaService,
                    SdkComponent.orderService,
                    SdkComponent.connectivity,
            ) as T
        }
    }
}