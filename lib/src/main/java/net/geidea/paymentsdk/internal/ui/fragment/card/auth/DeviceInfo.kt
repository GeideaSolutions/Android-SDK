package net.geidea.paymentsdk.internal.ui.fragment.card.auth

import net.geidea.paymentsdk.GeideaSdkInternal

@GeideaSdkInternal
internal data class DeviceInfo(
    val browser: String,
    val colorDepth: Int = 24,
    val language: String? = "en",
    val timezoneOffset: Int? = null,
    val screenWidth: Int? = null,
    val screenHeight: Int? = null,
    val javaEnabled: Boolean = true,
    val javascriptEnabled: Boolean = true,
    val providerDeviceId: String? = null,
    val acceptHeaders: String = "accept",
    val threeDSecureChallengeWindowSize: String = "FULLSCREEN"
)
