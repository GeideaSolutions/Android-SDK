package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.auth.v6.CreateSessionRequest
import net.geidea.paymentsdk.model.auth.v6.CreateSessionResponse

internal interface SessionV2Service {

    suspend fun createSession(createSessionRequest: CreateSessionRequest): CreateSessionResponse
}