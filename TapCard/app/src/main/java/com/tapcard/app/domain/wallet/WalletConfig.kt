package com.tapcard.app.domain.wallet

object WalletConfig {
    // Feature flag for Google Wallet integration
    // Defaulting to false until backend JWT generation and Issuer ID are configured
    const val isGoogleWalletEnabled = true
    
    // Feature flag for Apple Wallet integration
    const val isAppleWalletEnabled = false
}
