package net.geidea.paymentsdk.sampleapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.suspendCancellableCoroutine
import net.geidea.paymentsdk.model.GeideaJsonObject
import net.geidea.paymentsdk.model.Order
import net.geidea.paymentsdk.model.OrderStatus
import net.geidea.paymentsdk.model.TransactionStatus
import net.geidea.paymentsdk.sampleapp.sample.orders.OrderOperation
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.showErrorResult(text: String) {
    AlertDialog.Builder(this)
            .setTitle("Error")
            .setView(monoSpaceTextContainer(text))
            .setPositiveButton(android.R.string.ok, null)
            .show()
}

fun Context.showErrorMessage(message: String?, onPositive: DialogInterface.OnClickListener? = null) {
    AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, onPositive)
            .show()
}

fun Context.monoSpaceTextContainer(text: String): View {
    return LayoutInflater.from(this)
            .inflate(R.layout.dialog_json, null).apply {
                findViewById<TextView>(R.id.textView)!!.text = text
            }
}

inline val EditText.textOrNull: String?
    get() = this.text?.toString()?.takeIf(String::isNotBlank)

fun <T> List<DropDownItem<T>>.findValueByText(text: String?): T? =
        first { item -> item.text == text }.value

fun Context.showOrder(
        order: Order,
        onCapture: (order: Order) -> Unit,
        onRefund: (order: Order) -> Unit,
        onCancel: (order: Order) -> Unit,
) {
    val json = order.toJson(pretty = true)
    val operations: List<OrderOperation> = getAllowedOrderOperations(order)

    val orderDialog = AlertDialog.Builder(this).apply {
        setTitle("${order.status} - ${order.detailedStatus}")
        setView(monoSpaceTextContainer(json))
        setPositiveButton(android.R.string.ok, null)
        if (operations.isNotEmpty()) {
            if (operations.size == 1) {
                setNeutralButton(operations[0].displayText, null)
            } else {
                setNeutralButton("Operations…", null)
            }
        }
    }.create()

    orderDialog.setOnShowListener {
        // Add click listeners after creation to prevent auto-dismiss. Instead we dismiss manually.
        when {
            operations.size > 1 -> {
                orderDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    AlertDialog.Builder(this@showOrder).apply {
                        setTitle("Order operation(s)")
                        val operationTexts = operations.map(OrderOperation::displayText).toTypedArray()
                        setSingleChoiceItems(operationTexts, 0, null)
                        setPositiveButton(android.R.string.ok) { dialog, _ ->
                            val listView = (dialog as AlertDialog).listView
                            val selectedOp: String = listView.getItemAtPosition(listView.checkedItemPosition) as String
                            when (selectedOp) {
                                OrderOperation.CAPTURE.displayText -> onCapture(order)
                                OrderOperation.REFUND.displayText -> onRefund(order)
                                OrderOperation.CANCEL.displayText -> onCancel(order)
                            }
                            orderDialog.dismiss()
                        }
                        // Simply dismiss and go back to order dialog
                        setNegativeButton("Dismiss", null)
                    }.show()
                }
            }
            operations.size == 1 -> {
                orderDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                    when (operations[0]) {
                        OrderOperation.CAPTURE -> onCapture(order)
                        OrderOperation.REFUND -> onRefund(order)
                        OrderOperation.CANCEL -> onCancel(order)
                    }
                    orderDialog.dismiss()
                }
            }
        }
    }
    orderDialog.show()
}

fun getAllowedOrderOperations(order: Order): List<OrderOperation> {
    return mutableListOf<OrderOperation>().apply {
        if (order.detailedStatus == OrderStatus.AUTHORIZED) {
            add(OrderOperation.CAPTURE)
        }
        if (order.detailedStatus in listOf(OrderStatus.PAID, OrderStatus.CAPTURED)) {
            add(OrderOperation.REFUND)
        }
        if (order.isCancellable) {
            add(OrderOperation.CANCEL)
        }
    }
}

val Order.isCancellable: Boolean get() = status == TransactionStatus.IN_PROGRESS

fun Context.showObjectAsJson(obj: GeideaJsonObject, block: AlertDialog.Builder.() -> Unit = {}) {
    val json = obj.toJson(pretty = true)
    val dialogBuilder = AlertDialog.Builder(this).apply {
        setView(monoSpaceTextContainer(json))
        setPositiveButton(android.R.string.ok, null)
        block()
    }
    dialogBuilder.show()
}

fun ViewBinding.snack(message: CharSequence, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(root, message, duration).show()
}

suspend fun Context.inputText(title: CharSequence?, hint: CharSequence?): String? {
    return suspendCancellableCoroutine { continuation ->
        val customView: View = LayoutInflater.from(this).inflate(R.layout.dialog_input_text, null)
        val textInputLayout: TextInputLayout = customView.findViewById(R.id.textInputLayout)
        val editText = customView.findViewById<EditText>(R.id.editText)

        textInputLayout.isHintEnabled = true
        textInputLayout.hint = hint

        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setView(customView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val text = editText.text?.toString() ?: ""
                    continuation.resume(text)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    continuation.resume(null)
                }
                .create()
        dialog.show()
        continuation.invokeOnCancellation { dialog.dismiss() }
    }
}

suspend fun <T> Context.customViewDialog(
        customView: View,
        title: CharSequence?,
        block: View.() -> T,
): T? {
    return suspendCancellableCoroutine { continuation ->
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setView(customView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    try {
                        continuation.resume(block(customView))
                    } catch (t: Throwable) {
                        continuation.resumeWithException(t)
                    }
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    continuation.resume(null)
                }
                .create()
        dialog.show()
        continuation.invokeOnCancellation { dialog.dismiss() }
    }
}

fun getNextMonthUtc(): Long {
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val calendar: Calendar = getClearedUtc()
    calendar.timeInMillis = today
    calendar.roll(Calendar.MONTH, 1)
    return calendar.timeInMillis
}

fun getClearedUtc(): Calendar =
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { clear() }

fun Context.copyToClipboard(text: CharSequence, label: CharSequence) {
    val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    ClipData.newPlainText(label, text).let(clipboard::setPrimaryClip)
}