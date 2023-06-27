package net.geidea.paymentsdk.internal.util

import net.geidea.paymentsdk.GeideaSdkInternal

@GeideaSdkInternal
internal interface NetworkConnectivity {
    val isConnected: Boolean
}