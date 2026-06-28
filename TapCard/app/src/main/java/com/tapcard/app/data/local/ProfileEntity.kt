package com.tapcard.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tapcard.app.domain.model.Profile

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val profileName: String = "Personal",
    /** Stored slug — immutable URL-safe identifier for this profile. */
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

/** Room migration 1 → 2: adds profileSlug column, backfills from profileName. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE profile ADD COLUMN profileSlug TEXT NOT NULL DEFAULT 'personal'")
        // Backfill: lowercase, spaces → hyphens
        db.execSQL("""
            UPDATE profile
            SET profileSlug = lower(replace(trim(profileName), ' ', '-'))
            WHERE profileSlug = 'personal' AND profileName != 'Personal'
        """.trimIndent())
    }
}

fun ProfileEntity.toDomainModel(): Profile {
    return Profile(
        id = this.id,
        userId = this.userId,
        profileName = this.profileName,
        profileSlug = this.profileSlug,
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
        userId = this.userId,
        profileName = this.profileName,
        profileSlug = this.profileSlug,
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
