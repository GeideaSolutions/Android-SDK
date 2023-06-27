package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseViewModel
import net.geidea.paymentsdk.internal.util.Logger.loge
import net.geidea.paymentsdk.internal.util.orZero
import net.geidea.paymentsdk.model.bnpl.souhoola.CancelTransactionRequest
import net.geidea.paymentsdk.model.bnpl.souhoola.InstallmentPlan
import net.geidea.paymentsdk.model.bnpl.souhoola.VerifyCustomerResponse
import java.math.BigDecimal

@GeideaSdkInternal
internal class SouhoolaSharedViewModel(
    private val paymentViewModel: PaymentViewModel
) : BaseViewModel() {
    // From Verify screen input
    var customerIdentifier: String? = null
    var customerPin: String? = null

    // From /verifyCustomer response
    var verifyResponse: VerifyCustomerResponse? = null

    // From /retrieveInstallmentPlans and after user selects a plan
    var selectedInstallmentPlan: InstallmentPlan? = null

    // From /reviewTransaction response
    var souhoolaTransactionId: String? = null
    var downPayment: BigDecimal? = null
    var netAdministrativeFees: BigDecimal? = null

    // From /selectInstallmentPlan response
    var orderId: String?
        get() = paymentViewModel.orderId
        set(newValue) { paymentViewModel.orderId = newValue }

    // Set to false after the purchase is finalized and is successful
    var mustCancel: Boolean = false

    init {
        // Mark the transaction in progress to be canceled
        mustCancel = true
    }

    val upfrontAmount: BigDecimal get() = downPayment.orZero() + netAdministrativeFees.orZero()

    override fun onCleared() {
        super.onCleared()

        if (paymentViewModel.resultLiveData.value !is GeideaResult.Success) {
            GlobalScope.launch { cancelIfNeeded() }
        }
    }

    internal suspend fun cancelIfNeeded() {
        if (souhoolaTransactionId != null && customerIdentifier != null && customerPin != null) {
            try {
                val cancelRequest = CancelTransactionRequest(
                    customerIdentifier = customerIdentifier!!,
                    customerPin = customerPin!!,
                    souhoolaTransactionId = souhoolaTransactionId.toString()
                )
                SdkComponent.souhoolaService.deleteCancel(cancelRequest)
                souhoolaTransactionId = null
            } catch (e: Exception) {
                loge(e.stackTraceToString())
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
@GeideaSdkInternal
internal class SouhoolaSharedViewModelFactory(
    private val paymentViewModel: PaymentViewModel,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SouhoolaSharedViewModel(paymentViewModel) as T
    }
}

internal inline fun <reified VM : SouhoolaSharedViewModel> Fragment.souhoolaNavGraphViewModel(
    paymentViewModelLazy: Lazy<PaymentViewModel>,
): Lazy<VM> = navGraphViewModels(
    navGraphId = R.id.gd_souhoola_graph,
    factoryProducer = { SouhoolaSharedViewModelFactory(paymentViewModel = paymentViewModelLazy.value) }
)