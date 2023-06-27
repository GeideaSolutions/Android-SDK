package net.geidea.paymentsdk.sampleapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.suspendCancellableCoroutine
import net.geidea.paymentsdk.model.common.GeideaJsonObject
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.model.order.OrderStatus
import net.geidea.paymentsdk.model.paymentintent.EInvoicePaymentIntent
import net.geidea.paymentsdk.model.transaction.TransactionStatus
import net.geidea.paymentsdk.sampleapp.sample.orders.OrderOperation
import net.geidea.paymentsdk.sampleapp.sample.paymentintents.PaymentIntentOperation
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar
import java.util.Calendar.JANUARY
import java.util.TimeZone
import kotlin.coroutines.Continuation
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

fun Context.showErrorMessage(
    message: String?,
    onPositive: DialogInterface.OnClickListener? = null
) {
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
                        val operationTexts =
                            operations.map(OrderOperation::displayText).toTypedArray()
                        setSingleChoiceItems(operationTexts, 0, null)
                        setPositiveButton(android.R.string.ok) { dialog, _ ->
                            val listView = (dialog as AlertDialog).listView
                            val selectedOp: String =
                                listView.getItemAtPosition(listView.checkedItemPosition) as String
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

fun Context.showEInvoice(
    eInvoice: EInvoicePaymentIntent,
    onUpdate: (paymentIntentId: String) -> Unit,
    onDelete: (paymentIntentId: String) -> Unit,
    onSend: (eInvoiceId: String) -> Unit,
) {
    val json = eInvoice.toJson(pretty = true)
    val operations = listOf(
        PaymentIntentOperation.UPDATE,
        PaymentIntentOperation.DELETE,
        PaymentIntentOperation.SEND
    )

    val jsonDialog = AlertDialog.Builder(this).apply {
        setTitle("${eInvoice.status}")
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

    jsonDialog.setOnShowListener {
        // Add click listeners after creation to prevent auto-dismiss. Instead we dismiss manually.
        jsonDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            AlertDialog.Builder(this@showEInvoice).apply {
                setTitle("e-Invoice operations")
                val operationTexts =
                    operations.map(PaymentIntentOperation::displayText).toTypedArray()
                setSingleChoiceItems(operationTexts, 0, null)
                setPositiveButton(android.R.string.ok) { operationsDialog, _ ->
                    val listView = (operationsDialog as AlertDialog).listView
                    val selectedOp: String =
                        listView.getItemAtPosition(listView.checkedItemPosition) as String
                    when (selectedOp) {
                        PaymentIntentOperation.UPDATE.displayText -> onUpdate(eInvoice.paymentIntentId)
                        PaymentIntentOperation.DELETE.displayText -> onDelete(eInvoice.paymentIntentId)
                        PaymentIntentOperation.SEND.displayText -> onSend(eInvoice.paymentIntentId)
                    }
                    jsonDialog.dismiss()
                    operationsDialog.dismiss()
                }
                // Simply dismiss and go back to order dialog
                setNegativeButton("Dismiss", null)
            }.show()
        }
    }
    jsonDialog.show()
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
    positiveButtonTitle: CharSequence? = null,
    readInput: View.() -> T,
): T? {
    return suspendCancellableCoroutine { continuation ->
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setView(customView)
            .setPositiveButton(positiveButtonTitle ?: "OK") { _, _ ->
                continuation.resumeSafely { customView.readInput() }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                continuation.resume(null)
            }
            .create()
        dialog.show()

        continuation.invokeOnCancellation { dialog.dismiss() }
    }
}

suspend fun Context.singleChoiceDialog(
    title: CharSequence?,
    choices: List<CharSequence>,
    positiveButtonTitle: CharSequence? = null
): Int {
    var choice = -1
    return suspendCancellableCoroutine { continuation ->
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setSingleChoiceItems(choices.toTypedArray(), -1) { _, position ->
                choice = position
            }
            .setPositiveButton(positiveButtonTitle ?: "OK") { _, _ ->
                continuation.resumeSafely { choice }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                continuation.resume(-1)
            }
            .create()
        dialog.show()

        continuation.invokeOnCancellation { dialog.dismiss() }
    }
}

private fun <T> Continuation<T>.resumeSafely(block: () -> T) {
    try {
        resume(block())
    } catch (t: Throwable) {
        resumeWithException(t)
    }
}

private fun getTodayCalendar(): Calendar {
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val calendar: Calendar = getClearedUtc()
    calendar.timeInMillis = today
    return calendar
}

fun validUntil(field: Int, amount: Int) = CalendarConstraints.Builder()
    .setValidator(CompositeDateValidator.allOf(listOf(
        DateValidatorPointForward.now(),
        DateValidatorPointBackward.before(
            getTodayCalendar()
                .apply { roll(field, amount) }
                .timeInMillis
        )
    )))
    .build()

fun getNextMonthUtc(): Long {
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val calendar: Calendar = getClearedUtc()
    calendar.timeInMillis = today
    calendar.roll(Calendar.MONTH, 1)
    if (calendar.get(Calendar.MONTH) == JANUARY) {
        calendar.roll(Calendar.YEAR, 1)
    }
    return calendar.timeInMillis
}

fun getClearedUtc(): Calendar =
    Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { clear() }

fun Context.copyToClipboard(text: CharSequence, label: CharSequence) {
    val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
    ClipData.newPlainText(label, text).let(clipboard::setPrimaryClip)
}

// Animation helpers

fun FloatingActionButton.rotate(rotate: Boolean): Boolean {
    animate()
        .setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        })
        .rotation(if (rotate) 135f else 0f)
    return rotate
}

fun View.initAppearAnimation() {
    visibility = View.GONE
    translationY = height.toFloat()
    alpha = 0f
}

fun View.appear() {
    visibility = View.VISIBLE
    alpha = 0f
    translationY = height.toFloat()
    animate()
        .setDuration(200)
        .translationY(0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        })
        .alpha(1f)
        .start()
}

fun View.disappear() {
    visibility = View.VISIBLE
    alpha = 1f
    translationY = 0f
    animate()
        .setDuration(200)
        .translationY(height.toFloat())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = View.GONE
                super.onAnimationEnd(animation)
            }
        })
        .alpha(0f)
        .start()
}

fun hideKeyboard(view: View) {
    val iBinder = view.windowToken
    if (iBinder != null) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(iBinder, 0)
    }
}

fun Bundle.toJSONString(): String? {
    val json = JSONObject()
    val keys: Set<String> = keySet()
    for (key in keys) {
        try {
            json.put(key, JSONObject.wrap(get(key)))
        } catch (e: JSONException) {
            return null;
        }
    }
    return json.toString()
}

fun String.toBundle(): Bundle {
    val jsonObject = JSONObject(this)
    val bundle = Bundle()
    val iterator: Iterator<*> = jsonObject.keys()
    while (iterator.hasNext()) {
        val key = iterator.next() as String
        val value: Int = jsonObject.getInt(key)
        bundle.putInt(key, value)
    }
    return bundle
}

fun SharedPreferences.getStringOrNull(key: String): String? {
    return getString(key, null)
}