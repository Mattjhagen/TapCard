package com.tapcard.app.domain.model

data class Profile(
    val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val profileName: String = "Personal",
    /** Immutable URL-safe slug derived from profileName on creation. e.g. "Real Estate" → "real-estate" */
    val profileSlug: String = "personal",
    val fullName: String = "",
    val jobTitle: String = "",
    val company: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val username: String = "",
    val themeColorHex: String = "#000000",
    val isDarkTheme: Boolean = true,
    val isPublic: Boolean = true,
    val isPendingSync: Boolean = false,
    val profilePhotoLocalUri: String? = null,
    val companyLogoLocalUri: String? = null,
    val profilePhotoUrl: String? = null,
    val companyLogoUrl: String? = null
)
