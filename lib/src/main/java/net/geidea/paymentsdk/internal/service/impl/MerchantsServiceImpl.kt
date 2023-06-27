package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpException
import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.MerchantsService
import net.geidea.paymentsdk.internal.util.Logger.logi
import net.geidea.paymentsdk.model.MerchantConfigurationResponse

internal class MerchantsServiceImpl(private val client: HttpsClient) : MerchantsService {

    private var _cachedConfig: MerchantConfigurationResponse? = null

    override val cachedMerchantConfiguration: MerchantConfigurationResponse
        get() = _cachedConfig!!

    override suspend fun getMerchantConfiguration(gatewayKey: String): MerchantConfigurationResponse {
        if (_cachedConfig == null) {
            val config = client.get<MerchantConfigurationResponse>(path = "/pgw/api/v1/config/$gatewayKey")
                .unwrapOrThrow()
            if (config.isSuccess) {
                _cachedConfig = config
                logi("Merchant configuration loaded and cached!")
            } else {
                throw HttpException(
                    statusCode = 200,
                    responseCode = config.responseCode,
                    responseMessage = config.responseMessage,
                    detailedResponseCode = config.detailedResponseCode,
                    detailedResponseMessage = config.detailedResponseMessage,
                    language = config.language,
                )
            }
        }

        return _cachedConfig!!
    }

    override fun clearCachedMerchantConfiguration() {
        this._cachedConfig = null
    }
}