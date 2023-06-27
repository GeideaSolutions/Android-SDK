package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.AuthenticationV3Service
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v3.TokenAuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v3.TokenInitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.AuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationResponse

internal class AuthenticationV3ServiceImpl(private val client: HttpsClient) : AuthenticationV3Service {

    override suspend fun postInitiateAuthentication(authenticationRequest: InitiateAuthenticationRequest): InitiateAuthenticationResponse {
        return client.post<InitiateAuthenticationRequest, InitiateAuthenticationResponse>(
                path = "/pgw/api/v3/direct/authenticate/initiate",
                body = authenticationRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postAuthenticatePayer(authenticatePayerRequest: AuthenticatePayerRequest): AuthenticationResponse {
        return client.post<AuthenticatePayerRequest, AuthenticationResponse>(
                path = "/pgw/api/v3/direct/authenticate/payer",
                body = authenticatePayerRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postTokenInitiateAuthentication(authenticationRequest: TokenInitiateAuthenticationRequest): InitiateAuthenticationResponse {
        return client.post<TokenInitiateAuthenticationRequest, InitiateAuthenticationResponse>(
            path = "/pgw/api/v3/direct/authenticate/initiate/token",
            body = authenticationRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postTokenAuthenticatePayer(authenticatePayerRequest: TokenAuthenticatePayerRequest): AuthenticationResponse {
        return client.post<TokenAuthenticatePayerRequest, AuthenticationResponse>(
            path = "/pgw/api/v3/direct/authenticate/payer/token",
            body = authenticatePayerRequest,
        ).unwrapOrThrow()
    }
}