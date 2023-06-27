package net.geidea.paymentsdk.internal.ui.fragment.card.cvv

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.ColorInt
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.geidea.paymentsdk.GdCardGraphArgs
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentCvvInputBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.card.BaseCardPaymentViewModel
import net.geidea.paymentsdk.internal.ui.fragment.card.TokenPaymentViewModel3dsV2
import net.geidea.paymentsdk.internal.ui.fragment.card.cardNavGraphViewModel
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.setText
import net.geidea.paymentsdk.internal.util.textOrNull
import net.geidea.paymentsdk.internal.util.viewBinding


internal class CvvInputFragment : BaseFragment<CvvInputViewModel>(R.layout.gd_fragment_cvv_input) {

    private val cardGraphArgs: GdCardGraphArgs by navArgs()
    private val cardPaymentViewModel: BaseCardPaymentViewModel by cardNavGraphViewModel(
        lazy { paymentViewModel },
        lazy { cardGraphArgs }
    )

    private val args: CvvInputFragmentArgs by navArgs()

    private val binding by viewBinding(GdFragmentCvvInputBinding::bind)

    override val viewModel: CvvInputViewModel by viewModels {
        ViewModelFactory(
            paymentViewModel = paymentViewModel,
            tokenPaymentViewModel = cardPaymentViewModel as TokenPaymentViewModel3dsV2,
            args = args
        )
    }

    @ColorInt
    private var statusBarColor: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        addBackListener { confirmCancellationDialog { _, _ -> viewModel.onCancelConfirmed() } }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        infoIconImageView.setOnClickListener { showCvvHint() }
        cvvInputLayout.setEndIconOnClickListener { showCvvHint() }
        cvvEditText.setOnValidStatusListener(viewModel)
        cvvEditText.setOnInvalidStatusListener(viewModel)
        cvvEditText.setOnEditorActionListener { view, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (cvvEditText.isValid) {
                    val cvv = cvvEditText.textOrNull!!
                    cvvEditText.setText("")
                    viewModel.onNextButtonClicked(cvv)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
        nextButton.setOnClickListener {
            val cvv = cvvEditText.textOrNull!!
            cvvEditText.setText("")
            viewModel.onNextButtonClicked(cvv)
        }
        cancelButton.setOnClickListener {
            confirmCancellationDialog(onPositive = { _, _ -> viewModel.onCancelConfirmed() })
        }

        viewModel.loadingLiveData.observe(viewLifecycleOwner) { loading ->
            TransitionManager.beginDelayedTransition(root)
            progress.isVisible = loading
        }
        viewModel.cardVisibleLiveData.observe(viewLifecycleOwner) { visible ->
            TransitionManager.beginDelayedTransition(root)
            cardLinearLayout.isInvisible = !visible
        }

        viewModel.cardBrandLiveData.observe(viewLifecycleOwner) { cardBrand ->
            schemeLogoImageView.setImageResource(cardBrand.logo)
            cvvEditText.setValidator(viewModel.createCvvValidator(cardBrand))
            cvvEditText.maxLength = cardBrand.securityCodeLengthRange.last
        }
        viewModel.maskedCardNumberLiveData.observe(viewLifecycleOwner, maskedCardNumberTextView::setText)
        viewModel.expiryTextLiveData.observe(viewLifecycleOwner, expiryDateTextView::setText)
        viewModel.expiredLiveData.observe(viewLifecycleOwner) { expired ->
            if (expired) {
                val errorColor = MaterialColors.getColor(requireContext(), R.attr.colorError, Color.RED)
                expiryDateTextView.setTextColor(errorColor)
            }
        }
        viewModel.cvvInputEnabledLiveData.observe(viewLifecycleOwner, cvvEditText::setEnabled)
        viewModel.nextButtonEnabledLiveData.observe(viewLifecycleOwner, nextButton::setEnabled)

        setupTransparency(true)
    }

    override fun onDestroyView() {
        setupTransparency(false)

        super.onDestroyView()
    }

    private fun setupTransparency(transparent: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val activity = requireActivity()
            val window = requireActivity().window!!
            activity.setTranslucent(transparent)
            if (transparent) {
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                statusBarColor = window.statusBarColor
                window.statusBarColor = Color.TRANSPARENT
            } else {
                window.statusBarColor = statusBarColor
            }
        }
    }

    private fun showCvvHint() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.gd_ic_cvv_hint)
            .setTitle(R.string.gd_dlg_title_cvv_help)
            .setMessage(R.string.gd_dlg_msg_cvv_help)
            .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

private class ViewModelFactory(
    private val paymentViewModel: PaymentViewModel,
    private val tokenPaymentViewModel: TokenPaymentViewModel3dsV2,
    private val args: CvvInputFragmentArgs,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CvvInputViewModel(
            paymentViewModel = paymentViewModel,
            tokenPaymentViewModel = tokenPaymentViewModel,
            connectivity = SdkComponent.connectivity,
            tokenService = SdkComponent.tokenService,
            tokenId = args.tokenId
        ) as T
    }
}