package net.geidea.paymentsdk.flow.pay

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.WindowManager.LayoutParams.FLAG_SECURE
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import net.geidea.paymentsdk.BuildConfig
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdActivityPaymentBinding
import net.geidea.paymentsdk.flow.GeideaContract
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.NavigationCommand
import net.geidea.paymentsdk.internal.ui.widget.IconSnackbar
import net.geidea.paymentsdk.internal.ui.widget.snackbar
import net.geidea.paymentsdk.internal.util.ActivityLifecycleLoggingCallbacks
import net.geidea.paymentsdk.internal.util.LocaleUtils.withSdkLocale
import net.geidea.paymentsdk.internal.util.Logger.loge
import net.geidea.paymentsdk.internal.util.Logger.logi
import net.geidea.paymentsdk.internal.util.observeEvent
import net.geidea.paymentsdk.internal.util.viewBinding
import net.geidea.paymentsdk.model.exception.SdkException
import net.geidea.paymentsdk.model.order.Order


internal class PaymentActivity : AppCompatActivity() {

    companion object {
        /**
         * Input: [net.geidea.paymentsdk.flow.pay.PaymentData] parcelable (mandatory)
         */
        const val EXTRA_PAYMENT_DATA =
            "net.geidea.paymentsdk.flow.pay.PaymentActivity.EXTRA_PAYMENT_DATA"

        /**
         * Output: [net.geidea.paymentsdk.flow.GeideaResult] of type [Order]
         */
        const val EXTRA_RESULT = "net.geidea.paymentsdk.flow.pay.PaymentActivity.EXTRA_RESULT"

        /**
         * The only instance state data we keep is the id of a created order (if any).
         */
        private const val KEY_ORDER_ID = "orderId"
    }

    private var lifecycleLoggingCallbacks: Application.ActivityLifecycleCallbacks? = null

    private val binding by viewBinding(GdActivityPaymentBinding::inflate)

    private lateinit var viewModel: PaymentViewModel

    private lateinit var navController: NavController

    /**
     * Activity-scoped snackbar. Preserved across screen-to-screen navigation.
     */
    private var iconSnackbar: IconSnackbar? = null

