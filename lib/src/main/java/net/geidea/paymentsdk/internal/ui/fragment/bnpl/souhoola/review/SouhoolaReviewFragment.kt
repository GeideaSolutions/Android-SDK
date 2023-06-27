package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.review

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentSouhoolaReviewBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.SouhoolaSharedViewModel
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.souhoolaNavGraphViewModel
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.internal.util.Logger.logi

@GeideaSdkInternal
internal class SouhoolaReviewFragment :
    BaseFragment<SouhoolaReviewViewModel>(R.layout.gd_fragment_souhoola_review) {

    override val viewModel: SouhoolaReviewViewModel
            by viewModels { ViewModelFactory(paymentViewModel, souhoolaSharedViewModel) }

    private val souhoolaSharedViewModel: SouhoolaSharedViewModel
            by souhoolaNavGraphViewModel(lazy { paymentViewModel })

    private val binding by viewBinding(GdFragmentSouhoolaReviewBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            addBackListener(viewModel::navigateBack)

            with(appBarWithStepper.stepper) {
                setOnBackClickListener { viewModel.navigateBack() }
                currentStep = 3
                stepCount = 4
                text = getString(R.string.gd_souhoola_review_purchase_summary)
            }

            nextButton.isEnabled = false
            nextButton.setOnClickListener {
                runSafely(::onNextButtonClicked)
            }

            cancelButton.setOnClickListener {
                runSafely(::onCancelButtonClicked)
            }

            downPaymentOptions.downPaymentOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                viewModel.onCashOnDeliverySelected(checkedId == R.id.payOnDeliveryRadioButton)
            }

            viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
                TransitionManager.beginDelayedTransition(nestedScrollView)

                if (itemsLinearLayout.childCount == 0) {
                    state.items.map { it.inflate(layoutInflater) }
                        .onEach(itemsLinearLayout::addView)
                }
                nextButton.isEnabled = state.proceedButtonEnabled
                nextButtonProgress.isVisible = state.proceedButtonProgressVisible
                nextButton.setText(if (state.proceedButtonProgressVisible) emptyText() else state.proceedButtonTitle)
                progress.isVisible = state.progressVisible
                itemsLinearLayout.isVisible = !state.progressVisible
                appBarWithStepper.stepper.stepCount = state.stepCount

                with(downPaymentOptions) {
                    downPaymentOptionsRadioGroup.isVisible = state.isDownPaymentOptionsVisible

                    val amountAndCurrencyText = formatAmount(
                        souhoolaSharedViewModel.upfrontAmount,
                        paymentViewModel.initialPaymentData.currency
                    )

                    payNowRadioButton.setText(
                        templateText(R.string.gd_bnpl_pay_now, amountAndCurrencyText)
                    )
                    payOnDeliveryRadioButton.setText(
                        templateText(R.string.gd_bnpl_pay_on_delivery, amountAndCurrencyText)
                    )
                }
            }
        }
    }

    private fun onNextButtonClicked() {
        logi("SouhoolaReviewFragment.onNextButtonClicked()")
        viewModel.onNextButtonClicked()
    }

    private fun onCancelButtonClicked() {
        confirmCancellationDialog(onPositive = { _, _ -> viewModel.navigateCancel() })
    }

    private class ViewModelFactory(
        private val paymentViewModel: PaymentViewModel,
        private val souhoolaSharedViewModel: SouhoolaSharedViewModel,
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SouhoolaReviewViewModel(
                SdkComponent.souhoolaService,
                SdkComponent.connectivity,
                paymentViewModel = paymentViewModel,
                souhoolaSharedViewModel = souhoolaSharedViewModel,
            ) as T
        }
    }
}