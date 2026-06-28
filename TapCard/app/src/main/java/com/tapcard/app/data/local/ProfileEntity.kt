package com.tapcard.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tapcard.app.domain.model.Profile

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: String = "local_profile",
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

fun ProfileEntity.toDomainModel(): Profile {
    return Profile(
        id = this.id,
        fullName = this.fullName,
        jobTitle = this.jobTitle,
        company = this.company,
        phone = this.phone,
        email = this.email,
        website = this.website,
        username = this.username,
        themeColorHex = this.themeColorHex,
        isDarkTheme = this.isDarkTheme,
        isPublic = this.isPublic,
        isPendingSync = this.isPendingSync,
        profilePhotoLocalUri = this.profilePhotoLocalUri,
        companyLogoLocalUri = this.companyLogoLocalUri,
        profilePhotoUrl = this.profilePhotoUrl,
        companyLogoUrl = this.companyLogoUrl
    )
}

fun Profile.toEntity(): ProfileEntity {
    return ProfileEntity(
        id = this.id,
        fullName = this.fullName,
        jobTitle = this.jobTitle,
        company = this.company,
        phone = this.phone,
        email = this.email,
        website = this.website,
        username = this.username,
        themeColorHex = this.themeColorHex,
        isDarkTheme = this.isDarkTheme,
        isPublic = this.isPublic,
        isPendingSync = this.isPendingSync,
        profilePhotoLocalUri = this.profilePhotoLocalUri,
        companyLogoLocalUri = this.companyLogoLocalUri,
        profilePhotoUrl = this.profilePhotoUrl,
        companyLogoUrl = this.companyLogoUrl
    )
}
