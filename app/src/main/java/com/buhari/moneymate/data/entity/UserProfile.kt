package com.buhari.moneymate.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "User",
    val profileImage: String? = null,
    val theme: String = "System",
    val language: String = "en"
)
