package net.geidea.paymentsdk.internal.service.impl

import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.TokenService
import net.geidea.paymentsdk.model.token.TokenResponse

internal class TokenServiceImpl(private val client: HttpsClient) : TokenService {

    override suspend fun getToken(tokenId: String): TokenResponse {
        return client.get<TokenResponse>(path = "/pgw/api/v1/direct/token/$tokenId").unwrapOrThrow()
    }
}