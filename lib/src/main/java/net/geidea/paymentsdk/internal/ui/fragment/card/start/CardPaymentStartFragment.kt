package net.geidea.paymentsdk.internal.ui.fragment.card.start

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import net.geidea.paymentsdk.GdCardGraphArgs
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.card.BaseCardPaymentViewModel
import net.geidea.paymentsdk.internal.ui.fragment.card.cardNavGraphViewModel
import net.geidea.paymentsdk.internal.util.Logger.logi

internal class CardPaymentStartFragment : BaseFragment<CardPaymentStartViewModel>() {

    override val viewModel: CardPaymentStartViewModel by viewModels()

    private val cardGraphArgs: GdCardGraphArgs by navArgs()
    private val cardPaymentViewModel: BaseCardPaymentViewModel by cardNavGraphViewModel(lazy { paymentViewModel }, lazy { cardGraphArgs })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Important: this is needed to trigger evaluation of the Lazy value of cardPaymentViewModel
        logi("cardPaymentViewModel of type: ${cardPaymentViewModel.javaClass.simpleName}")
    }
}