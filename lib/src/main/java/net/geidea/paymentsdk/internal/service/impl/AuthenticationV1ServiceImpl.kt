package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.AuthenticationV1Service
import net.geidea.paymentsdk.model.auth.v1.AuthenticationRequest
import net.geidea.paymentsdk.model.auth.v1.AuthenticationResponse

internal class AuthenticationV1ServiceImpl(private val client: HttpsClient) : AuthenticationV1Service {

    override suspend fun postAuthenticate(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        return client.post<AuthenticationRequest, AuthenticationResponse>(
                path = "/pgw/api/v1/direct/authenticate",
                body = authenticationRequest,
        ).unwrapOrThrow()
    }
}