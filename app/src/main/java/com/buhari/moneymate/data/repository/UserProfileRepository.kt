package com.buhari.moneymate.data.repository

import com.buhari.moneymate.data.dao.UserProfileDao
import com.buhari.moneymate.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow

class UserProfileRepository(private val userProfileDao: UserProfileDao) {
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun updateProfile(userProfile: UserProfile) {
        userProfileDao.insertOrUpdateUserProfile(userProfile)
    }
}
