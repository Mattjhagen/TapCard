package com.tapcard.app.domain.model

data class Profile(
    val id: String = "",
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
