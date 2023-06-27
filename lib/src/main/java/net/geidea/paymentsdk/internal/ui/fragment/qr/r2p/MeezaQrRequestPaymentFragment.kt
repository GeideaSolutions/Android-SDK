package net.geidea.paymentsdk.internal.ui.fragment.qr.r2p

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdDialogMeezaPhoneBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseDialogFragment
import net.geidea.paymentsdk.internal.util.hideKeyboard
import net.geidea.paymentsdk.internal.util.setNavigationResult
import net.geidea.paymentsdk.internal.util.textOrNull
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.receiver.MeezaMobileNumberOrDigitalIdValidator
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener

@Deprecated("From the old flow which is not used anymore")
@GeideaSdkInternal
internal class MeezaQrRequestPaymentFragment : BaseDialogFragment<MeezaQrRequestPaymentViewModel>() {

    private val args: MeezaQrRequestPaymentFragmentArgs by navArgs()

    override val viewModel: MeezaQrRequestPaymentViewModel by viewModels {
        ViewModelFactory(paymentViewModel, args)
    }

    private lateinit var binding: GdDialogMeezaPhoneBinding

    private lateinit var positiveButton: Button
    private lateinit var negativeButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = GdDialogMeezaPhoneBinding.inflate(requireActivity().layoutInflater).apply {
            receiverIdEditText.setValidator(MeezaMobileNumberOrDigitalIdValidator)
            receiverIdEditText.setOnErrorListener(TextInputErrorListener(receiverIdInputLayout))
            receiverIdEditText.setOnInvalidStatusListener { value: String?, status: ValidationStatus.Invalid ->
                if (!value.isNullOrEmpty()) {
                    receiverIdEditText.updateErrorMessage()
                }
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.gd_meezaqr_r2p_mobile_number))
                .setView(binding.root)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.gd_meezaqr_r2p_send), null)
                .setNegativeButton(android.R.string.cancel, null)
                .create()

        dialog.show()

        positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)

        positiveButton.setOnClickListener {
            with(binding) {
                receiverIdEditText.updateErrorMessage()

                if (receiverIdEditText.validationStatus == ValidationStatus.Valid) {
                    viewModel.onSendButtonClicked(receiverIdEditText.textOrNull!!)
                }
            }
        }

        viewModel.stateLiveData.observe(this@MeezaQrRequestPaymentFragment, ::onStateChanged)

        return dialog
    }

    private fun onStateChanged(state: MeezaQrRequestPaymentState) = with(binding) {
        when (state) {
            is MeezaQrRequestPaymentState.Sending -> {
                // Temporarily change the UI to show the progress
                progressLinearLayout.isVisible = true
                receiverIdEditText.clearFocus()
                receiverIdEditText.isEnabled = false
                positiveButton.isEnabled = false
                negativeButton.isEnabled = false
                hideKeyboard(binding.root)
            }
            MeezaQrRequestPaymentState.Success -> {
                setNavigationResult("requestPaymentSuccessful", true)
                dismiss()
            }
            MeezaQrRequestPaymentState.Error -> {
                // Re-enable the UI to accept input again. After an error user could retry.
                receiverIdEditText.isEnabled = true
                receiverIdEditText.requestFocus()
                positiveButton.isEnabled = true
                negativeButton.isEnabled = true
                progressLinearLayout.isVisible = false
            }
        }
    }

    private class ViewModelFactory(
            private val paymentViewModel: PaymentViewModel,
            private val args: MeezaQrRequestPaymentFragmentArgs,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MeezaQrRequestPaymentViewModel(
                    paymentViewModel = paymentViewModel,
                    meezaService = SdkComponent.meezaService,
                    connectivity = SdkComponent.connectivity,
                    qrMessage = args.qrMessage,
            ) as T
        }
    }

    companion object {
        const val KEY_RESULT = "requestPaymentSuccessful"       // Boolean
    }
}