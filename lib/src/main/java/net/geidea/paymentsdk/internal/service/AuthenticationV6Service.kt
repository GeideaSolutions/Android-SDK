package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v6.AuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v6.InitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v6.InitiateAuthenticationResponse

internal interface AuthenticationV6Service {
    // Plain card data
    suspend fun postInitiateAuthentication(authenticationRequest: InitiateAuthenticationRequest): InitiateAuthenticationResponse
    suspend fun postAuthenticatePayer(authenticatePayerRequest: AuthenticatePayerRequest): AuthenticationResponse
}