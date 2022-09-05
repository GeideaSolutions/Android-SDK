package net.geidea.paymentsdk.sampleapp.sample

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.sampleapp.showErrorMessage

abstract class BaseSampleActivity<B : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = createBinding(layoutInflater)
        with(binding) {
            setContentView(root)
            initialize()
        }
    }

    private fun initialize() = with(binding) {
        lifecycleScope.launch(Dispatchers.Main) {
            val result: GeideaResult<MerchantConfigurationResponse> = GatewayApi.getMerchantConfiguration()
            when (result) {
                is GeideaResult.Success<MerchantConfigurationResponse> -> {
                    setupUi(result.data)
                }
                else -> {
                    onConfigurationLoadingFailed()
                }
            }
        }
    }

    abstract fun createBinding(layoutInflater: LayoutInflater): B

    abstract fun setupUi(merchantConfig: MerchantConfigurationResponse)

    protected open fun onConfigurationLoadingFailed() {
        showErrorMessage("Failed to load merchant configuration") { _, _ -> finish() }
    }
}