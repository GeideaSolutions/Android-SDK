package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v3.TokenAuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v3.TokenInitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.AuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationResponse

internal interface AuthenticationV3Service {
    // Plain card data
    suspend fun postInitiateAuthentication(authenticationRequest: InitiateAuthenticationRequest): InitiateAuthenticationResponse
    suspend fun postAuthenticatePayer(authenticatePayerRequest: AuthenticatePayerRequest): AuthenticationResponse

    // Token
    suspend fun postTokenInitiateAuthentication(authenticationRequest: TokenInitiateAuthenticationRequest): InitiateAuthenticationResponse
    suspend fun postTokenAuthenticatePayer(authenticatePayerRequest: TokenAuthenticatePayerRequest): AuthenticationResponse
}