package com.tapcard.app.domain.wallet

data class WalletJwtRequest(
    val userId: String,
    val profileData: Map<String, String>
)

data class WalletJwtResponse(
    val jwt: String?,
    val error: String?
)
