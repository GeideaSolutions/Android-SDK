package net.geidea.paymentsdk

/**
 * Describes a particular Geidea server environment.
 *
 * [Prod] should be used normally. The backend will re-route the requests to Prod or PreProd
 * depending on your Merchant configuration.
 */
sealed class ServerEnvironment(val title: String, val apiBaseUrl: String, val hppUrl: String? = null) {
    object EGY_PROD : ServerEnvironment(title= "EGY-PROD", apiBaseUrl = "https://api.merchant.geidea.net", "https://www.merchant.geidea.net/hpp/checkout/?")
    object EGY_PREPROD : ServerEnvironment(title= "EGY-PREPROD",apiBaseUrl = "https://api-merchant.staging.geidea.net", "https://www.gd-pprod-infra.net/hpp/checkout/?")

    object UAE_PROD : ServerEnvironment(title= "UAE-PROD",apiBaseUrl = "https://api.merchant.geidea.ae", "https://payments.geidea.ae/hpp/checkout/?")
    object UAE_PREPROD : ServerEnvironment(title= "UAE-PREPROD",apiBaseUrl = "https://api-merchant.staging.geidea.ae", "https://www.staging.geidea.ae/hpp/checkout/?")

    object KSA_PROD : ServerEnvironment(title= "KSA-PROD",apiBaseUrl = "https://api.ksamerchant.geidea.net", "https://www.ksamerchant.geidea.net/hpp/checkout/?")
    object KSA_PREPROD : ServerEnvironment(title= "KSA-PREPROD",apiBaseUrl = "https://api-ksamerchant.staging.geidea.net", "https://www.gd-pprod-infra.net/hpp/checkout/?")


    companion object {
        fun environments(): List<ServerEnvironment> = listOf(EGY_PROD, EGY_PREPROD, UAE_PROD, UAE_PREPROD, KSA_PROD, KSA_PREPROD)
    }
}