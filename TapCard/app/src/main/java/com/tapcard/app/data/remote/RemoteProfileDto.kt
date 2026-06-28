package com.tapcard.app.data.remote

import com.tapcard.app.domain.model.Profile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteProfileDto(
    @SerialName("id")
    val id: String,
    
    @SerialName("full_name")
    val fullName: String?,
    
    @SerialName("job_title")
    val jobTitle: String?,
    
    @SerialName("company")
    val company: String?,
    
    @SerialName("phone")
    val phone: String?,
    
    @SerialName("email")
    val email: String?,
    
    @SerialName("username")
    val username: String,
    
    @SerialName("website")
    val website: String?,
    
    @SerialName("theme_color_hex") val themeColorHex: String? = null,
    @SerialName("is_dark_theme") val isDarkTheme: Boolean = true,
    @SerialName("is_public") val isPublic: Boolean = true,
    @SerialName("profile_photo_url") val profilePhotoUrl: String? = null,
    @SerialName("company_logo_url") val companyLogoUrl: String? = null
)

fun Profile.toRemoteDto(): RemoteProfileDto {
    return RemoteProfileDto(
        id = this.id,
        fullName = this.fullName.ifEmpty { null },
        jobTitle = this.jobTitle.ifEmpty { null },
        company = this.company.ifEmpty { null },
        phone = this.phone.ifEmpty { null },
        email = this.email.ifEmpty { null },
        username = this.username,
        website = this.website.ifEmpty { null },
        themeColorHex = this.themeColorHex,
        isDarkTheme = this.isDarkTheme,
        isPublic = this.isPublic,
        profilePhotoUrl = this.profilePhotoUrl,
        companyLogoUrl = this.companyLogoUrl
    )
}
