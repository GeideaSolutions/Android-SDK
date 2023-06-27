package net.geidea.paymentsdk.internal.serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.geidea.paymentsdk.model.common.GeideaJsonObject

internal inline fun <reified T : GeideaJsonObject> T.encodeToJson(pretty: Boolean = false): String {
    return Json { prettyPrint = pretty }.encodeToString<T>(this)
}

internal inline fun <reified T : GeideaJsonObject> decodeFromJson(json: String): T {
    return Json { ignoreUnknownKeys = true }.decodeFromString(json)
}