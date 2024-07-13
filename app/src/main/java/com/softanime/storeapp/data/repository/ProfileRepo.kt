package com.softanime.storeapp.data.repository

import com.softanime.storeapp.data.network.ApiServices
import javax.inject.Inject

class ProfileRepo @Inject constructor(private val api: ApiServices) {
    suspend fun getProfile() = api.getProfileData()
}