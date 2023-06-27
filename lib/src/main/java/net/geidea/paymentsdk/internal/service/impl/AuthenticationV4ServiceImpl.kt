package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.AuthenticationV4Service
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse
import net.geidea.paymentsdk.model.auth.v4.AuthenticatePayerRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationRequest
import net.geidea.paymentsdk.model.auth.v4.InitiateAuthenticationResponse

internal class AuthenticationV4ServiceImpl(private val client: HttpsClient) :
    AuthenticationV4Service {

    override suspend fun postInitiateAuthentication(authenticationRequest: InitiateAuthenticationRequest): InitiateAuthenticationResponse {
        return client.post<InitiateAuthenticationRequest, InitiateAuthenticationResponse>(
                path = "/pgw/api/v4/direct/authenticate/initiate",
                body = authenticationRequest,
        ).unwrapOrThrow()
    }

    override suspend fun postAuthenticatePayer(authenticatePayerRequest: AuthenticatePayerRequest): AuthenticationResponse {
        return client.post<AuthenticatePayerRequest, AuthenticationResponse>(
                path = "/pgw/api/v4/direct/authenticate/payer",
                body = authenticatePayerRequest,
        ).unwrapOrThrow()
    }
}