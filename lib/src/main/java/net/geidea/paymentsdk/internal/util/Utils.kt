package net.geidea.paymentsdk.internal.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.BuildConfig
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.GeideaResultCallback
import net.geidea.paymentsdk.flow.responseAsResult
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.card.auth.DeviceInfo
import net.geidea.paymentsdk.internal.ui.widget.AspectRatioImageView
import net.geidea.paymentsdk.internal.util.LocaleUtils.localeLanguage
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.exception.SdkException
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal fun String.decodeFromBase64(flags: Int = Base64.DEFAULT): String {
    return Base64.decode(this, flags).toString(charset("UTF-8"))
}

internal fun String.encodeToBase64(flags: Int = Base64.DEFAULT): String {
    return Base64.encodeToString(this.toByteArray(charset("UTF-8")), flags)
}

internal fun parseQueryParams(url: String): List<Pair<String, String>> {
    // Split into 2 parts - path and query
    val urlParts = url.split("?")
    return if (urlParts.size == 2 && urlParts[1].isNotEmpty()) {
        val query = urlParts[1]
        // Split by pairs of param and value
        query.split("&").mapNotNull { paramAndValue ->
            val chunks: List<String> = paramAndValue.split('=')
            when (chunks.size) {
                2 -> Pair(chunks[0], chunks[1])
                1 -> Pair(chunks[0], "")
                else -> null
            }
        }.filter { it.first.isNotEmpty() }
    } else {
        emptyList()
    }
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <V : View> Activity.viewById(@IdRes id: Int): V {
    return findViewById(id) ?: throw IllegalArgumentException("Failed to find view with id $id")
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <V : View> View.viewById(@IdRes id: Int): V {
    return findViewById(id) ?: throw IllegalArgumentException("Failed to find view with id $id")
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <V : View> Activity.viewWithTag(tag: String): V {
    return window.decorView.findViewWithTag(tag)
            ?: throw IllegalArgumentException("Failed to find view with tag '$tag'")
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun <V : View> View.viewWithTag(tag: String): V {
    return findViewWithTag(tag)
            ?: throw IllegalArgumentException("Failed to find view with tag '$tag'")
}

@Suppress("NOTHING_TO_INLINE")
@OptIn(ExperimentalContracts::class)
internal inline fun <T : Any> sdkCheckNotNull(value: T?, errorCode: String): T {
    contract {
        returns() implies (value != null)
    }

    if (value == null) {
        throw SdkException(errorCode = errorCode)
    } else {
        return value
    }
}

internal inline val EditText.textOrNull: String?
    get() = this.text?.toString()?.takeIf(String::isNotBlank)

internal fun TextView.toBigDecimal(): BigDecimal = text?.toString()?.toBigDecimalOrNull().orZero()

internal inline fun BigDecimal?.orZero(): BigDecimal = or(BigDecimal.ZERO)
internal inline fun BigDecimal?.or(other: Int): BigDecimal = or(BigDecimal(other))
internal inline fun BigDecimal?.or(other: BigDecimal): BigDecimal = this ?: other

internal inline infix fun BigDecimal.eq(other: BigDecimal): Boolean = this.compareTo(other) == 0
internal inline infix fun BigDecimal.eq(other: Int): Boolean = this.compareTo(other.toBigDecimal()) == 0
internal inline infix fun BigDecimal.notEq(other: BigDecimal): Boolean = !(this eq other)
internal inline infix fun BigDecimal.notEq(other: Int): Boolean = !(this eq other)

internal inline infix fun BigDecimal.greaterThan(other: Int): Boolean = this > BigDecimal(other)
internal inline infix fun BigDecimal.greaterOrEqualThan(other: Int): Boolean = this > BigDecimal(other)

internal inline fun min(a: BigDecimal, b: BigDecimal): BigDecimal = a.min(b)
internal inline fun max(a: BigDecimal, b: BigDecimal): BigDecimal = a.max(b)

internal fun currencyFormat(currency: String, currencyFirst: Boolean = false): NumberFormat {
    // For most screens Geidea prefer non-localized currency format like "3,123.45 USD" but there
    // are exceptions (e.g. the total amount in Receipt screen)
    val currencyPattern = currency.uppercase(Locale.US)
    val pattern = when (currencyFirst) {
        true -> "$currencyPattern $amountPattern"
        false -> "$amountPattern $currencyPattern"
    }

    return DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.US))
}

private const val amountPattern = "#,###.##"

private val amountFormat: NumberFormat = DecimalFormat(amountPattern, DecimalFormatSymbols.getInstance(Locale.US))

internal fun formatAmount(amount: BigDecimal): CharSequence {
    return amountFormat.format(amount)
}

internal fun formatAmount(amount: BigDecimal, currency: String): CharSequence {
    return if (currency.isNotEmpty()) {
        currencyFormat(currency).format(amount)
    } else {
        "$amount"
    }
}

internal fun makeAmountText(amount: BigDecimal?, currency: String): NativeText {
    return if (amount != null && amount > BigDecimal.ZERO) {
        val text = currencyFormat(currency).format(amount)
        NativeText.Plain(text)
    } else {
        NativeText.Resource(R.string.gd_dash)
    }
}

internal fun capitalizeFirstCharacter(text: String): String =
    text.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

internal fun BigDecimal.has2orLessFractionalDigits(): Boolean {
    val intValue: BigInteger = toBigInteger()
    return this.subtract(BigDecimal(intValue)).toPlainString().length <= 4
}

internal fun hideKeyboard(activity: Activity) {
    val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val iBinder = activity.currentFocus?.windowToken
    if (iBinder != null) {
        inputMethodManager.hideSoftInputFromWindow(iBinder, 0)
    }
}

internal fun hideKeyboard(view: View) {
    val iBinder = view.windowToken
    if (iBinder != null) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(iBinder, 0)
    }
}

internal val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

internal val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

internal val Double.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

internal fun Fragment.confirmCancellationDialog(
        title: CharSequence = getString(R.string.gd_dlg_msg_cancel_confirm),
        onNegative: DialogInterface.OnClickListener? = null,
        onPositive: DialogInterface.OnClickListener,
) {
    requireActivity().confirmCancellationDialog(title = title, onPositive = onPositive, onNegative = onNegative)
}

internal fun Activity.confirmCancellationDialog(
        title: CharSequence = getString(R.string.gd_dlg_msg_cancel_confirm),
        onPositive: DialogInterface.OnClickListener,
        onNegative: DialogInterface.OnClickListener? = null
) {
    MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setPositiveButton(R.string.gd_yes, onPositive::onClick)
            .setNegativeButton(R.string.gd_no) { dialog, which -> onNegative?.onClick(dialog, which) }
            .show()
}

internal fun String.toDate(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.US)
    parser.timeZone = timeZone
    return parser.parse(this)
}

