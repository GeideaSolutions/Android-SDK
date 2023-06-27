package net.geidea.paymentsdk.model.common

interface LocalizableRequest : GeideaJsonObject {
    var language: String?
}