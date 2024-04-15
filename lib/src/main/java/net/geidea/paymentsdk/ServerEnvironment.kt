package net.geidea.paymentsdk

/**
 * Describes a particular Geidea server environment.
 *
 * [Prod] should be used normally. The backend will re-route the requests to Prod or PreProd
 * depending on your Merchant configuration.
 */
sealed class ServerEnvironment(val title: String, val apiBaseUrl: String) {
    object EGY_PROD : ServerEnvironment(title= "EGY-PROD", apiBaseUrl = "https://api.merchant.geidea.net")
    object EGY_PREPROD : ServerEnvironment(title= "EGY-PREPROD",apiBaseUrl = "https://api-merchant.staging.geidea.net")

    object UAE_PROD : ServerEnvironment(title= "UAE-PROD",apiBaseUrl = "https://api.merchant.geidea.ae")
    object UAE_PREPROD : ServerEnvironment(title= "UAE-PREPROD",apiBaseUrl = "https://api-merchant.staging.geidea.ae")

    object KSA_PROD : ServerEnvironment(title= "KSA-PROD",apiBaseUrl = "https://api.ksamerchant.geidea.net")
    object KSA_PREPROD : ServerEnvironment(title= "KSA-PREPROD",apiBaseUrl = "https://api-ksamerchant.staging.geidea.net")


    companion object {
        fun environments(): List<ServerEnvironment> = listOf(EGY_PROD, EGY_PREPROD, UAE_PROD, UAE_PREPROD, KSA_PROD, KSA_PREPROD)
    }
}