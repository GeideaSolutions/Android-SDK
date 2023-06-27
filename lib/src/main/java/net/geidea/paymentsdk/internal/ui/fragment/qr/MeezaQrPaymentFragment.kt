package net.geidea.paymentsdk.internal.ui.fragment.qr

import android.os.Bundle
import android.system.Os.bind
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentMeezaQrPaymentBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.widget.setup
import net.geidea.paymentsdk.internal.util.decodeImageBase64
import net.geidea.paymentsdk.internal.util.viewBinding
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

internal class MeezaQrPaymentFragment : BaseFragment<MeezaQrPaymentViewModel>(R.layout.gd_fragment_meeza_qr_payment) {

    private val args: MeezaQrPaymentFragmentArgs by navArgs()

    private val binding by viewBinding(GdFragmentMeezaQrPaymentBinding::bind)

    override val viewModel: MeezaQrPaymentViewModel by viewModels {
        ViewModelFactory(paymentViewModel, args)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            val isStep = args.step != null
            appBarWithStepper.setup(args.step, underlayView = binding.nestedScrollView)
            appBarWithStepper.stepper.isVisible = isStep
            // Disallow back navigation for Meeza QR
            appBarWithStepper.stepper.isBackButtonVisible = false
            if (isStep) {
                appBarWithStepper.stepper.text = getString(R.string.gd_bnpl_process_down_payment)
            }
            addBackListener { /* Disallow back navigation for Meeza QR */ }

            cancelButton.setOnClickListener(::onCancelButtonClicked)

            val paymentData = viewModel.paymentData

            currencyTextView.text = paymentData.currency

            require(paymentData.amount.signum() == 1) { "Positive amount required" }

            // The integral part of the amount formatted as a String
            val integerAmountString = paymentData.amount.toBigInteger().toString()

            val myFormatter = DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.US))
            val fractionalPart = paymentData.amount.remainder(BigDecimal.ONE)
            // Remove the zero before floating point
            val fractionAmountString = myFormatter.format(fractionalPart).substring(1)

            amountIntegerPartTextView.text = integerAmountString
            amountFractionPartTextView.text = fractionAmountString

            merchantNameTextView.text = args.merchantName

            //requestToPayButton.setOnClickListener { viewModel.onRequestToPayClicked() }

            viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
                TransitionManager.beginDelayedTransition(view as ViewGroup)
                when (state) {
                    is MeezaQrPaymentState.Generating -> {
                        idleStateLinearLayout.isVisible = true
                        errorStateLinearLayout.isVisible = false
                        qrCodeImageView.setImageDrawable(null)
                    }
                    is MeezaQrPaymentState.Idle -> {
                        qrCodeImageView.setImageDrawable(decodeImageBase64(resources, state.qrCodeImageBase64))
                        idleStateLinearLayout.isVisible = true
                        errorStateLinearLayout.isVisible = false
                    }
                    is MeezaQrPaymentState.Error -> {
                        /*
                        idleStateLinearLayout.isVisible = false
                        errorStateLinearLayout.isVisible = true
                        */
                        qrCodeImageView.setImageDrawable(null)
                    }
                }
            }

            // Old R2P flow
            /*getNavigationResult<Boolean>(R.id.gd_meezaqrpaymentfragment, MeezaQrRequestPaymentFragment.KEY_RESULT) { result ->
                if (result) {
                    viewModel.onRequestPaymentSuccess()
                }
            }*/

            viewModel.start()
        }
    }

    private fun onCancelButtonClicked(view: View) {
        viewModel.navigateCancel()
    }

    private class ViewModelFactory(
            private val paymentViewModel: PaymentViewModel,
            private val args: MeezaQrPaymentFragmentArgs,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MeezaQrPaymentViewModel(
                paymentViewModel = paymentViewModel,
                meezaService = SdkComponent.meezaService,
                paymentIntentService = SdkComponent.paymentIntentService,
                orderService = SdkComponent.orderService,
                connectivity = SdkComponent.connectivity,
                formatter = SdkComponent.formatter,
                downPaymentAmount = args.downPaymentAmount,
                qrMessage = args.qrMessage,
                qrCodeImageBase64 = args.qrCodeImageBase64,
                paymentIntentId = args.paymentIntentId,
            ) as T
        }
    }
}