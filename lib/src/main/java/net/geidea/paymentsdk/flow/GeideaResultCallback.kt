package net.geidea.paymentsdk.flow

import android.os.Parcelable

fun interface GeideaResultCallback<R : Parcelable> {
    fun onResult(result: GeideaResult<R>)
}