package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.SessionV2Service
import net.geidea.paymentsdk.model.auth.v6.CreateSessionRequest
import net.geidea.paymentsdk.model.auth.v6.CreateSessionResponse

internal class SessionV2ServiceImpl(private val client: HttpsClient) :
    SessionV2Service {

    override suspend fun createSession(createSessionRequest: CreateSessionRequest): CreateSessionResponse {
        return client.post<CreateSessionRequest, CreateSessionResponse>(
            path = "/payment-intent/api/v2/direct/session",
            body = createSessionRequest
        ).unwrapOrThrow()
    }
}