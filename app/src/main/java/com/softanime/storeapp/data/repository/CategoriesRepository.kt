package com.softanime.storeapp.data.repository

import com.softanime.storeapp.data.network.ApiServices
import javax.inject.Inject

class CategoriesRepository @Inject constructor(private val api: ApiServices) {
    suspend fun getCategoriesList() = api.getCategoriesList()
}