internal fun Date.formatWith(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.US)
    formatter.timeZone = timeZone
    return formatter.format(this)
}

internal fun currentMonthShort(): NativeText = plainText(android.text.format.DateFormat.format("MMMM", Date()))

internal fun currentYear4Digit(): NativeText = plainText(android.text.format.DateFormat.format("yyyy", Date()))

internal fun monthShort(calendar: Calendar): NativeText = plainText(android.text.format.DateFormat.format("MMMM", calendar))

internal fun year4Digit(calendar: Calendar): NativeText = plainText(android.text.format.DateFormat.format("yyyy", calendar))

internal inline fun <reified T : View> Fragment.findFirstChildRecursive(): T? {
    return requireView().findFirstChildRecursive()
}

internal inline fun <reified T : View> View.findFirstChildRecursive(): T? {
    return (this as? ViewGroup)?.children?.firstNotNullOfOrNull { it.findFirstChildRecursive(T::class.java) }
}

internal fun <T : View> View.findFirstChildRecursive(clazz: Class<T>): T? {
    if (this::class.java == clazz) {
        @Suppress("UNCHECKED_CAST")
        return this as T
    } else if (this is ViewGroup) {
        (0 until childCount).forEach { i ->
            getChildAt(i).findFirstChildRecursive(clazz)?.let { return it }
        }
    }
    return null
}

internal fun ViewGroup.forAllDescendants(block: (v: View) -> Unit) {
    block(this)
    for (cx in 0 until childCount) {
        val child = getChildAt(cx)
        if (child is ViewGroup)
            child.forAllDescendants(block)
        else
            block(child)
    }
}

internal fun <T> Fragment.setNavigationResult(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
}

internal fun <T> Fragment.setNavigationResult(@IdRes destination: Int, key: String, value: T) {
    findNavController().getBackStackEntry(destination).savedStateHandle.set(key, value)
}

internal fun <T> AppCompatActivity.setNavigationResult(@IdRes destination: Int, key: String, value: T) {
    findNavController(this, android.R.id.content)
        .getBackStackEntry(destination)
        .savedStateHandle
        .set(key, value)
}

internal fun <T> Fragment.getNavigationResult(@IdRes id: Int, key: String, onResult: (result: T) -> Unit) {
    val navBackStackEntry = findNavController().getBackStackEntry(id)

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains(key)
        ) {
            val result = navBackStackEntry.savedStateHandle.get<T>(key)
            result?.let(onResult)
            navBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)

    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}

