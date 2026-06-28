package com.tapcard.app.domain.wallet

interface WalletService {
    /**
     * Requests a signed JWT from the backend to save the pass to Google Wallet.
     * Note: Requires backend integration and Issuer ID to be configured.
     */
    suspend fun getGoogleWalletJwt(request: WalletJwtRequest): WalletJwtResponse
}
