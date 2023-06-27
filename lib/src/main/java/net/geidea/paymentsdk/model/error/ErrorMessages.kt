package net.geidea.paymentsdk.model.error

import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.model.PaymentType
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.transaction.Transaction

internal fun errorMessage(networkError: GeideaResult.NetworkError): NativeText =
        networkError.detailedResponseMessage?.let(NativeText::Plain)
                ?: networkError.responseMessage?.let(NativeText::Plain)
                ?: networkError.errors?.values?.firstOrNull()?.firstOrNull()?.let(NativeText::Plain)
                ?: NativeText.Resource(R.string.gd_err_msg_unknown_error)

internal fun errorMessage(failResponse: GeideaResponse): NativeText =
        failResponse.detailedResponseMessage?.let(NativeText::Plain)
                ?: failResponse.responseMessage?.let(NativeText::Plain)
                ?: NativeText.Resource(R.string.gd_err_msg_unknown_error)

internal fun getReason(result: GeideaResult<*>?): NativeText {
    return when (result) {
        is GeideaResult.NetworkError -> errorMessage(result)
        is GeideaResult.SdkError -> result.errorMessage?.let(NativeText::Plain) ?: NativeText.Resource(R.string.gd_err_msg_sdk_error)
        is GeideaResult.InvalidInputError -> NativeText.Plain(result.errorMessage)
        is GeideaResult.Cancelled -> result.detailedResponseMessage?.let(NativeText::Plain)
                ?: result.responseMessage?.let(NativeText::Plain)
        else -> null
    } ?: NativeText.Resource(R.string.gd_err_msg_unknown_error)
}

internal fun getReason(transaction: Transaction): CharSequence {
    return when (transaction.paymentMethod.type) {
        PaymentType.CARD -> transaction.status
        PaymentType.QR -> transaction.meezaDetails?.responseDescription
        //PaymentType.BNPL -> transaction.bnplDetails?.responseDescription
        else -> null
    } ?: "Unknown error"
}