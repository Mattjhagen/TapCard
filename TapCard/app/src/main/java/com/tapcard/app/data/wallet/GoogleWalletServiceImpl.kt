package com.tapcard.app.data.wallet

import com.tapcard.app.domain.wallet.WalletJwtRequest
import com.tapcard.app.domain.wallet.WalletJwtResponse
import com.tapcard.app.domain.wallet.WalletService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleWalletServiceImpl @Inject constructor() : WalletService {

    override suspend fun getGoogleWalletJwt(request: WalletJwtRequest): WalletJwtResponse {
        // TODO: Implement actual network call to our backend to generate the JWT.
        // We must not sign the JWT on the client for security reasons.
        // The backend requires the Google Wallet Issuer ID and credentials.
        return WalletJwtResponse(
            jwt = null,
            error = "Google Wallet is currently disabled. Missing backend configuration and Issuer ID."
        )
    }
}
