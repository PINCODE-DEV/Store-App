package com.softanime.storeapp.data.repository

import academy.nouri.storeapp.data.models.login.BodyLogin
import com.softanime.storeapp.data.network.ApiServices
import javax.inject.Inject

class LogInRepo @Inject constructor(private val api: ApiServices) {
    suspend fun postLogin(body: BodyLogin) = api.postLogin(body)
}