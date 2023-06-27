package net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.plan

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentValuInstallmentPlanBinding
import net.geidea.paymentsdk.flow.pay.PaymentData
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.valu.ValuSharedViewModel
import net.geidea.paymentsdk.internal.util.Logger.logi
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.setText
import net.geidea.paymentsdk.internal.util.viewBinding
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter
import java.math.BigDecimal


internal class ValuInstallmentPlanFragment : BaseFragment<ValuInstallmentPlanViewModel>(R.layout.gd_fragment_valu_installment_plan) {

    private val args: ValuInstallmentPlanFragmentArgs by navArgs()

    override val viewModel: ValuInstallmentPlanViewModel by viewModels { ViewModelFactory(paymentViewModel, valuSharedViewModel, args) }

    private val valuSharedViewModel: ValuSharedViewModel by navGraphViewModels(R.id.gd_valu_graph)

    private val binding by viewBinding(GdFragmentValuInstallmentPlanBinding::bind)

    private val paymentData: PaymentData get() = paymentViewModel.initialPaymentData

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with (binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            addBackListener(viewModel::navigateBack)

            with(appBarWithStepper.stepper) {
                setOnBackClickListener { viewModel.navigateBack() }
                currentStep = 2
                stepCount = 3
                text = getString(R.string.gd_bnpl_choose_installment_plan)
            }

            nextButton.isEnabled = false
            nextButton.setOnClickListener {
                runSafely(::onNextButtonClicked)
            }

            cancelButton.setOnClickListener {
                runSafely(::onCancelButtonClicked)
            }

            installmentPlanView.addDownPaymentAmountTextWatcher(object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    viewModel.onDownPaymentTextChanged(s.toString())
                }
            })

            installmentPlanView.addGiftCardAmountTextWatcher(object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    viewModel.onGiftCardAmountChanged(s.toString())
                }
            })

            installmentPlanView.addCampaignAmountTextWatcher(object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    viewModel.onCampaignAmountChanged(s.toString())
                }
            })

            installmentPlanView.setOnInstallmentPlanSelectedListener { installmentPlan ->
                viewModel.onInstallmentPlanSelected(installmentPlan)
            }

            installmentPlanView.setTotalAmount(paymentData.amount, paymentData.currency)

            downPaymentOptions.downPaymentOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                viewModel.onCashOnDeliverySelected(cashOnDelivery = checkedId == R.id.payOnDeliveryRadioButton)
            }

            viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is ValuInstallmentPlanState.Initial -> {
                        installmentPlanView.installmentPlans = emptyList()
                        installmentPlanView.financedAmount = BigDecimal.ZERO
                        nextButton.isEnabled = false
                        downPaymentOptions.downPaymentOptionsRadioGroup.isVisible = false
                    }
                    is ValuInstallmentPlanState.Loading -> {
                        //installmentPlanView.installmentPlans = emptyList()
                        nextButton.isEnabled = false
                        downPaymentOptions.downPaymentOptionsRadioGroup.isVisible = false
                    }
                    is ValuInstallmentPlanState.Success -> {
                        installmentPlanView.installmentPlans = state.response.installmentPlans
                        installmentPlanView.financedAmount = state.response.financedAmount
                        installmentPlanView.error = null
                        installmentPlanView.downPaymentAmountError = null
                        installmentPlanView.giftCardAmountError = null
                        installmentPlanView.campaignAmountError = null
                        nextButton.isEnabled = state.nextButtonEnabled
                        appBarWithStepper.stepper.stepCount = state.stepCount

                        downPaymentOptions.downPaymentOptionsRadioGroup.isVisible = state.downPaymentOptionsVisible
                        downPaymentOptions.payNowRadioButton.setText(state.downPaymentNowText)
                        downPaymentOptions.payOnDeliveryRadioButton.setText(state.downPaymentOnDeliveryText)
                    }
                    is ValuInstallmentPlanState.ValidationErrors -> {
                        installmentPlanView.installmentPlans = emptyList()
                        installmentPlanView.financedAmount = BigDecimal.ZERO
                        with (installmentPlanView) {
                            downPaymentAmountError = state.errors["DownPayment"]?.firstOrNull()
                            giftCardAmountError = state.errors["GiftCardAmount"]?.firstOrNull()
                            campaignAmountError = state.errors["CampaignAmount"]?.firstOrNull()
                        }
                        nextButton.isEnabled = false
                        downPaymentOptions.downPaymentOptionsRadioGroup.isVisible = false
                    }
                    is ValuInstallmentPlanState.ProcessingError -> {
                        installmentPlanView.installmentPlans = emptyList()
                        installmentPlanView.financedAmount = BigDecimal.ZERO
                        installmentPlanView.error = state.message
                        installmentPlanView.downPaymentAmountError = null
                        installmentPlanView.giftCardAmountError = null
                        installmentPlanView.campaignAmountError = null
                        nextButton.isEnabled = false
                        downPaymentOptions.downPaymentOptionsRadioGroup.isVisible = false
                    }
                }
            }
        }
    }

    private fun onNextButtonClicked() {
        logi("ValuInstallmentPlanFragment.onNextButtonClicked()")
        viewModel.onNextButtonClicked()
    }

    private fun onCancelButtonClicked() {
        confirmCancellationDialog(onPositive = { _, _ -> viewModel.navigateCancel() })
    }

    private class ViewModelFactory(
            private val paymentViewModel: PaymentViewModel,
            private val valuSharedViewModel: ValuSharedViewModel,
            private val args: ValuInstallmentPlanFragmentArgs,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ValuInstallmentPlanViewModel(
                    paymentViewModel,
                    SdkComponent.valuService,
                    SdkComponent.connectivity,
                    valuSharedViewModel = valuSharedViewModel,
                    phoneNumber = args.customerIdentifier,
                    step = args.step,
            ) as T
        }
    }
}