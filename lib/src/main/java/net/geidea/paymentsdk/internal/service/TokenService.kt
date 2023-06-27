package net.geidea.paymentsdk.internal.service

import net.geidea.paymentsdk.model.token.TokenResponse


internal interface TokenService {
    suspend fun getToken(tokenId: String): TokenResponse
    //suspend fun deleteToken(tokenId: String): TokenResponse
}
