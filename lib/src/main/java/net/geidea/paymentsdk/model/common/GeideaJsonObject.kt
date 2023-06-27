package net.geidea.paymentsdk.model.common

/**
 * Geidea serializable and de-serializable JSON object.
 */
interface GeideaJsonObject {

    /**
     * Serializes this object to a JSON string.
     *
     * @param pretty when true adds indent of 4 spaces and spacing after colons otherwise a
     * dense one-line JSON representation is returned. Default is false.
     */
    fun toJson(pretty: Boolean = false): String
}