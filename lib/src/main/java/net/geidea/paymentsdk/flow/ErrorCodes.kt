@file:JvmName("ErrorCodes")
package net.geidea.paymentsdk.flow

// Common errors
internal const val ERR_MERCHANT_KEY_OR_PASS_MISSING = "002"

// Payment flow bad input errors
internal const val ERR_EXTRA_PAYMENT_DATA_MISSING = "A100"
internal const val ERR_EXTRA_PAYMENT_DATA_NULL = "A101"
internal const val ERR_EXTRA_PAYMENT_DATA_UNEXPECTED_TYPE = "A102"

// Payment flow bad output errors
internal const val ERR_INTENT_RESULT_CODE_UNEXPECTED = "A201"
internal const val ERR_INTENT_NULL = "A202"
internal const val ERR_EXTRA_RESULT_MISSING = "A203"
internal const val ERR_EXTRA_RESULT_NULL = "A204"
internal const val ERR_EXTRA_RESULT_UNEXPECTED_TYPE = "A205"

// Unexpected server responses
internal const val ERR_UNEXPECTED_CLIENT_ERROR_BODY = "A400"
internal const val ERR_UNEXPECTED_SERVER_ERROR_BODY = "A401"
internal const val ERR_AUTH_V1_SUCCESS_BUT_NULL_THREEDSECUREID = "A402"
internal const val ERR_AUTH_V1_SUCCESS_BUT_NULL_HTMLBODYCONTENT = "A403"
internal const val ERR_PAY_V1_SUCCESS_BUT_NULL_ORDER = "A404"

// SDK assertion errors
internal const val ERR_AUTH_V1_WITH_PAYMENT_METHOD_NULL = "A500"
internal const val ERR_PAY_WITH_PAYMENT_METHOD_NULL = "A500"