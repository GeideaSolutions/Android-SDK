package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.auth.v1.AuthenticationRequest
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse

internal interface AuthenticationV1Service {
    suspend fun postAuthenticate(authenticationRequest: AuthenticationRequest): AuthenticationResponse
}