package net.geidea.paymentsdk

/**
 * Describes a particular Geidea server environment.
 *
 * [Prod] should be used normally. The backend will re-route the requests to Prod or PreProd
 * depending on your Merchant configuration.
 */
sealed class ServerEnvironment(val apiBaseUrl: String) {
    object Dev : ServerEnvironment(apiBaseUrl = "https://api-dev.gd-azure-dev.net")
    object Test : ServerEnvironment(apiBaseUrl = "https://api-test.gd-azure-dev.net")
    object PreProd : ServerEnvironment(apiBaseUrl = "https://api.gd-pprod-infra.net")
    object Prod : ServerEnvironment(apiBaseUrl = "https://api.merchant.geidea.net")
}