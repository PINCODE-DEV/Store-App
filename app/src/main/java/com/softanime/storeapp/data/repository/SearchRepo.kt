package com.softanime.storeapp.data.repository

import academy.nouri.storeapp.data.models.login.BodyLogin
import com.softanime.storeapp.data.network.ApiServices
import javax.inject.Inject

class SearchRepo @Inject constructor(private val api: ApiServices) {
    suspend fun getSearchList(queries: Map<String, String>) = api.getSearchList(queries)
}