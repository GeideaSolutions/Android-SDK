package net.geidea.paymentsdk.internal.ui.fragment.hpp

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings.LOAD_NO_CACHE
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentHppBinding
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.util.Logger.loge
import net.geidea.paymentsdk.internal.util.Logger.logv
import net.geidea.paymentsdk.internal.util.viewBinding


@GeideaSdkInternal
internal class HppFragment : BaseFragment<HppViewModel>() {
    private val binding by viewBinding(GdFragmentHppBinding::bind)

    override val viewModel: HppViewModel by viewModels {
        ViewModelFactory(paymentViewModel)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.gd_fragment_hpp, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {

            addBackListener(viewModel::onBackPressed)
            initWebView()

            viewModel.hppUrlLiveData.observe(viewLifecycleOwner) { url ->
                webView.loadUrl(url)
            }

            if (savedInstanceState != null) {
                webView.restoreState(savedInstanceState)
            }

            viewModel.sessionId.observe(viewLifecycleOwner) { sessionId ->
                viewModel.loadPaymentView(sessionId)
            }
            viewModel.start(paymentViewModel.initialPaymentData)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
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
                }

                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    viewModel.setProgressbarVisibility(newProgress < 100)
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return viewModel.onReturnUrl(url)
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    webRequest: WebResourceRequest?
                ): Boolean {
                    return viewModel.onReturnUrl(webRequest?.url.toString())
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
                }

                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    loge("HTTPS WebViewClient.onReceivedError(errorCode=$errorCode, description=$description, failingUrl=$failingUrl)")
                }
            }
        }
    }
}

private class ViewModelFactory(
    private val paymentViewModel: PaymentViewModel
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HppViewModel(paymentViewModel, SdkComponent.orderService) as T
    }
}