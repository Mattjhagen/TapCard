package com.tapcard.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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
    val isDarkTheme: Boolean = true
)
