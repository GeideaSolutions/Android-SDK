package net.geidea.paymentsdk.internal.ui.fragment.base

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.flow.pay.PaymentActivity
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.ui.widget.IconSnackbar
import net.geidea.paymentsdk.internal.ui.widget.snackbar
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.observeEvent

@GeideaSdkInternal
internal abstract class BaseFragment<VM : BaseViewModel> : Fragment {

    constructor() : super()
    constructor(@LayoutRes layoutResId: Int) : super(layoutResId)

    protected val paymentActivity: PaymentActivity get() = requireActivity() as PaymentActivity

    protected val paymentViewModel: PaymentViewModel by activityViewModels()

    protected abstract val viewModel: VM

    private var iconSnackbar: IconSnackbar? = null

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationLiveEvent.observeEvent(viewLifecycleOwner, ::handleNavigation)
        viewModel.snackbarLiveEvent.observeEvent(viewLifecycleOwner, ::showSnack)
    }

    private fun handleNavigation(navCommand: NavigationCommand) {
        paymentActivity.handleNavigation(navCommand)
    }

    protected open fun showSnack(snack: Snack) {
        iconSnackbar = snackbar(paymentActivity.coordinatorLayout, snack)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        iconSnackbar?.dismiss()
        iconSnackbar = null
    }

    protected fun NativeText.toCharSequence(): CharSequence = this.toCharSequence(requireContext())

    protected fun addBackListener(listener: () -> Unit) {
        (activity as AppCompatActivity)
                .onBackPressedDispatcher
                .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { listener() }
        })
    }

    protected fun runSafely(block: () -> Unit) {
        paymentActivity.runSafely(block)
    }
}