package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.MerchantConfigurationResponse

internal interface MerchantsService {
    suspend fun getMerchantConfiguration(gatewayKey: String): MerchantConfigurationResponse

    val cachedMerchantConfiguration: MerchantConfigurationResponse

    fun clearCachedMerchantConfiguration()
}