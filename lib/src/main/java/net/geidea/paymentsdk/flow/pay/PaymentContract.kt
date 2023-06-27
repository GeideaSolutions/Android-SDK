package net.geidea.paymentsdk.flow.pay

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import net.geidea.paymentsdk.flow.*
import net.geidea.paymentsdk.model.exception.SdkException
import net.geidea.paymentsdk.model.order.Order

/**
 * Activity result contract for Geidea payment flow.
 *
 * @see PaymentData
 * @see GeideaResult
 */
public class PaymentContract : GeideaContract<PaymentData, GeideaResult<Order>>() {

    override fun createIntent(context: Context, paymentData: PaymentData) =
            paymentData.toIntent(context)

    override fun parseResult(resultCode: Int, resultIntent: Intent?) : GeideaResult<Order> {
        return when (resultCode) {
            RESULT_OK,
            RESULT_CANCELED -> {
                if (resultIntent != null) {
                    resultIntent.setExtrasClassLoader(Order::class.java.classLoader)
                    if (resultIntent.hasExtra(PaymentActivity.EXTRA_RESULT)) {
                        val result: Any? = resultIntent.getParcelableExtra(PaymentActivity.EXTRA_RESULT)
                        when (result) {
                            is GeideaResult<*> -> {
                                when (resultCode) {
                                    RESULT_OK -> {
                                        result as GeideaResult<Order>
                                    }
                                    RESULT_CANCELED -> {
                                        if (result is GeideaResult.Cancelled) {
                                            result
                                        } else {
                                            GeideaResult.SdkError(errorCode = ERR_EXTRA_RESULT_UNEXPECTED_TYPE)
                                        }
                                    }
                                    else -> error("Should not reach here")
                                }
                            }
                            null -> {
                                GeideaResult.SdkError(errorCode = ERR_EXTRA_RESULT_NULL)
                            } else -> {
                                GeideaResult.SdkError(errorCode = ERR_EXTRA_RESULT_UNEXPECTED_TYPE)
                            }
                        }
                    } else {
                        GeideaResult.SdkError(errorCode = ERR_EXTRA_RESULT_MISSING)
                    }
                } else {
                    GeideaResult.SdkError(errorCode = ERR_INTENT_NULL)
                }
            }
            else -> {
                GeideaResult.SdkError(errorCode = ERR_INTENT_RESULT_CODE_UNEXPECTED)
            }
        }
    }
}

internal fun readPaymentDataOrThrow(intent: Intent): PaymentData {
    intent.setExtrasClassLoader(PaymentData::class.java.classLoader)
    return if (intent.hasExtra(PaymentActivity.EXTRA_PAYMENT_DATA)) {
        val extra: Any? = intent.getParcelableExtra(PaymentActivity.EXTRA_PAYMENT_DATA)
        if (extra != null) {
            if (extra is PaymentData) {
                extra
            } else {
                throw SdkException(ERR_EXTRA_PAYMENT_DATA_UNEXPECTED_TYPE)
            }
        } else {
            throw SdkException(ERR_EXTRA_PAYMENT_DATA_NULL)
        }
    } else {
        throw SdkException(ERR_EXTRA_PAYMENT_DATA_MISSING)
    }
}