internal fun decodeImageBase64(resources: Resources, imageBase64: String): BitmapDrawable {
    val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
    val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    return BitmapDrawable(resources, decodedBitmap)
}

internal fun AspectRatioImageView.setImageWithAspectRatio(@DrawableRes logo: Int) {
    if (logo != 0) {
        val logoDrawable = AppCompatResources.getDrawable(this.context, logo)!!
        val logoWidth = logoDrawable.intrinsicWidth.toFloat()
        val logoHeight = logoDrawable.intrinsicHeight.toFloat()
        setAspectRatioEnabled(true)
        setAspectRatio(logoWidth / logoHeight)
    }
    setImageResource(logo)
}

internal fun browserIntent(uri: String) = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

internal fun setupIndeterminateProgressOn(textInputLayout: TextInputLayout) {
    val context = textInputLayout.context
    val progressIndicatorSpec = CircularProgressIndicatorSpec(context, null).apply {
        indicatorInset = 16.dp
        indicatorSize = 24.dp
        trackThickness = 2.dp
    }

    val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(
        context, progressIndicatorSpec)

    with (textInputLayout) {
        isEndIconVisible = true
        endIconMode = TextInputLayout.END_ICON_CUSTOM
        endIconDrawable = progressIndicatorDrawable
    }
}

internal fun <T : GeideaResponse, R : Parcelable> CoroutineScope.launchWithCallback(
    resultCallback: GeideaResultCallback<R>,
    outputTransform: (T) -> R,
    block: suspend () -> T)
{
    launch(SdkComponent.dispatchersProvider.main) {
        resultCallback.onResult(responseAsResult(outputTransform, block))
    }
}

/**
 * Debug log on release build which is not stripped out by the R8 assumeNoSideEffects rule that removes
 * all logging code. Use sparingly and avoid logging sensitive data.
 */
internal fun releaseLogd(logMessage: String) {
    if (!BuildConfig.DEBUG) {
        Log.d(Logger.LOG_TAG, logMessage)
    }
}

/**
 * Verbose log on release build which is not stripped out by the R8 assumeNoSideEffects rule that removes
 * all logging code. Use sparingly and avoid logging sensitive data.
 */
internal fun releaseLogv(logMessage: String) {
    if (!BuildConfig.DEBUG) {
        Log.v(Logger.LOG_TAG, logMessage)
    }
}

internal fun debugAndReleaseLogd(logMessage: String) {
    Logger.logd(logMessage)
    releaseLogd(logMessage)
}

internal fun debugAndReleaseLogv(logMessage: String) {
    Logger.logv(logMessage)
    releaseLogv(logMessage)
}

internal fun WebView.retrieveDeviceInfo() : DeviceInfo {
    return DeviceInfo(
        browser = settings.userAgentString ?: "Android",
        colorDepth = 32,     // After API 17 the display pixel format is always RGBA_8888
        language = context.localeLanguage,
        screenWidth = getScreenWidth(),
        screenHeight = getScreenHeight(),
        javascriptEnabled = settings.javaScriptEnabled,
        javaEnabled = true,
        timezoneOffset = getTimezoneOffsetFromUtc(),
    )
}

internal fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

internal fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

/**
 * Time difference between UTC time and the device local time, in minutes.
 */
internal fun getTimezoneOffsetFromUtc(): Int {
    val tz = TimeZone.getDefault()
    val now = Date()
    return tz.getOffset(now.time) / 1000 / 60
}
internal const val ENCRYPTION_ALGORITHM = "HmacSHA256"
internal fun generateSignature(
    publicKey: String,
    orderAmount: BigDecimal,
    orderCurrency: String,
    merchantRefId: String?,
    apiPass: String,
    timestamp: String?
): String {
    val amountStr = String.format("%.2f", orderAmount)
    val data = "$publicKey$amountStr$orderCurrency$merchantRefId$timestamp"
    val hmacSha256 = Mac.getInstance(ENCRYPTION_ALGORITHM)
    val secretKeySpec = SecretKeySpec(apiPass.toByteArray(), ENCRYPTION_ALGORITHM)
    hmacSha256.init(secretKeySpec)
    val hash = hmacSha256.doFinal(data.toByteArray())
    return Base64.encodeToString(hash, Base64.NO_WRAP)
}

internal fun getCurrentTimestamp(): String {
    val dateFormat = SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.getDefault())
    return dateFormat.format(Calendar.getInstance().time)
}