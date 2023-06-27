package net.geidea.paymentsdk.internal.ui.fragment.base

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.internal.client.HttpException
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.ReturnUrlParams
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.plainText
import net.geidea.paymentsdk.internal.util.templateText
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.error.errorMessage
import net.geidea.paymentsdk.model.error.getReason

/**
 * Snackbar message model.
 */
internal data class Snack(
    val title: NativeText? = null,
    val message: NativeText? = null,
    val reference: NativeText? = null,
    @StringRes val messageResId: Int = 0,
    @DrawableRes val iconResId: Int = R.drawable.gd_ic_snackbar_info,
    @ColorRes val outlineColorResId: Int = R.color.gd_snackbar_info,
    val duration: Int = -1,      // =Snackbar.LENGTH_SHORT
)

internal fun successSnack(
    title: NativeText? = null,
    message: NativeText? = null,
    @DrawableRes iconResId: Int = R.drawable.gd_ic_snackbar_success,
    @ColorRes outlineColorResId: Int = R.color.gd_snackbar_success,
    duration: Int = -1      // =Snackbar.LENGTH_SHORT
) = Snack(
    title = title,
    message = message,
    iconResId = iconResId,
    outlineColorResId = outlineColorResId,
    duration = duration
)

internal fun infoSnack(
    title: NativeText? = null,
    message: NativeText? = null,
    @DrawableRes iconResId: Int = R.drawable.gd_ic_snackbar_info,
    @ColorRes outlineColorResId: Int = R.color.gd_snackbar_info,
    duration: Int = 0,      // =Snackbar.LENGTH_LONG
) = Snack(
    title = title,
    message = message,
    iconResId = iconResId,
    outlineColorResId = outlineColorResId,
    duration = duration
)

internal fun warningSnack(
    title: NativeText? = null,
    message: NativeText? = null,
    @DrawableRes iconResId: Int = R.drawable.gd_ic_snackbar_warning,
    @ColorRes outlineColorResId: Int = R.color.gd_snackbar_warning,
    duration: Int = 0,      // =Snackbar.LENGTH_LONG
) = Snack(
    title = title,
    message = message,
    iconResId = iconResId,
    outlineColorResId = outlineColorResId,
    duration = duration
)

internal fun errorSnack(
    title: NativeText? = null,
    message: NativeText? = null,
    reference: NativeText? = null,
    @DrawableRes iconResId: Int = R.drawable.gd_ic_snackbar_error,
    @ColorRes outlineColorResId: Int = R.color.gd_snackbar_error,
    duration: Int = Snackbar.LENGTH_INDEFINITE,
) = Snack(
    title = title,
    message = message,
    reference = reference,
    iconResId = iconResId,
    outlineColorResId = outlineColorResId,
    duration = duration
)

internal fun errorSnack(t: Throwable): Snack {
    return if (t is HttpException) {
        errorSnack(
            title = getFullErrorCode(t),
            message = t.detailedResponseMessage?.let(NativeText::Plain)
                ?: t.errors?.values?.firstOrNull()?.firstOrNull()?.let(NativeText::Plain)
                ?: NativeText.Resource(R.string.gd_err_msg_unknown_error),
            reference = getReferenceText(),
        )
    } else {
        errorSnack(
            message = NativeText.Resource(R.string.gd_err_msg_unknown_error),
            reference = getReferenceText(),
        )
    }
}

internal fun errorSnack(error: GeideaResult.Error): Snack {
    return errorSnack(
        title = getFullErrorCode(error),
        message = getReason(error),
        reference = getReferenceText(),
    )
}

internal fun errorSnack(failResponse: GeideaResponse) = errorSnack(
    title = getFullErrorCode(failResponse),
    message = errorMessage(failResponse),
    reference = getReferenceText(),
)

internal fun errorSnack(urlParams: ReturnUrlParams) =
    errorSnack(
        title = urlParams.code?.let(NativeText::Plain),
        message = urlParams.msg?.let(NativeText::Plain),
        reference = getReferenceText(),
    )

// Full error code

// Full error code has format like: <responseCode>.<detailedResponseCode> or just <responseCode>
// if no detailedResponseCode is available. E.g. "300.001" or just "300"

private fun getFullErrorCode(httpException: HttpException): NativeText? =
    getFullErrorCode(httpException.responseCode, httpException.detailedResponseCode)

internal fun getFullErrorCode(errorResult: GeideaResult.Error): NativeText? {
    return when (errorResult) {
        is GeideaResult.NetworkError -> {
            getFullErrorCode(errorResult.responseCode, errorResult.detailedResponseCode)
        }
        is GeideaResult.SdkError -> {
            getFullErrorCode(errorResult.errorCode, detailedResponseCode = null)
        }
        else -> null
    }
}

private fun getFullErrorCode(geideaResponse: GeideaResponse): NativeText? =
    getFullErrorCode(geideaResponse.responseCode, geideaResponse.detailedResponseCode)

private fun getFullErrorCode(responseCode: String?, detailedResponseCode: String?): NativeText? {
    return when {
        responseCode != null && detailedResponseCode != null -> {
            plainText("$responseCode.$detailedResponseCode")
        }
        responseCode != null && detailedResponseCode == null -> {
            plainText("$responseCode")
        }
        responseCode == null && detailedResponseCode != null -> {
            // Should never reach here, but still...
            plainText("?.$detailedResponseCode")
        }
        else -> null
    }
}

/**
 * As Reference ID we use the X-Correlation-Id header value. It is helpful for tracking issues in
 * the server logs.
 */
internal fun getReferenceText(): NativeText? {
    return SdkComponent.httpsClient.correlationId
        ?.let { correlationId -> templateText(R.string.gd_reference_id_s, correlationId) }
}

/**
 * A snack shown normally just before a UI action (e.g. button click) that immediately trigger a
 * network call. This way a connection exception is avoided and user is given a chance to retry.
 */
internal val noInternetSnack =
    errorSnack(
        message = NativeText.Resource(R.string.gd_err_msg_no_internet_connection),
        duration = Snackbar.LENGTH_LONG
    )