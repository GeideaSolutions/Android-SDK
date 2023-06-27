package net.geidea.paymentsdk.internal.ui.fragment.card.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.webkit.*
import android.webkit.WebSettings.LOAD_NO_CACHE
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.progressindicator.LinearProgressIndicator
import net.geidea.paymentsdk.GdCardGraphArgs
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentCardAuthBinding
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.card.BaseCardPaymentViewModel
import net.geidea.paymentsdk.internal.ui.fragment.card.cardNavGraphViewModel
import net.geidea.paymentsdk.internal.ui.widget.setup
import net.geidea.paymentsdk.internal.util.Logger.loge
import net.geidea.paymentsdk.internal.util.Logger.logi
import net.geidea.paymentsdk.internal.util.Logger.logv
import net.geidea.paymentsdk.internal.util.confirmCancellationDialog
import net.geidea.paymentsdk.internal.util.dp
import net.geidea.paymentsdk.internal.util.retrieveDeviceInfo
import net.geidea.paymentsdk.internal.util.viewBinding


@GeideaSdkInternal
internal class CardAuthFragment : BaseFragment<CardAuthViewModel>() {

    private val args: CardAuthFragmentArgs by navArgs()

    private val binding by viewBinding(GdFragmentCardAuthBinding::bind)

    private val cardGraphArgs: GdCardGraphArgs by navArgs()
    private val cardPaymentViewModel: BaseCardPaymentViewModel by cardNavGraphViewModel(lazy { paymentViewModel }, lazy { cardGraphArgs })

    override val viewModel: CardAuthViewModel by viewModels {
        ViewModelFactory(cardPaymentViewModel)
    }

    private lateinit var webProgressBar: LinearProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.gd_fragment_card_auth, container,false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            appBarWithStepper.setup(args.step, underlayView = null)

            if (args.step != null) {
                appBarWithStepper.stepper.setOnBackClickListener { onCancelButtonClicked() }
                addBackListener { onCancelButtonClicked() }
            } else {
                appBarWithStepper.appBarLayout.isLiftOnScroll = false
                addBackListener(viewModel::onBackPressed)
                appBarWithStepper.stepper.setOnBackClickListener { viewModel.onBackPressed() }
            }

            webProgressBar = LinearProgressIndicator(requireContext(), null, com.google.android.material.R.attr.linearProgressIndicatorStyle).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                trackThickness = 4.dp
            }
            appBarWithStepper.ovalHeaderFrameLayout.addView(webProgressBar)

            initWebView()

            viewModel.htmlLiveData.observe(viewLifecycleOwner) { html ->
                // TODO check if html is already loaded to avoid reloading and loosing user input
                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", null)
            }

            if (savedInstanceState != null) {
                webView.restoreState(savedInstanceState)
            }

            viewModel.start(binding.webView.retrieveDeviceInfo())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        logi("CardAuthFragment.onSaveInstanceState")

        super.onSaveInstanceState(outState)

        binding.webView.saveState(outState)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() = with(binding) {
        with(webView) {
            settings.javaScriptEnabled = true
            settings.cacheMode = LOAD_NO_CACHE
            webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView, title: String?) {
                    /*if (webView.isVisible) {
                        toolbar.title = title
                    }*/
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    webProgressBar.isVisible = newProgress < 100
                    webProgressBar.progress = newProgress
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    logv("HTTPS WebViewClient.shouldOverrideUrlLoading(url='$url')")
                    return viewModel.onReturnUrl(url)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    logv("HTTPS WebViewClient.onPageStarted(url='$url')")
                }

                override fun onReceivedSslError(
                    view: WebView,
                    handler: SslErrorHandler?,
                    error: SslError
                ) {
                    loge("HTTPS WebViewClient.onReceivedSslError(error=$error)")
                }

                @SuppressLint("NewApi")
                override fun onReceivedError(
                    view: WebView,
                    request: WebResourceRequest,
                    error: WebResourceError?
                ) {
                    loge("HTTPS WebViewClient.onReceivedError(errorCode=${error?.errorCode}, description=${error?.description})")

                    /*if (request.isForMainFrame && error != null) {
                        view.loadUrl(DEFAULT_ERROR_PAGE_PATH)
                    }*/
                }

                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    loge("HTTPS WebViewClient.onReceivedError(errorCode=$errorCode, description=$description, failingUrl=$failingUrl)")

                    /*if (errorCode != ERROR_UNSUPPORTED_SCHEME && errorCode != ERROR_HOST_LOOKUP) {
                        view.loadUrl(DEFAULT_ERROR_PAGE_PATH)
                    }*/
                }
            }
        }
    }

    private fun onCancelButtonClicked() {
        confirmCancellationDialog(onPositive = { _, _ ->
            logi("Cancel confirmed")
            viewModel.onCancelConfirmed()
        })
    }
}

private class ViewModelFactory(
    private val cardPaymentViewModel: BaseCardPaymentViewModel,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardAuthViewModel(
            cardPaymentViewModel = cardPaymentViewModel,
        ) as T
    }
}