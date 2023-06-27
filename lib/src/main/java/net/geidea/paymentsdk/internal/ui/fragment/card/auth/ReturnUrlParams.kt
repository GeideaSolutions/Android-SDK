package net.geidea.paymentsdk.internal.ui.fragment.card.auth

internal data class ReturnUrlParams(
    val code: String?,
    val msg: String?
) {
    val isSuccess: Boolean get() = code.equals(CODE_SUCCESS, ignoreCase = true)

    companion object {
        internal const val RETURN_URL = "geidea://paymentsdk/return"
        private const val CODE_SUCCESS = "000"
    }
}
