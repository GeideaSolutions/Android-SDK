package net.geidea.paymentsdk.internal.ui.fragment.receipt

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import com.google.android.material.color.MaterialColors
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentReceiptBinding
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.util.viewBinding

internal class ReceiptFragment : BaseFragment<ReceiptViewModel>(R.layout.gd_fragment_receipt) {

    private val args: ReceiptFragmentArgs by navArgs()

    private val binding by viewBinding(GdFragmentReceiptBinding::bind)

    override val viewModel: ReceiptViewModel by viewModels { ViewModelFactory(args) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            addBackListener(viewModel::navigateBack)

            headerFrameLayout.setBackgroundColor(
                MaterialColors.getColor(view, com.google.android.material.R.attr.colorOnSurface)
            )

            returnButton.setOnClickListener { viewModel.onReturnButtonClicked() }
            tryAgainButton.setOnClickListener { viewModel.onTryAgainButtonClicked() }

            viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->

                TransitionManager.beginDelayedTransition(nestedScrollView)

                when (state) {
                    is ReceiptState.Loading -> {
                        receiptCardView.isVisible = false
                        circularProgressIndicator.isVisible = true
                        geideaLogoImageView.isVisible = false
                    }
                    is ReceiptState.TransactionSuccess -> {
                        receiptCardView.isVisible = true
                        receiptItemListLinearLayout.isVisible = true
                        circularProgressIndicator.isVisible = false
                        labelTransactionStatus.text = state.statusText.toCharSequence()
                        loadingErrorTextView.isVisible = false
                        statusImageView.setImageResource(state.statusImage)
                        state.items.map { it.inflate(layoutInflater) }
                                .onEach(receiptItemListLinearLayout::addView)
                        geideaLogoImageView.isVisible = true
                    }
                    is ReceiptState.TransactionError -> {
                        receiptCardView.isVisible = true
                        receiptItemListLinearLayout.isVisible = true
                        circularProgressIndicator.isVisible = false
                        labelTransactionStatus.text = state.statusText.toCharSequence()
                        statusImageView.setImageResource(state.statusImage)
                        state.items.map { it.inflate(layoutInflater) }
                                .onEach(receiptItemListLinearLayout::addView)
                        geideaLogoImageView.isVisible = true
                    }
                    is ReceiptState.LoadingError -> {
                        receiptCardView.isVisible = true
                        receiptItemListLinearLayout.isVisible = false
                        circularProgressIndicator.isVisible = false
                        loadingErrorTextView.isVisible = true
                        tryAgainButton.isEnabled = true
                        statusImageView.setImageResource(R.drawable.gd_ic_x_circle)
                    }
                }
            }
        }
    }

    private class ViewModelFactory(
        private val args: ReceiptFragmentArgs,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReceiptViewModel(
                    receiptService = SdkComponent.receiptService,
                    connectivity = SdkComponent.connectivity,
                    args.args,
            ) as T
        }
    }
}