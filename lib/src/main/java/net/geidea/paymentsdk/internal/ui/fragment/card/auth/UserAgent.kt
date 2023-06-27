package net.geidea.paymentsdk.internal.ui.fragment.card.auth

import net.geidea.paymentsdk.GeideaSdkInternal

/**
 * An owner of WebView.
 */
@GeideaSdkInternal
internal interface UserAgent {
    fun loadHtml(html: String)
    val deviceInfo: DeviceInfo
}