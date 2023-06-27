package net.geidea.paymentsdk.internal.client

internal data class RequestConfig(
        val method: RequestMethod,
        val path: String,
        val headers: Map<String, String> = mapOf(),
        val query: Map<String, List<String>> = mapOf()
)