    internal val coordinatorLayout: CoordinatorLayout get() = binding.fragmentContainer

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base.withSdkLocale())
    }

    private fun getOrCreateViewModel(paymentData: PaymentData): PaymentViewModel {
        val vmFactory = ViewModelFactory(
            application,
            paymentData = paymentData
        )
        return ViewModelProvider(viewModelStore, vmFactory)
            .get(PaymentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) = runSafely {
        if (BuildConfig.DEBUG) {
            lifecycleLoggingCallbacks =
                ActivityLifecycleLoggingCallbacks(PaymentActivity::class.java)
            application.registerActivityLifecycleCallbacks(lifecycleLoggingCallbacks)
        }

        // Intentionally prevent restoring the fragment backstack and start the flow all over.
        // Saving and restoring the payment flow is not supported.
        super.onCreate(null)

        if (!BuildConfig.DEBUG) {
            window.setFlags(FLAG_SECURE, FLAG_SECURE)
        }

        viewModel = getOrCreateViewModel(readPaymentDataOrThrow(intent))

        val paymentData = viewModel.initialPaymentData

        if (savedInstanceState == null) {
            val defaultResult = viewModel.makeDefaultCancelledResult(orderId = null)
            viewModel.setResult(defaultResult)
            setResult(defaultResult)
        } else {
            viewModel.orderId = savedInstanceState.getString(KEY_ORDER_ID)
        }

        val themeId: Int? = paymentData.bundle?.getInt(GeideaContract.PARAM_THEME)
        if (themeId != null) {
            setTheme(themeId)
            val dynamicColorsOptions = DynamicColorsOptions.Builder()
                .setThemeOverlay(themeId)
                .build()
            DynamicColors.applyToActivityIfAvailable(this, dynamicColorsOptions)
        } else {
            // Set the default theme programmatically due to font not applied correctly when
            // the activity had already a theme set in manifest
            setTheme(R.style.Gd_Theme_DayNight_NoActionBar)
        }

        if (paymentData.paymentMethod != null) {
            logi("PaymentActivity: paying with merchant-provided payment data")
        } else {
            logi("PaymentActivity: paying with payment data collected by SDK")
        }

        setContentView(binding.root)

        val merchantConfigResult = viewModel.preloadMerchantConfiguration()
        if (merchantConfigResult is GeideaResult.Success) {

            // Dynamically set the start destination and its args
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            navController = navHostFragment.navController

            // Intentionally prevent restoring navController and start the flow all over.
            // Saving and restoring the payment flow is not supported.
            navController.setGraph(R.navigation.gd_navigation)

            viewModel.navigationLiveEvent.observeEvent(this, ::handleNavigation)
            viewModel.resultLiveData.observe(this, ::setResult)

            viewModel.snackbarLiveEvent.observeEvent(this) { snack ->
                iconSnackbar = snackbar(binding.fragmentContainer, snack)
            }
            viewModel.snackbarDismissLiveEvent.observeEvent(this) {
                iconSnackbar?.dismiss()
                iconSnackbar = null
            }

            viewModel.processingLiveData.observe(this) { processing ->
                binding.progressView.isVisible = processing
            }

            val merchantConfiguration = merchantConfigResult.data
            if (merchantConfiguration.currencies != null
                && !merchantConfiguration.currencies.contains(paymentData.currency)
            ) {
                val errorResult = GeideaResult.NetworkError(
                    responseCode = "005",
                    responseMessage = "Invalid currency"
                )
                setResult(errorResult)
                finish()
            }
        } else {
            setResult(merchantConfigResult)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (viewModel.shouldSaveOrderId()) {
            outState.putString(KEY_ORDER_ID, viewModel.orderId)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (lifecycleLoggingCallbacks != null) {
            application.unregisterActivityLifecycleCallbacks(lifecycleLoggingCallbacks)
        }
    }

    internal fun handleNavigation(navCommand: NavigationCommand) {
        when (navCommand) {
            is NavigationCommand.ToDirection -> {
                iconSnackbar?.dismiss()
                navController.navigate(
                    directions = navCommand.directions,
                    navOptions = navCommand.navOptions
                )
            }
            is NavigationCommand.Back -> {
                if (!navController.popBackStack()) {
                    finish()
                }
            }
            is NavigationCommand.BackTo -> {
                if (isDestinationInBackStack(navCommand.destinationId)) {
                    if (!navController.popBackStack(
                            destinationId = navCommand.destinationId,
                            inclusive = navCommand.inclusive
                        )
                    ) {
                        finish()
                    }
                }
            }
            is NavigationCommand.BackToWithResult<*> -> {
                if (isDestinationInBackStack(navCommand.destinationId)) {
                    navController.getBackStackEntry(navCommand.destinationId)
                        .savedStateHandle
                        .set(navCommand.key, navCommand.value)
                    if (!navController.popBackStack(
                            destinationId = navCommand.destinationId,
                            inclusive = false
                        )
                    ) {
                        finish()
                    }
                }
            }
            is NavigationCommand.Cancel -> viewModel.onCancel()
            is NavigationCommand.Finish -> finish()
        }
    }

    private fun isDestinationInBackStack(@IdRes destinationId: Int): Boolean {
        val backStackEntry = try {
            navController.getBackStackEntry(destinationId)
        } catch (e: IllegalArgumentException) {
            null
        }
        return backStackEntry != null && navController.currentDestination?.id != backStackEntry.destination.id
    }

    private inline fun <reified T : Parcelable> setResult(result: GeideaResult<T>) {
        val resultCode = when (result) {
            is GeideaResult.Cancelled -> RESULT_CANCELED
            else -> RESULT_OK
        }
        setResult(resultCode, Intent().apply {
            putExtra(EXTRA_RESULT, result)
            R::class.java.classLoader?.let(::setExtrasClassLoader)
        })
    }

    internal fun runSafely(block: () -> Unit) {
        try {
            block()
        } catch (sdkEx: SdkException) {
            loge(sdkEx.stackTraceToString())
            val result = GeideaResult.SdkError(sdkEx.errorCode, sdkEx.message)
            setResult(result)
            finish()
        } catch (t: Throwable) {
            loge(t.stackTraceToString())
            val result = GeideaResult.SdkError(t)
            if (this::viewModel.isInitialized) {
                viewModel.setResult(result)
                viewModel.navigateToReceiptOrFinish()
            } else {
                setResult(result)
                finish()
            }
        }
    }

    private class ViewModelFactory(
        private val application: Application,
        private val paymentData: PaymentData
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentViewModel(
                merchantsService = SdkComponent.merchantsService,
                cancellationService = SdkComponent.cancellationService,
                paymentMethodsFilterProvider = {    // Provider because config is not yet loaded
                    PaymentMethodsFilter(
                        merchantConfiguration = SdkComponent.merchantsService.cachedMerchantConfiguration,
                        paymentData = paymentData
                    )
                },
                nativeTextFormatter = SdkComponent.formatter,
                initialPaymentData = paymentData,
            ) as T
        }
    }
}