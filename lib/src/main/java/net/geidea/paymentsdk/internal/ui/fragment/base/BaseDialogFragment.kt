package net.geidea.paymentsdk.internal.ui.fragment.base

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.activityViewModels
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.pay.PaymentActivity
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.ui.widget.snackbar
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.observeEvent

@GeideaSdkInternal
internal abstract class BaseDialogFragment<VM : BaseViewModel> : AppCompatDialogFragment {

    constructor() : super()
    constructor(@LayoutRes layoutResId: Int) : super(layoutResId)

    protected val paymentActivity: PaymentActivity get() = requireActivity() as PaymentActivity

    protected val paymentViewModel: PaymentViewModel by activityViewModels()

    protected abstract val viewModel: VM

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigationLiveEvent.observeEvent(this, ::handleNavigation)
        viewModel.snackbarLiveEvent.observeEvent(this, ::showSnack)
    }

    private fun handleNavigation(navCommand: NavigationCommand) {
        paymentActivity.handleNavigation(navCommand)
    }

    protected open fun showSnack(snack: Snack) {
        snackbar(snack)
    }

    protected fun NativeText.toCharSequence(): CharSequence = this.toCharSequence(requireContext())

    protected fun addBackListener(listener: () -> Unit) {
        (activity as AppCompatActivity)
                .onBackPressedDispatcher
                .addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { listener() }
        })
    }

    protected fun runSafely(block: () -> Unit) {
        paymentActivity.runSafely(block)
    }
}