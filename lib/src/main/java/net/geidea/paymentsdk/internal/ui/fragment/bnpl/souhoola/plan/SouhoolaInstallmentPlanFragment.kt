package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.plan

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionManager
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentSouhoolaInstallmentPlanBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.SouhoolaSharedViewModel
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.souhoolaNavGraphViewModel
import net.geidea.paymentsdk.internal.util.Logger.logi
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.observeEvent
import net.geidea.paymentsdk.internal.util.setText
import net.geidea.paymentsdk.internal.util.viewBinding
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter


internal class SouhoolaInstallmentPlanFragment :
    BaseFragment<SouhoolaInstallmentPlanViewModel>(R.layout.gd_fragment_souhoola_installment_plan) {

    override val viewModel: SouhoolaInstallmentPlanViewModel by viewModels {
        ViewModelFactory(
            paymentViewModel,
            souhoolaSharedViewModel
        )
    }

    private val souhoolaSharedViewModel: SouhoolaSharedViewModel
            by souhoolaNavGraphViewModel(lazy { paymentViewModel })

    private val binding by viewBinding(GdFragmentSouhoolaInstallmentPlanBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            addBackListener(viewModel::navigateBack)
            with(appBarWithStepper.stepper) {
                setOnBackClickListener { viewModel.navigateBack() }
                currentStep = 2
                stepCount = 3
                text = getString(R.string.gd_bnpl_choose_installment_plan)
            }

            installmentPlanView.setDownPaymentOnEditorActionListener { _, actionId, keyEvent ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    onNextButtonClicked()
                    true
                } else
                    false
            }

            nextButton.isEnabled = false
            nextButton.setOnClickListener {
                runSafely(::onNextButtonClicked)
            }

            cancelButton.setOnClickListener {
                runSafely(::onCancelButtonClicked)
            }

            with(installmentPlanView) {
                addDownPaymentAmountTextWatcher(object : TextWatcherAdapter() {
                    override fun afterTextChanged(s: Editable) {
                        viewModel.onDownPaymentTextChanged(s.toString())
                    }
                })

                setOnInstallmentPlanSelectedListener(viewModel::onInstallmentPlanSelected)

                setTotalAmount(
                    totalAmount = paymentViewModel.initialPaymentData.amount,
                    currency = paymentViewModel.initialPaymentData.currency
                )

                selectedInstallmentPlan = souhoolaSharedViewModel.selectedInstallmentPlan
                isUpfrontAmountsVisible = false
            }

            viewModel.minDownPaymentLiveData.observeEvent(viewLifecycleOwner) { downPayment ->
                val existingDownPayment = installmentPlanView.downPaymentAmount
                //if (existingDownPayment notEq downPayment) {
                    installmentPlanView.downPaymentAmount = downPayment
                //}
            }

            viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
                logi("Souhoola installment plans state: $state")

                TransitionManager.beginDelayedTransition(nestedScrollView)

                with(installmentPlanView) {
                    installmentPlans = state.installmentPlans
                    financedAmount = state.financedAmount
                    financedAmountErrorTextView.isVisible = state.financedAmountError != null
                    financedAmountErrorTextView.setText(state.financedAmountError)
                    error = state.error?.toCharSequence(requireContext())
                    isDownPaymentVisible = state.downPaymentVisible
                    downPaymentHelperText = state.downPaymentHelperText?.toCharSequence(requireContext())
                    downPaymentAmountError = state.downPaymentError?.toCharSequence(requireContext())
                    isProgressVisible = state.internalState == InternalState.Loading
                }
                nextButton.isEnabled = state.nextButtonEnabled
                appBarWithStepper.stepper.stepCount = state.stepCount
            }
        }
    }

    private fun onNextButtonClicked() {
        logi("SouhoolaInstallmentPlanFragment.onNextButtonClicked()")
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
            return SouhoolaInstallmentPlanViewModel(
                paymentViewModel,
                SdkComponent.souhoolaService,
                SdkComponent.connectivity,
                souhoolaSharedViewModel = souhoolaSharedViewModel,
            ) as T
        }
    }
}